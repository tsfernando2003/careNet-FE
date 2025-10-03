import { BrowserRouter as Router, Routes, Route ,Navigate} from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import HomePage from './pages/HomePage/HomePage';
import BookingPage from './pages/BookingPage/Booking';
import PaymentPage from './pages/PaymentGateawayPage/Payment';
import UserDashboardPage from './pages/UserDashBaordPage/UserDashboard';
import LoginPage from './pages/LoginPage.jsx';
import LandingPage from './pages/LandingPage.jsx';
import CaregiverApplicationForm from './pages/CaregiverApplicationForm.jsx';
import CaregiverDashboard from './pages/CaregiverDashboard.jsx';
import AdminDashboard from './pages/AdminDashboard.jsx';
import ProtectedRoute from './components/ProtectedRoute.jsx';


import './App.css';


const theme = createTheme({
  palette: {
    primary: { main: '#1976d2' }, 
    secondary: { main: '#f50057' }, 
  },
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <Router>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/booking" element={<BookingPage />} />
           <Route path="/payment" element={<PaymentPage />} />
            <Route path="/dashboard" element={<UserDashboardPage />} />
            <Route path="/login" element={<LoginPage />} />
          <Route path="/apply" element={<CaregiverApplicationForm />} />
          
          {/* Protected Routes */}
          <Route 
            path="/" 
            element={
              <ProtectedRoute>
                <LandingPage />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/dashboard" 
            element={
              <ProtectedRoute>
                <CaregiverDashboard />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/caregiver-dashboard" 
            element={
              <ProtectedRoute>
                <CaregiverDashboard />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/admin-dashboard" 
            element={
              <ProtectedRoute adminOnly={true}>
                <AdminDashboard />
              </ProtectedRoute>
            } 
          />
          
          {/* Redirect old admin route to new one */}
          <Route path="/admin" element={<Navigate to="/admin-dashboard" replace />} />
          
          {/* Default redirect to login if no user is logged in */}
          <Route path="*" element={<Navigate to="/login" replace />} />
          {/* Add routes for Payment, Success, Failure later */}
        </Routes>
      </Router>
    </ThemeProvider>
  );
}

export default App;