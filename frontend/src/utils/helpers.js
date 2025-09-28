// File upload utilities
export const fileUtils = {
  // Validate file size (max 10MB per file)
  validateFileSize: (file, maxSizeMB = 10) => {
    const maxSizeBytes = maxSizeMB * 1024 * 1024;
    return file.size <= maxSizeBytes;
  },

  // Validate file type
  validateFileType: (file, allowedTypes = ['pdf', 'doc', 'docx', 'jpg', 'jpeg', 'png']) => {
    const fileExtension = file.name.split('.').pop().toLowerCase();
    return allowedTypes.includes(fileExtension);
  },

  // Format file size for display
  formatFileSize: (bytes) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  },

  // Generate unique file ID
  generateFileId: () => {
    return Date.now().toString(36) + Math.random().toString(36).substr(2);
  },

  // Download blob as file
  downloadBlob: (blob, filename) => {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  },
};

// Date formatting utilities
export const dateUtils = {
  // Format date for display
  formatDate: (date) => {
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  },

  // Format date and time for display
  formatDateTime: (date) => {
    return new Date(date).toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  },

  // Check if date is today
  isToday: (date) => {
    const today = new Date();
    const checkDate = new Date(date);
    return today.toDateString() === checkDate.toDateString();
  },
};

// Form validation utilities
export const validationUtils = {
  // Validate email format
  isValidEmail: (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  },

  // Validate phone number (optional validation)
  isValidPhone: (phone) => {
    const phoneRegex = /^[\+]?[1-9][\d]{0,15}$/;
    return phoneRegex.test(phone.replace(/[\s\-\(\)]/g, ''));
  },

  // Validate date of birth (must be in the past)
  isValidDateOfBirth: (dateOfBirth) => {
    if (!dateOfBirth) return false;
    const dobDate = new Date(dateOfBirth);
    const today = new Date();
    
    // Check if the date is valid
    if (isNaN(dobDate.getTime())) return false;
    
    // Check if the date is in the past
    return dobDate < today;
  },

  // Validate required fields
  validateRequired: (fields) => {
    const errors = {};
    Object.entries(fields).forEach(([key, value]) => {
      if (!value || (typeof value === 'string' && value.trim() === '')) {
        errors[key] = `${key.charAt(0).toUpperCase() + key.slice(1)} is required`;
      }
    });
    return errors;
  },
};

// Status utilities
export const statusUtils = {
  // Get status color for UI
  getStatusColor: (status) => {
    const colors = {
      PENDING: 'text-yellow-600 bg-yellow-100',
      VERIFIED: 'text-green-600 bg-green-100',
      REJECTED: 'text-red-600 bg-red-100',
      UNDER_REVIEW: 'text-blue-600 bg-blue-100',
    };
    return colors[status] || 'text-gray-600 bg-gray-100';
  },

  // Get status display text
  getStatusText: (status) => {
    const texts = {
      PENDING: 'Pending Review',
      VERIFIED: 'Verified',
      REJECTED: 'Rejected',
      UNDER_REVIEW: 'Under Review',
    };
    return texts[status] || status;
  },
};

// Local storage utilities
export const storageUtils = {
  // Set item in localStorage with expiration
  setWithExpiry: (key, value, ttl) => {
    const now = new Date();
    const item = {
      value: value,
      expiry: now.getTime() + ttl,
    };
    localStorage.setItem(key, JSON.stringify(item));
  },

  // Get item from localStorage with expiration check
  getWithExpiry: (key) => {
    const itemStr = localStorage.getItem(key);
    if (!itemStr) return null;

    const item = JSON.parse(itemStr);
    const now = new Date();

    if (now.getTime() > item.expiry) {
      localStorage.removeItem(key);
      return null;
    }
    return item.value;
  },
};

// Error handling utilities
export const errorUtils = {
  // Extract error message from API response
  getErrorMessage: (error) => {
    if (error.response?.data?.message) {
      return error.response.data.message;
    }
    if (error.response?.data?.error) {
      return error.response.data.error;
    }
    if (error.message) {
      return error.message;
    }
    return 'An unexpected error occurred';
  },

  // Check if error is network related
  isNetworkError: (error) => {
    return !error.response && error.request;
  },
};
