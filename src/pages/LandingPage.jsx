import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Button } from '../components/UI';

function LandingPage() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const navigate = useNavigate();

  // Get user data from localStorage
  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const userType = user.userType || 'caregiver';

  const scrollToSection = (sectionId) => {
    const element = document.getElementById(sectionId);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' });
    }
    setMobileMenuOpen(false);
  };

  const handleLogout = () => {
    localStorage.removeItem('user');
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
      {/* Enhanced Navigation Bar */}
      <header className="bg-white shadow-lg sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-4">
            {/* Logo */}
            <div className="flex items-center">
              <div className="w-10 h-10 bg-primary-500 rounded-lg flex items-center justify-center mr-3">
                <span className="text-white font-bold text-lg">C</span>
              </div>
              <h1 className="text-2xl font-bold text-gray-900">CareNet</h1>
            </div>
            
            {/* Desktop Navigation */}
            <nav className="hidden md:flex items-center space-x-8">
              <button 
                onClick={() => scrollToSection('home')}
                className="text-gray-600 hover:text-primary-600 transition-colors font-medium"
              >
                Home
              </button>
              <button 
                onClick={() => scrollToSection('features')}
                className="text-gray-600 hover:text-primary-600 transition-colors font-medium"
              >
                Features
              </button>
              <button 
                onClick={() => scrollToSection('about')}
                className="text-gray-600 hover:text-primary-600 transition-colors font-medium"
              >
                About
              </button>
              
              {/* User Type Specific Links */}
              {userType === 'admin' ? (
                <Link 
                  to="/admin-dashboard" 
                  className="text-gray-600 hover:text-primary-600 transition-colors font-medium"
                >
                  Admin Panel
                </Link>
              ) : (
                <Link 
                  to="/dashboard" 
                  className="text-gray-600 hover:text-primary-600 transition-colors font-medium"
                >
                  Dashboard
                </Link>
              )}
              
              <Link to="/apply">
                <Button className="px-6 py-2">
                  Apply Now
                </Button>
              </Link>
              
              {/* User Info & Logout */}
              <div className="flex items-center space-x-3">
                <span className="text-sm text-gray-600">
                  {userType === 'admin' ? '👨‍💼 Admin' : '👩‍⚕️ Caregiver'}
                </span>
                <button
                  onClick={handleLogout}
                  className="text-red-600 hover:text-red-800 font-medium text-sm"
                >
                  Logout
                </button>
              </div>
            </nav>

            {/* Mobile Menu Button */}
            <button 
              className="md:hidden text-gray-600 hover:text-gray-900 focus:outline-none"
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                {mobileMenuOpen ? (
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                ) : (
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
                )}
              </svg>
            </button>
          </div>

          {/* Mobile Navigation Menu */}
          {mobileMenuOpen && (
            <div className="md:hidden py-4 border-t border-gray-200">
              <div className="flex flex-col space-y-4">
                <button 
                  onClick={() => scrollToSection('home')}
                  className="text-gray-600 hover:text-primary-600 transition-colors font-medium text-left"
                >
                  Home
                </button>
                <button 
                  onClick={() => scrollToSection('features')}
                  className="text-gray-600 hover:text-primary-600 transition-colors font-medium text-left"
                >
                  Features
                </button>
                <button 
                  onClick={() => scrollToSection('about')}
                  className="text-gray-600 hover:text-primary-600 transition-colors font-medium text-left"
                >
                  About
                </button>
                
                {/* User Type Specific Links */}
                {userType === 'admin' ? (
                  <Link 
                    to="/admin-dashboard" 
                    className="text-gray-600 hover:text-primary-600 transition-colors font-medium"
                    onClick={() => setMobileMenuOpen(false)}
                  >
                    Admin Panel
                  </Link>
                ) : (
                  <Link 
                    to="/dashboard" 
                    className="text-gray-600 hover:text-primary-600 transition-colors font-medium"
                    onClick={() => setMobileMenuOpen(false)}
                  >
                    Dashboard
                  </Link>
                )}
                
                <Link to="/apply" onClick={() => setMobileMenuOpen(false)}>
                  <Button className="w-full">
                    Apply Now
                  </Button>
                </Link>
                
                {/* User Info & Logout */}
                <div className="pt-2 border-t border-gray-200">
                  <div className="text-sm text-gray-600 mb-2">
                    {userType === 'admin' ? '👨‍💼 Admin User' : '👩‍⚕️ Caregiver User'}
                  </div>
                  <button
                    onClick={handleLogout}
                    className="text-red-600 hover:text-red-800 font-medium text-sm"
                  >
                    Logout
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
      </header>

      {/* Hero Section */}
      <section id="home" className="py-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-4xl md:text-6xl font-bold text-gray-900 mb-6">
            Welcome to <span className="text-primary-500">CareNet</span>
          </h2>
          <p className="text-xl text-gray-600 mb-8 max-w-3xl mx-auto">
            Your trusted platform for connecting qualified caregivers with families who need care. 
            Join our verified network of professional caregivers today.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Link to="/apply">
              <Button size="large" className="px-8 py-4 text-lg">
                🏥 Enroll as a Caregiver
              </Button>
            </Link>
           
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section id="features" className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h3 className="text-3xl font-bold text-gray-900 mb-4">Why Choose CareNet?</h3>
            <p className="text-lg text-gray-600">We make it easy to become a verified caregiver</p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="text-center p-6">
              <div className="w-16 h-16 bg-primary-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-2xl">📋</span>
              </div>
              <h4 className="text-xl font-semibold text-gray-900 mb-2">Simple Application</h4>
              <p className="text-gray-600">
                Easy online application process with document upload and progress tracking.
              </p>
            </div>
            
            <div className="text-center p-6">
              <div className="w-16 h-16 bg-success-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-2xl">✅</span>
              </div>
              <h4 className="text-xl font-semibold text-gray-900 mb-2">Quick Verification</h4>
              <p className="text-gray-600">
                Fast document verification process with real-time status updates.
              </p>
            </div>
            
            <div className="text-center p-6">
              <div className="w-16 h-16 bg-warning-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-2xl">📱</span>
              </div>
              <h4 className="text-xl font-semibold text-gray-900 mb-2">Digital Credentials</h4>
              <p className="text-gray-600">
                Get your QR code and digital certificate upon verification completion.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* About Section */}
      <section id="about" className="py-16 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h3 className="text-3xl font-bold text-gray-900 mb-4">About CareNet</h3>
          </div>
          
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
            <div>
              <h4 className="text-2xl font-semibold text-gray-900 mb-4">
                Connecting Care with Trust
              </h4>
              <p className="text-gray-600 mb-4">
                CareNet is a comprehensive platform designed to streamline the caregiver verification 
                process. We ensure that only qualified, verified professionals join our network.
              </p>
              <ul className="space-y-3 text-gray-600">
                <li className="flex items-start">
                  <span className="text-green-500 mr-2">✓</span>
                  Comprehensive background verification
                </li>
                <li className="flex items-start">
                  <span className="text-green-500 mr-2">✓</span>
                  Digital document management
                </li>
                <li className="flex items-start">
                  <span className="text-green-500 mr-2">✓</span>
                  Real-time application tracking
                </li>
                <li className="flex items-start">
                  <span className="text-green-500 mr-2">✓</span>
                  Secure credential system
                </li>
              </ul>
            </div>
            
            <div className="text-center">
              <div className="bg-primary-500 rounded-lg p-8 text-white">
                <h5 className="text-2xl font-bold mb-4">Ready to Get Started?</h5>
                <p className="mb-6">Join thousands of verified caregivers in our network</p>
                <Link to="/apply">
                  <Button variant="secondary" size="large">
                    Start Your Application
                  </Button>
                </Link>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-gray-900 text-white py-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center">
            <h5 className="text-lg font-semibold mb-2">CareNet</h5>
            <p className="text-gray-400 text-sm">
              © 2025 CareNet. All rights reserved. Connecting qualified caregivers with families who need care.
            </p>
          </div>
        </div>
      </footer>
    </div>
  );
}

export default LandingPage;
