import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import QRCodeModal from '../components/QRCodeModal';

function CaregiverDashboard() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedApplication, setSelectedApplication] = useState(null);
  const [showDocumentsModal, setShowDocumentsModal] = useState(false);
  const [showQRModal, setShowQRModal] = useState(false);
  const [caregiverData, setCaregiverData] = useState(null);

  useEffect(() => {
    // Check if user is logged in
    const userData = localStorage.getItem('user');
    if (!userData) {
      navigate('/login');
      return;
    }

    const parsedUser = JSON.parse(userData);
    if (parsedUser.role !== 'CAREGIVER') {
      navigate('/login');
      return;
    }

    setUser(parsedUser);
    fetchDashboardData(parsedUser.id);
  }, [navigate]);

  const fetchDashboardData = async (userId) => {
    try {
      setLoading(true);
      
      // Get dashboard data for the logged-in user
      const response = await fetch('http://localhost:8080/api/caregivers/dashboard', {
        headers: {
          'User-ID': userId.toString(),
        },
      });
      
      if (response.ok) {
        const data = await response.json();
        setDashboardData(data);
      } else {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Failed to load dashboard data');
      }
    } catch (err) {
      console.error('Dashboard error:', err);
      setError('Failed to load dashboard data: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('user');
    navigate('/login');
  };

  const getStatusColor = (status) => {
    switch (status?.toUpperCase()) {
      case 'APPROVED':
        return 'bg-green-100 text-green-800 border-green-200';
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      case 'REJECTED':
        return 'bg-red-100 text-red-800 border-red-200';
      default:
        return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  const getStatusIcon = (status) => {
    switch (status?.toUpperCase()) {
      case 'APPROVED':
        return '✅';
      case 'PENDING':
        return '⏳';
      case 'REJECTED':
        return '❌';
      default:
        return '📄';
    }
  };

  const getCaregiverTypeDisplay = (type) => {
    switch (type) {
      case 'ELDER_CARE':
        return { icon: '🧓', name: 'Elder Care' };
      case 'CHILD_CARE':
        return { icon: '👶', name: 'Child Care' };
      case 'PET_CARE':
        return { icon: '🐾', name: 'Pet Care' };
      case 'HUMAN_CARE':
        return { icon: '👩‍⚕️', name: 'Human Care (Legacy)' };
      default:
        return { icon: '📋', name: 'Care' };
    }
  };

  const getDocumentTypeDisplay = (type) => {
    switch (type) {
      case 'ID':
        return { name: 'Identification Document', icon: '🆔', color: 'blue' };
      case 'MEDICAL':
        return { name: 'Medical Certificate', icon: '🏥', color: 'red' };
      case 'BACKGROUND':
        return { name: 'Background Check', icon: '🔍', color: 'purple' };
      case 'TRAINING':
        return { name: 'Training Certificate', icon: '📜', color: 'green' };
      case 'INSURANCE':
        return { name: 'Insurance Document', icon: '🛡️', color: 'indigo' };
      case 'REFERENCE':
        return { name: 'Reference Letter', icon: '📝', color: 'orange' };
      case 'DOCUMENT':
        return { name: 'General Document', icon: '📄', color: 'gray' };
      case 'OTHER':
      default:
        return { name: 'Other Document', icon: '📁', color: 'gray' };
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  const deleteApplication = async (applicationId) => {
    if (!confirm('Are you sure you want to delete this application? This action cannot be undone.')) {
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/api/caregivers/${applicationId}`, {
        method: 'DELETE',
        headers: {
          'User-ID': user.id.toString(),
        },
      });

      if (response.ok) {
        // Refresh dashboard data
        fetchDashboardData(user.id);
      } else {
        const errorData = await response.json();
        alert('Error deleting application: ' + (errorData.error || 'Unknown error'));
      }
    } catch (err) {
      console.error('Delete error:', err);
      alert('Failed to delete application: ' + err.message);
    }
  };

  const handleViewDocuments = (application) => {
    setSelectedApplication(application);
    setShowDocumentsModal(true);
  };

  const closeDocumentsModal = () => {
    setShowDocumentsModal(false);
    setSelectedApplication(null);
  };

  const handleExportPDF = async (applicationId) => {
    try {
      const response = await fetch(`http://localhost:8080/api/caregivers/${applicationId}/export`, {
        headers: {
          'User-ID': user.id.toString(),
        },
      });

      if (response.ok) {
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.style.display = 'none';
        a.href = url;
        a.download = `caregiver-application-${applicationId}.pdf`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
      } else {
        const errorData = await response.json();
        alert('Error exporting PDF: ' + (errorData.error || 'Unknown error'));
      }
    } catch (err) {
      console.error('PDF Export error:', err);
      alert('Failed to export PDF: ' + err.message);
    }
  };

  const handleDownloadDocument = async (documentId, fileName) => {
    try {
      const response = await fetch(`http://localhost:8080/api/admin/documents/${documentId}/download`, {
        headers: {
          'User-ID': user.id.toString(),
        },
      });

      if (response.ok) {
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.style.display = 'none';
        a.href = url;
        a.download = fileName || `document-${documentId}`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
      } else {
        alert('Error downloading document. Please try again.');
      }
    } catch (err) {
      console.error('Download error:', err);
      alert('Failed to download document: ' + err.message);
    }
  };

  const handleViewQR = async (application) => {
    try {
      console.log('Generating QR for application:', application);
      
      // Use the application data directly since it's already loaded
      setCaregiverData({
        name: application.name || `${user.firstName} ${user.lastName}`,
        id: application.id,
        careType: application.caregiverType,
        email: application.email || user.email,
        registrationDate: application.applicationDate || new Date().toLocaleDateString()
      });
      setShowQRModal(true);
    } catch (error) {
      console.error('Error generating QR code:', error);
      alert('Error generating QR code: ' + error.message);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading your dashboard...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center max-w-md mx-auto p-6">
          <div className="text-red-600 text-6xl mb-4">❌</div>
          <h2 className="text-2xl font-bold text-gray-900 mb-2">Error Loading Dashboard</h2>
          <p className="text-gray-600 mb-4">{error}</p>
          <div className="space-x-4">
            <button
              onClick={() => fetchDashboardData(user?.id)}
              className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
            >
              Try Again
            </button>
            <button
              onClick={() => navigate('/')}
              className="bg-gray-600 text-white px-4 py-2 rounded hover:bg-gray-700"
            >
              Go Home
            </button>
          </div>
        </div>
      </div>
    );
  }

  const applications = dashboardData?.applications || [];
  const stats = dashboardData?.stats || { total: 0, pending: 0, approved: 0, rejected: 0 };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-4">
            <div>
              <h1 className="text-2xl font-bold text-gray-900">Caregiver Dashboard</h1>
              <p className="text-sm text-gray-600">
                Welcome back, {user?.firstName} {user?.lastName}
              </p>
            </div>
            <div className="flex items-center space-x-4">
              <Link
                to="/apply"
                className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
              >
                + New Application
              </Link>
              <Link
                to="/"
                className="text-gray-600 hover:text-gray-900 px-3 py-2"
              >
                Home
              </Link>
              <button
                onClick={handleLogout}
                className="text-red-600 hover:text-red-800 px-3 py-2"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Stats Overview */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <span className="text-2xl">📋</span>
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-500">Total Applications</p>
                <p className="text-2xl font-semibold text-gray-900">{stats.total}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <span className="text-2xl">⏳</span>
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-500">Pending Review</p>
                <p className="text-2xl font-semibold text-yellow-600">{stats.pending}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <span className="text-2xl">✅</span>
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-500">Approved</p>
                <p className="text-2xl font-semibold text-green-600">{stats.approved}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <span className="text-2xl">❌</span>
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-500">Rejected</p>
                <p className="text-2xl font-semibold text-red-600">{stats.rejected}</p>
              </div>
            </div>
          </div>
        </div>

        {/* Applications List */}
        <div className="bg-white rounded-lg shadow">
          <div className="px-6 py-4 border-b border-gray-200">
            <h2 className="text-lg font-semibold text-gray-900">My Applications</h2>
            <p className="text-sm text-gray-600">Track the status of your caregiver applications</p>
          </div>

          {applications.length === 0 ? (
            <div className="text-center py-12">
              <span className="text-6xl mb-4 block">📝</span>
              <h3 className="text-lg font-medium text-gray-900 mb-2">No Applications Yet</h3>
              <p className="text-gray-600 mb-6">Start your journey as a caregiver by submitting your first application.</p>
              <Link
                to="/apply"
                className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition-colors inline-block"
              >
                Submit Application
              </Link>
            </div>
          ) : (
            <div className="divide-y divide-gray-200">
              {applications.map((application) => {
                const typeInfo = getCaregiverTypeDisplay(application.caregiverType);
                return (
                  <div key={application.id} className="p-6 hover:bg-gray-50">
                    <div className="flex items-center justify-between">
                      <div className="flex items-start space-x-4">
                        <div className="flex-shrink-0">
                          <span className="text-3xl">{typeInfo.icon}</span>
                        </div>
                        <div className="flex-1 min-w-0">
                          <div className="flex items-center space-x-3 mb-2">
                            <h3 className="text-lg font-medium text-gray-900">
                              {typeInfo.name} Application
                            </h3>
                            <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border ${getStatusColor(application.status)}`}>
                              {getStatusIcon(application.status)} {application.status}
                            </span>
                          </div>
                          <div className="text-sm text-gray-600 space-y-1">
                            <p><strong>Application ID:</strong> #{application.id}</p>
                            <p><strong>Submitted:</strong> {formatDate(application.applicationDate)}</p>
                            {application.specializations && (
                              <p><strong>Specializations:</strong> {application.specializations}</p>
                            )}
                            {application.reviewedAt && (
                              <p><strong>Reviewed:</strong> {formatDate(application.reviewedAt)}</p>
                            )}
                            {application.notes && (
                              <p><strong>Notes:</strong> {application.notes}</p>
                            )}
                            {/* Document Status Summary */}
                            {application.documents && application.documents.length > 0 && (
                              <div className="mt-2">
                                <p><strong>Document Status:</strong></p>
                                <div className="flex flex-wrap gap-1 mt-1">
                                  {application.documents.map((doc, index) => (
                                    <span
                                      key={doc.id || index}
                                      className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium border ${
                                        doc.status === 'APPROVED' ? 'bg-green-100 text-green-800 border-green-200' :
                                        doc.status === 'REJECTED' ? 'bg-red-100 text-red-800 border-red-200' :
                                        'bg-yellow-100 text-yellow-800 border-yellow-200'
                                      }`}
                                      title={`${doc.fileName}: ${doc.status || 'PENDING'}`}
                                    >
                                      <span className="mr-1">
                                        {doc.status === 'APPROVED' ? '✅' :
                                         doc.status === 'REJECTED' ? '❌' : '⏳'}
                                      </span>
                                      <span>{getDocumentTypeDisplay(doc.documentType).name}</span>
                                    </span>
                                  ))}
                                </div>
                                <p className="text-xs text-gray-500 mt-1">
                                  {application.documents.filter(d => d.status === 'APPROVED').length} approved,{' '}
                                  {application.documents.filter(d => d.status === 'REJECTED').length} rejected,{' '}
                                  {application.documents.filter(d => !d.status || d.status === 'PENDING').length} pending
                                </p>
                              </div>
                            )}
                          </div>
                        </div>
                      </div>
                      
                      <div className="flex items-center space-x-2">
                        {application.status === 'APPROVED' && (
                          <button
                            onClick={() => handleViewQR(application)}
                            className="text-green-600 hover:text-green-700 px-3 py-1 text-sm border border-green-300 rounded hover:bg-green-50"
                          >
                            View QR
                          </button>
                        )}
                        <button
                          onClick={() => handleExportPDF(application.id)}
                          className="text-blue-600 hover:text-blue-700 px-3 py-1 text-sm border border-blue-300 rounded hover:bg-blue-50"
                        >
                          Export PDF
                        </button>
                        {application.documents && application.documents.length > 0 && (
                          <button
                            onClick={() => handleViewDocuments(application)}
                            className="text-indigo-600 hover:text-indigo-700 px-3 py-1 text-sm border border-indigo-300 rounded hover:bg-indigo-50"
                          >
                            View Documents
                          </button>
                        )}
                        {application.status === 'PENDING' && (
                          <button
                            onClick={() => deleteApplication(application.id)}
                            className="text-red-600 hover:text-red-700 px-3 py-1 text-sm border border-red-300 rounded hover:bg-red-50"
                          >
                            Delete
                          </button>
                        )}
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>

        {/* Quick Actions */}
        {applications.length > 0 && (
          <div className="mt-8 bg-blue-50 rounded-lg p-6">
            <h3 className="text-lg font-semibold text-blue-900 mb-4">Quick Actions</h3>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <Link
                to="/apply"
                className="flex items-center p-4 bg-white rounded-lg shadow-sm hover:shadow-md transition-shadow"
              >
                <span className="text-2xl mr-3">➕</span>
                <div>
                  <h4 className="font-medium text-gray-900">New Application</h4>
                  <p className="text-sm text-gray-600">Submit another caregiver application</p>
                </div>
              </Link>
              
              <button
                onClick={() => fetchDashboardData(user.id)}
                className="flex items-center p-4 bg-white rounded-lg shadow-sm hover:shadow-md transition-shadow"
              >
                <span className="text-2xl mr-3">🔄</span>
                <div>
                  <h4 className="font-medium text-gray-900">Refresh Status</h4>
                  <p className="text-sm text-gray-600">Check for application updates</p>
                </div>
              </button>
              
              <Link
                to="/"
                className="flex items-center p-4 bg-white rounded-lg shadow-sm hover:shadow-md transition-shadow"
              >
                <span className="text-2xl mr-3">🏠</span>
                <div>
                  <h4 className="font-medium text-gray-900">Back to Home</h4>
                  <p className="text-sm text-gray-600">Return to main page</p>
                </div>
              </Link>
            </div>
          </div>
        )}
      </main>

      {/* Documents Modal */}
      {showDocumentsModal && selectedApplication && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 max-w-4xl w-full mx-4 max-h-[90vh] overflow-y-auto">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-2xl font-bold text-gray-900">
                Documents for Application #{selectedApplication.id}
              </h2>
              <button
                onClick={closeDocumentsModal}
                className="text-gray-500 hover:text-gray-700 text-2xl font-bold"
              >
                ×
              </button>
            </div>

            {/* Application Details */}
            <div className="bg-gray-50 p-4 rounded-lg mb-6">
              <h3 className="text-lg font-semibold mb-3">Application Overview</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                <div><strong>Application ID:</strong> #{selectedApplication.id}</div>
                <div><strong>Type:</strong> {getCaregiverTypeDisplay(selectedApplication.caregiverType).name}</div>
                <div><strong>Status:</strong> 
                  <span className={`ml-2 px-2 py-1 rounded-full text-xs ${getStatusColor(selectedApplication.status)}`}>
                    {getStatusIcon(selectedApplication.status)} {selectedApplication.status}
                  </span>
                </div>
                <div><strong>Submitted:</strong> {formatDate(selectedApplication.applicationDate)}</div>
                {selectedApplication.reviewedAt && (
                  <div><strong>Reviewed:</strong> {formatDate(selectedApplication.reviewedAt)}</div>
                )}
                {selectedApplication.specializations && (
                  <div><strong>Specializations:</strong> {selectedApplication.specializations}</div>
                )}
              </div>
            </div>

            {/* Documents List */}
            <div>
              <h3 className="text-lg font-semibold mb-4">
                Submitted Documents ({selectedApplication.documents?.length || 0})
              </h3>
              
              {selectedApplication.documents && selectedApplication.documents.length > 0 ? (
                <div className="space-y-3">
                  {selectedApplication.documents.map((document, index) => (
                    <div key={document.id || index} className="border rounded-lg p-4 flex items-center justify-between">
                      <div className="flex-1">
                        <div className="flex items-center space-x-3">
                          <div className="text-blue-500 text-2xl">
                            {getDocumentTypeDisplay(document.documentType).icon}
                          </div>
                          <div>
                            <p className="font-medium text-gray-900">
                              {document.fileName || `${getDocumentTypeDisplay(document.documentType).name}` || `Document ${index + 1}`}
                            </p>
                            <div className="flex items-center space-x-4 text-sm text-gray-600 mt-1">
                              <span><strong>Type:</strong> {getDocumentTypeDisplay(document.documentType).name}</span>
                              <span><strong>Status:</strong> 
                                <span className={`ml-1 px-2 py-1 rounded text-xs font-medium ${
                                  document.status === 'APPROVED' ? 'bg-green-100 text-green-800' :
                                  document.status === 'REJECTED' ? 'bg-red-100 text-red-800' :
                                  'bg-yellow-100 text-yellow-800'
                                }`}>
                                  {document.status === 'APPROVED' ? '✅ Approved' :
                                   document.status === 'REJECTED' ? '❌ Rejected' : '⏳ Pending Review'}
                                </span>
                              </span>
                              {document.fileSize && (
                                <span><strong>Size:</strong> {(document.fileSize / 1024).toFixed(1)} KB</span>
                              )}
                            </div>
                            {document.uploadedAt && (
                              <p className="text-xs text-gray-400 mt-1">
                                Uploaded: {formatDate(document.uploadedAt)}
                              </p>
                            )}
                          </div>
                        </div>
                        {document.status === 'REJECTED' && (
                          <div className="mt-2 p-2 bg-red-50 border border-red-200 rounded">
                            <p className="text-sm text-red-700">
                              <strong>Rejection Notice:</strong> This document was rejected and needs to be resubmitted or replaced.
                            </p>
                          </div>
                        )}
                      </div>
                      
                      <div className="flex space-x-2 ml-4">
                        <button
                          onClick={() => handleDownloadDocument(document.id, document.fileName)}
                          className="bg-blue-500 hover:bg-blue-600 text-white px-3 py-2 rounded-md text-sm disabled:opacity-50"
                          disabled={!document.id}
                        >
                          {document.id ? 'Download' : 'Unavailable'}
                        </button>
                      </div>
                    </div>
                  ))}
                  
                  {/* Summary */}
                  <div className="mt-4 p-4 bg-blue-50 rounded-lg">
                    <h4 className="font-semibold text-blue-900 mb-2">Document Review Summary</h4>
                    <div className="grid grid-cols-3 gap-4 text-sm">
                      <div className="text-center">
                        <div className="text-2xl text-green-600 font-bold">
                          {selectedApplication.documents.filter(d => d.status === 'APPROVED').length}
                        </div>
                        <div className="text-green-700">Approved</div>
                      </div>
                      <div className="text-center">
                        <div className="text-2xl text-red-600 font-bold">
                          {selectedApplication.documents.filter(d => d.status === 'REJECTED').length}
                        </div>
                        <div className="text-red-700">Rejected</div>
                      </div>
                      <div className="text-center">
                        <div className="text-2xl text-yellow-600 font-bold">
                          {selectedApplication.documents.filter(d => !d.status || d.status === 'PENDING').length}
                        </div>
                        <div className="text-yellow-700">Pending</div>
                      </div>
                    </div>
                    {selectedApplication.documents.some(d => d.status === 'REJECTED') && (
                      <div className="mt-3 p-3 bg-red-100 border border-red-300 rounded">
                        <p className="text-sm text-red-800">
                          <strong>Action Required:</strong> Some documents have been rejected. 
                          Please resubmit or replace rejected documents to proceed with your application.
                        </p>
                      </div>
                    )}
                  </div>
                </div>
              ) : (
                <div className="text-center py-8 text-gray-500">
                  <span className="text-6xl mb-4 block">📄</span>
                  <p>No documents uploaded for this application</p>
                </div>
              )}
            </div>

            <div className="mt-6 flex justify-end space-x-3">
              <button
                onClick={() => handleExportPDF(selectedApplication.id)}
                className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg"
              >
                Export Application PDF
              </button>
              <button
                onClick={closeDocumentsModal}
                className="bg-gray-500 hover:bg-gray-600 text-white px-4 py-2 rounded-lg"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      {/* QR Code Modal */}
      <QRCodeModal
        isOpen={showQRModal}
        onClose={() => setShowQRModal(false)}
        caregiverData={caregiverData}
      />
    </div>
  );
}

export default CaregiverDashboard;
