import axios from 'axios';

// Create axios instance with base configuration
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for adding auth tokens if needed
api.interceptors.request.use(
  (config) => {
    // Add auth token if available
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    // Don't override Content-Type for multipart/form-data requests
    // Let the browser set the boundary automatically
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type'];
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for handling errors globally
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Handle unauthorized access
      localStorage.removeItem('authToken');
      window.location.href = '/';
    }
    return Promise.reject(error);
  }
);

// Caregiver API calls
export const caregiverService = {
  // Create new caregiver application
  create: (caregiverData) => api.post('/caregivers', caregiverData),
  
  // Get caregiver by ID
  getById: (id) => api.get(`/caregivers/${id}`),
  
  // Get all caregivers (for testing)
  getAll: () => api.get('/caregivers'),
  
  // Update caregiver application
  update: (id, data) => api.put(`/caregivers/${id}`, data),
  
  // Delete caregiver application
  delete: (id) => api.delete(`/caregivers/${id}`),
  
  // Upload files for caregiver
  uploadFiles: (id, files) => {
    const formData = new FormData();
    Array.from(files).forEach(file => {
      formData.append('files', file);
    });
    
    return api.post(`/caregivers/${id}/files`, formData, {
      onUploadProgress: (progressEvent) => {
        const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
        return percentCompleted;
      },
    });
  },
  
  // Export caregiver data as PDF
  exportPdf: (id) => api.get(`/caregivers/${id}/export`, {
    responseType: 'blob',
  }),
  
  // Get QR code for caregiver
  getQrCode: (id) => api.get(`/caregivers/${id}/qrcode`, {
    responseType: 'blob',
  }),
};

// Admin API calls
export const adminService = {
  // Login admin
  login: (credentials) => api.post('/admin/login', credentials),
  
  // Get all caregivers for admin view
  getCaregivers: (filters = {}) => {
    const params = new URLSearchParams();
    Object.entries(filters).forEach(([key, value]) => {
      if (value) params.append(key, value);
    });
    const queryString = params.toString();
    return api.get(`/admin/caregivers${queryString ? '?' + queryString : ''}`);
  },
  
  // Update file status
  updateFileStatus: (fileId, status) => api.post(`/admin/files/${fileId}/status`, { status }),
  
  // Verify caregiver application
  verifyCaregiver: (id) => api.post(`/admin/caregivers/${id}/verify`),
  
  // Reject caregiver application
  rejectCaregiver: (id) => api.post(`/admin/caregivers/${id}/reject`),
  
  // Update caregiver status
  updateCaregiverStatus: (id, status) => api.post(`/admin/caregivers/${id}/status`, { status }),
  
  // Delete caregiver application
  deleteCaregiver: (id) => api.delete(`/admin/caregivers/${id}`),
  
  // Download document
  downloadDocument: (documentId) => api.get(`/admin/documents/${documentId}/download`, {
    responseType: 'blob',
  }),
  
  // Get dashboard statistics
  getDashboardStats: async () => {
    try {
      const response = await api.get('/admin/caregivers');
      const caregivers = response.data;
      
      // Calculate statistics from caregiver data
      const stats = {
        totalApplications: caregivers.length,
        pendingReview: caregivers.filter(c => c.status === 'PENDING' || c.status === 'UNDER_REVIEW').length,
        approved: caregivers.filter(c => c.status === 'APPROVED' || c.status === 'VERIFIED').length,
        rejected: caregivers.filter(c => c.status === 'REJECTED').length,
        activeCaregivers: caregivers.filter(c => c.status === 'VERIFIED' || c.status === 'APPROVED').length
      };
      
      return { data: stats };
    } catch (error) {
      console.error('Error calculating dashboard stats:', error);
      throw error;
    }
  },
};

// File API calls
export const fileService = {
  // Download file by ID
  download: (fileId) => api.get(`/files/${fileId}`, {
    responseType: 'blob',
  }),
};

// Notification API calls
export const notificationService = {
  // Send email notification
  sendEmail: (emailData) => api.post('/notifications/email', emailData),
};

export default api;
