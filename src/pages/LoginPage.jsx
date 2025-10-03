import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

function LoginPage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    userType: 'caregiver' // caregiver or admin
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const handleInputChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    setError(''); // Clear error on input change
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    try {
      // Call authentication API
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email: formData.email,
          password: formData.password
        }),
      });

      const data = await response.json();
      
      if (data.success) {
        // Store user data in localStorage
        const userData = {
          ...data.user,
          isAuthenticated: true,
          loginTime: new Date().toISOString()
        };
        localStorage.setItem('user', JSON.stringify(userData));

        // Redirect based on user role
        if (data.user.role === 'ADMIN') {
          navigate('/admin-dashboard');
        } else if (data.user.role === 'CAREGIVER') {
          navigate('/');
        } else {
          navigate('/');
        }
      } else {
        setError(data.message || 'Login failed. Please try again.');
      }
    } catch (err) {
      console.error('Login error:', err);
      setError('Network error. Please check your connection and try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
      <div className="max-w-md w-full">
        {/* Logo/Header */}
        <div className="text-center mb-8">
          <h1 className="text-4xl font-bold text-indigo-600 mb-2">🏥 CareNet</h1>
          <p className="text-gray-600 text-lg">Welcome back! Please sign in to your account.</p>
        </div>

        {/* Login Form */}
        <div className="bg-white rounded-lg shadow-lg p-8">
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* User Type Selection */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Login as:
              </label>
              <div className="grid grid-cols-2 gap-3">
                <button
                  type="button"
                  onClick={() => setFormData({...formData, userType: 'caregiver'})}
                  className={`p-3 rounded-lg border-2 transition-all ${
                    formData.userType === 'caregiver' 
                      ? 'border-blue-500 bg-blue-50 text-blue-700' 
                      : 'border-gray-300 hover:border-gray-400'
                  }`}
                >
                  <div className="text-center">
                    <span className="block text-2xl mb-1">👩‍⚕️</span>
                    <span className="text-sm font-medium">Caregiver</span>
                  </div>
                </button>
                <button
                  type="button"
                  onClick={() => setFormData({...formData, userType: 'admin'})}
                  className={`p-3 rounded-lg border-2 transition-all ${
                    formData.userType === 'admin' 
                      ? 'border-indigo-500 bg-indigo-50 text-indigo-700' 
                      : 'border-gray-300 hover:border-gray-400'
                  }`}
                >
                  <div className="text-center">
                    <span className="block text-2xl mb-1">👨‍💼</span>
                    <span className="text-sm font-medium">Admin</span>
                  </div>
                </button>
              </div>
            </div>

            {/* Email Input */}
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-2">
                Email Address
              </label>
              <input
                type="email"
                id="email"
                name="email"
                value={formData.email}
                onChange={handleInputChange}
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="Enter your email"
              />
            </div>

            {/* Password Input */}
            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-2">
                Password
              </label>
              <input
                type="password"
                id="password"
                name="password"
                value={formData.password}
                onChange={handleInputChange}
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="Enter your password"
              />
            </div>

            {/* Error Message */}
            {error && (
              <div className="p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">
                {error}
              </div>
            )}

            {/* Submit Button */}
            <button
              type="submit"
              disabled={isLoading}
              className={`w-full py-3 px-4 rounded-lg font-medium text-white transition-colors ${
                isLoading 
                  ? 'bg-gray-400 cursor-not-allowed' 
                  : formData.userType === 'admin'
                    ? 'bg-indigo-600 hover:bg-indigo-700'
                    : 'bg-blue-600 hover:bg-blue-700'
              }`}
            >
              {isLoading ? (
                <span className="flex items-center justify-center text">
                  <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Signing in...
                </span>
              ) : (
                `Sign in as ${formData.userType === 'admin' ? 'Admin' : 'Caregiver'}`
              )}
            </button>
          </form>

          {/* Demo Credentials */}
          <div className="mt-6 p-4 bg-gray-50 rounded-lg">
            <h4 className="text-sm font-semibold text-gray-700 mb-2">Demo Credentials:</h4>
            <div className="text-xs text-gray-600 space-y-1">
              <div><strong>Admin:</strong> admin@carenet.com / admin123</div>
              <div><strong>Caregiver:</strong> caregiver@demo.com / password123</div>
            </div>
          </div>

         
        </div>
      </div>
    </div>
  );
}

export default LoginPage;
