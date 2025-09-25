import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import HomePage from './pages/HomePage/HomePage';
import BookingPage from './pages/BookingPage/Booking';
import PaymentPage from './pages/PaymentGateawayPage/Payment';
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
          {/* Add routes for Payment, Success, Failure later */}
        </Routes>
      </Router>
    </ThemeProvider>
  );
}

export default App;