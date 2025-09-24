import React from "react";
import { FaHome, FaInfoCircle, FaEnvelope , FaHandHoldingMedical } from "react-icons/fa";
import "./Header.css";

const Header: React.FC = () => {
  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-gradient fixed-top header">
      <div className="container-fluid">
          <a className="navbar-brand fw-bold" href="#">
          <FaHandHoldingMedical className="me-2 ms-1" /> CareNet
        </a>

        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav ms-auto">
            <li className="nav-item">
              <a className="nav-link d-flex align-items-center" href="#">
                <FaHome className="me-1" /> Home
              </a>
            </li>
            <li className="nav-item">
              <a className="nav-link d-flex align-items-center" href="#">
                <FaInfoCircle className="me-1" /> About
              </a>
            </li>
            <li className="nav-item">
              <a className="nav-link d-flex align-items-center" href="#">
                <FaEnvelope className="me-1" /> Contact
              </a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  );
};

export default Header;
