import React from "react";
import Header from "../components/Header";

const HomePage: React.FC = () => {
  return (
    <>
      <Header />
      <main className="container mt-5">
        <h1>Welcome to HomePage!</h1>
        <p>Content goes here…</p>
      </main>
    </>
  );
};

export default HomePage;
