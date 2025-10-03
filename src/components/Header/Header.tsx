import React from "react";
import { FaHome, FaInfoCircle, FaEnvelope, FaHandHoldingMedical } from "react-icons/fa";
import { Link } from "react-router-dom"; // <-- import Link
import "./Header.css";

const Header: React.FC = () => {
  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-gradient fixed-top header">
      <div className="container-fluid">
        <Link className="navbar-brand fw-bold" to="/">
          <FaHandHoldingMedical className="me-2 ms-1" /> CareNet
        </Link>

        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav ms-auto">
            <li className="nav-item">
              <Link className="nav-link d-flex align-items-center" to="/">
                <FaHome className="me-1" /> Home
              </Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link d-flex align-items-center" to="/dashboard">
                <FaInfoCircle className="me-1" /> About
              </Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link d-flex align-items-center" to="/contact">
                <FaEnvelope className="me-1" /> Contact
              </Link>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  );
};

export default Header;
