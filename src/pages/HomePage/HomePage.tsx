import React, { useState, useEffect } from "react";
import Header from "../../components/Header/Header";
import "./HomePage.css";

import slide1 from "../../assets/slide1.jpg";
import slide2 from "../../assets/slide2.jpg";
import slide3 from "../../assets/slide3.jpg";
import slide4 from "../../assets/pexels-olly-755049.jpg";
import slide5 from "../../assets/pexels-markus-winkler-1430818-30885929.jpg";
import { Link } from "react-router-dom";

const HomePage: React.FC = () => {
  const [currentSlide, setCurrentSlide] = useState(0);

  const slides = [
    slide1,slide2,slide3, slide4 , slide5
  ];

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentSlide((prev) => (prev + 1) % slides.length);
    }, 5000);
    return () => clearInterval(timer);
  }, [slides.length]);

  const goToSlide = (index: number) => {
    setCurrentSlide(index);
  };

  return (
    <>
      <Header />
      <main className="container">
        <section className="slideshow">
          {slides.map((slide, index) => (
            <img
              key={index}
              src={slide}
              alt={`Caregiver Slide ${index + 1}`}
              className={`slide ${currentSlide === index ? "active" : ""}`}
            />
          ))}
          <div className="nav-dots">
            {slides.map((_, index) => (
              <button
                key={index}
                onClick={() => goToSlide(index)}
                className={`dot ${currentSlide === index ? "active" : ""}`}
              />
            ))}
          </div>
        </section>

        <section className="welcome">
          <h1>Welcome to CareNet</h1>
          <p>Your trusted platform for booking professional caregivers for all your needs.</p>
        </section>

        <section className="features">
          <h2>Why CareNet?</h2>
          <div className="features-grid">
            <div className="feature-card">
              <h3>Trusted Caregivers</h3>
              <p>Connect with verified, compassionate caregivers for elderly, child, or special needs care.</p>
            </div>
            <div className="feature-card">
              <h3>Flexible Booking</h3>
              <p>Book caregivers on your schedule, from one-time visits to ongoing support.</p>
            </div>
            <div className="feature-card">
              <h3>Personalized Care</h3>
              <p>Find caregivers tailored to your specific needs and preferences.</p>
            </div>
          </div>
        </section>

        <section className="cta">
          <h2>Find the Perfect Caregiver Today</h2>
          <p>Join CareNet and book trusted caregivers with ease.</p>
         <Link to="/booking">
            <button className="cta-button">Book Now</button>
          </Link>
        </section>
      </main>
    </>
  );
};

export default HomePage;