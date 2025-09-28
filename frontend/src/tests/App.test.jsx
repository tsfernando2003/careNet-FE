import { describe, it, expect } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import userEvent from '@testing-library/user-event';
import LandingPage from '../pages/LandingPage';
import CaregiverApplicationForm from '../pages/CaregiverApplicationForm';
import CaregiverDashboard from '../pages/CaregiverDashboard';
import AdminDashboard from '../pages/AdminDashboard';

// Mock react-router-dom
const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
    useParams: () => ({ id: '1' }),
  };
});

// Mock API services
vi.mock('../services/api', () => ({
  caregiverService: {
    create: vi.fn().mockResolvedValue({ data: { id: 1 } }),
    getById: vi.fn().mockResolvedValue({
      data: {
        id: 1,
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@example.com',
        phone: '123-456-7890',
        status: 'PENDING',
        documents: [],
        createdAt: '2025-01-01T00:00:00Z',
      },
    }),
    uploadFiles: vi.fn().mockResolvedValue({ data: 'success' }),
    exportPdf: vi.fn().mockResolvedValue({ data: new Blob() }),
    getQrCode: vi.fn().mockResolvedValue({ data: new Blob() }),
  },
  adminService: {
    getCaregivers: vi.fn().mockResolvedValue({ data: [] }),
    updateFileStatus: vi.fn().mockResolvedValue({ data: 'success' }),
    verifyCaregiver: vi.fn().mockResolvedValue({ data: 'success' }),
  },
  fileService: {
    download: vi.fn().mockResolvedValue({ data: new Blob() }),
  },
  notificationService: {
    sendEmail: vi.fn().mockResolvedValue({ data: 'success' }),
  },
}));

// Mock emailjs
vi.mock('emailjs-com', () => ({
  send: vi.fn().mockResolvedValue({ status: 200 }),
}));

const renderWithRouter = (component) => {
  return render(<BrowserRouter>{component}</BrowserRouter>);
};

describe('LandingPage', () => {
  it('renders welcome message', () => {
    renderWithRouter(<LandingPage />);
    expect(screen.getByText(/Welcome to CareNet/i)).toBeInTheDocument();
  });

  it('has navigation buttons', () => {
    renderWithRouter(<LandingPage />);
    expect(screen.getByText(/Enroll as a Caregiver/i)).toBeInTheDocument();
    expect(screen.getByText(/Admin Dashboard/i)).toBeInTheDocument();
  });

  it('displays features section', () => {
    renderWithRouter(<LandingPage />);
    expect(screen.getByText(/Why Choose CareNet/i)).toBeInTheDocument();
    expect(screen.getByText(/Simple Application/i)).toBeInTheDocument();
    expect(screen.getByText(/Quick Verification/i)).toBeInTheDocument();
  });
});

describe('CaregiverApplicationForm', () => {
  it('renders form steps', () => {
    renderWithRouter(<CaregiverApplicationForm />);
    expect(screen.getByText(/Personal Information/i)).toBeInTheDocument();
    expect(screen.getByText(/Caregiver Application/i)).toBeInTheDocument();
  });

  it('validates required fields', async () => {
    const user = userEvent.setup();
    renderWithRouter(<CaregiverApplicationForm />);
    
    const nextButton = screen.getByText('Next');
    await user.click(nextButton);
    
    await waitFor(() => {
      expect(screen.getByText(/First Name is required/i)).toBeInTheDocument();
    });
  });

  it('allows form submission with valid data', async () => {
    const user = userEvent.setup();
    renderWithRouter(<CaregiverApplicationForm />);
    
    // Fill out the form
    await user.type(screen.getByPlaceholderText(/first name/i), 'John');
    await user.type(screen.getByPlaceholderText(/last name/i), 'Doe');
    await user.type(screen.getByPlaceholderText(/email/i), 'john@example.com');
    await user.type(screen.getByPlaceholderText(/phone/i), '123-456-7890');
    
    const nextButton = screen.getByText('Next');
    await user.click(nextButton);
    
    // Should proceed to next step
    await waitFor(() => {
      expect(screen.getByText(/Address & Emergency Contact/i)).toBeInTheDocument();
    });
  });
});

describe('CaregiverDashboard', () => {
  it('renders dashboard with loading state initially', () => {
    renderWithRouter(<CaregiverDashboard />);
    expect(screen.getByText(/Loading your dashboard/i)).toBeInTheDocument();
  });

  it('displays caregiver information after loading', async () => {
    renderWithRouter(<CaregiverDashboard />);
    
    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
      expect(screen.getByText('john@example.com')).toBeInTheDocument();
      expect(screen.getByText('123-456-7890')).toBeInTheDocument();
    });
  });

  it('shows export PDF button', async () => {
    renderWithRouter(<CaregiverDashboard />);
    
    await waitFor(() => {
      expect(screen.getByText(/Export PDF Profile/i)).toBeInTheDocument();
    });
  });

  it('displays QR code section for verified users', async () => {
    // Mock verified user
    const { caregiverService } = await import('../services/api');
    caregiverService.getById.mockResolvedValue({
      data: {
        id: 1,
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@example.com',
        phone: '123-456-7890',
        status: 'VERIFIED',
        documents: [],
        createdAt: '2025-01-01T00:00:00Z',
      },
    });

    renderWithRouter(<CaregiverDashboard />);
    
    await waitFor(() => {
      expect(screen.getByText(/Download QR Code/i)).toBeInTheDocument();
    });
  });
});

describe('FileUploader', () => {
  // This would be in a separate file, but included here for completeness
  it('renders drop zone', () => {
    const { FileUploader } = require('../components/FileUploader');
    renderWithRouter(<FileUploader onFilesSelected={vi.fn()} />);
    expect(screen.getByText(/Drop files here or click to browse/i)).toBeInTheDocument();
  });
});

describe('AdminDashboard', () => {
  it('shows login modal initially', () => {
    renderWithRouter(<AdminDashboard />);
    expect(screen.getByText(/Admin Login/i)).toBeInTheDocument();
  });

  it('allows admin login with correct credentials', async () => {
    const user = userEvent.setup();
    renderWithRouter(<AdminDashboard />);
    
    await user.type(screen.getByLabelText(/username/i), 'admin');
    await user.type(screen.getByLabelText(/password/i), 'admin123');
    await user.click(screen.getByText('Sign In'));
    
    await waitFor(() => {
      expect(screen.getByText(/Admin Dashboard/i)).toBeInTheDocument();
    });
  });

  it('shows statistics after login', async () => {
    // Mock successful login state
    localStorage.setItem('adminToken', 'dummy-token');
    
    renderWithRouter(<AdminDashboard />);
    
    await waitFor(() => {
      expect(screen.getByText(/Total Applications/i)).toBeInTheDocument();
      expect(screen.getByText(/Pending/i)).toBeInTheDocument();
      expect(screen.getByText(/Verified/i)).toBeInTheDocument();
    });
  });
});
