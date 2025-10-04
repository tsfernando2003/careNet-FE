import { render, screen } from '@testing-library/react';
import LandingPage from './LandingPage';

test('renders welcome message and enroll button', () => {
  render(<LandingPage />);
  const welcomeElement = screen.getByText(/Welcome to CareNet/i);
  const enrollButton = screen.getByText(/Enroll me as a caregiver/i);
  expect(welcomeElement).toBeInTheDocument();
  expect(enrollButton).toBeInTheDocument();
});
