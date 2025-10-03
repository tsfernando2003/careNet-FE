import React, { useEffect, useState } from "react";
import axios from "axios";
import "./UserDashboard.css";

interface Booking {
  id: number;
  caregiverName: string;
  caregiverRole: string;
  rating: number;
  date: string;
  time: string;
  duration: number;
  cost: number;
  status: string;
}

interface Payment {
  id: number;
  bookingId: number;
  amount: number;
  method: string;
  date: string;
}

const UserDashboard: React.FC = () => {
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [payments, setPayments] = useState<Payment[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        // replace with your backend endpoints
        const bookingsRes = await axios.get("http://localhost:8081/api/bookings");
        const paymentsRes = await axios.get("http://localhost:8081/api/payments");
        setBookings(bookingsRes.data);
        setPayments(paymentsRes.data);
      } catch (err) {
        console.error("Error fetching dashboard data", err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) return <div className="loading">Loading dashboard...</div>;

  return (
    <div className="dashboard-container">
      <h2 className="dashboard-title">My Dashboard</h2>
      <p className="dashboard-subtitle">
        Manage your care bookings and track your healthcare journey
      </p>

      {/* Stats Section */}
      <div className="stats-grid">
        <div className="stats-card">
          <h3>{bookings.length}</h3>
          <p>Total Bookings</p>
        </div>
        <div className="stats-card">
          <h3>{bookings.filter((b) => b.status === "Upcoming").length}</h3>
          <p>Upcoming</p>
        </div>
        <div className="stats-card">
          <h3>{bookings.filter((b) => b.status === "Completed").length}</h3>
          <p>Completed</p>
        </div>
        <div className="stats-card">
          <h3>
            $
            {payments.reduce((sum, payment) => sum + (payment.amount || 0), 0)}
          </h3>
          <p>Total Spent</p>
        </div>
      </div>

      {/* Bookings List */}
      <div className="bookings-list">
        <h3 className="section-title">My Bookings</h3>
        {bookings.map((booking) => (
          <div key={booking.id} className="booking-card">
            <div className="booking-info">
              <div>
                <h4>{booking.caregiverName}</h4>
                <p className="role">{booking.caregiverRole}</p>
                <p className="rating">⭐ {booking.rating}</p>
              </div>
              <div>
                <p>
                  <strong>Date:</strong> {booking.date}
                </p>
                <p>
                  <strong>Time:</strong> {booking.time} ({booking.duration} hrs)
                </p>
                <p>
                  <strong>Cost:</strong> ${booking.cost}
                </p>
              </div>
            </div>
            <div className="booking-actions">
              {/* <span className={`status ${booking.status.toLowerCase()}`}>
                {booking.status}
              </span> */}
              <button className="btn">Message</button>
              <button className="btn">Call</button>
              <button className="btn primary">View Details</button>
            </div>
          </div>
        ))}
      </div>

      {/* Payments List */}
      <div className="payments-list">
        <h3 className="section-title">My Payments</h3>
        {payments.map((payment) => (
          <div key={payment.id} className="payment-card">
            <p>
              <strong>Booking ID:</strong> {payment.bookingId}
            </p>
            <p>
              <strong>Amount:</strong> ${payment.amount}
            </p>
            <p>
              <strong>Method:</strong> {payment.method}
            </p>
            <p>
              <strong>Date:</strong> {payment.date}
            </p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default UserDashboard;
