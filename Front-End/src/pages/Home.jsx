import React, { useState, useEffect } from "react";
import logo from "./assets/logo.png";
import "./Home.css";

function Home() {
  const [showList, setShowList] = useState(false);
  const [darkMode, setDarkMode] = useState(false);
  const [products, setProducts] = useState([]);

  useEffect(() => {
    fetch('http://localhost:8080/api/products')
      .then(response => response.json())
      .then(data => setProducts(data))
      .catch(error => console.error('Error fetching products:', error));
  }, []);

  const searchAnime = () => {
    console.log("Search triggered");
  };

  const handleButton = (name) => {
    console.log(`${name} button clicked`);
  };

  const List = () => (
    <div>
      <h1>Products</h1>
      <ul>
        {products.map(product => (
          <li key={product.id}>{product.name} - ${product.price}</li>
        ))}
      </ul>
    </div>
  );

  // Dynamic styles based on darkMode
  const bodyStyle = {
    backgroundColor: darkMode ? "black" : "white",
    color: darkMode ? "white" : "black",
    minHeight: "100vh",
  };

  const sectionStyle = {
    backgroundColor: darkMode ? "black" : "white",
    color: darkMode ? "white" : "black",
  };

  return (
    <div style={bodyStyle}>
      {/* HEADER */}
      <div id="header" style={sectionStyle}>
        <img
          src={logo}
          alt="Logo"
          style={{
            height: "60px",
            width: "60px",
            marginTop: "20px",
            marginLeft: "10px",
            borderRadius: "50%",
            backgroundColor: "white",
          }}
        />
        <ul className="nav">
          <li className="les">
            <button className="btn1" onClick={() => setShowList(false)}>
              <i className="fas fa-home"></i>
            </button>
          </li>
          <li className="les">
            <button className="btn1" onClick={() => setShowList(true)}>
              <i className="fas fa-book"></i>
            </button>
          </li>
          <li className="les">
            <button className="btn1" onClick={() => handleButton("Tag")}>
              <i className="fas fa-tag"></i>
            </button>
          </li>
          <li className="les">
            <button className="btn1" onClick={() => handleButton("Trophy")}>
              <i className="fas fa-trophy"></i>
            </button>
          </li>
          <li className="les">
            <button className="btn1" onClick={() => handleButton("Settings")}>
              <i className="fas fa-cog"></i>
            </button>
          </li>
          <li className="les">
            <button className="btn1" onClick={() => handleButton("User")}>
              <i className="fas fa-user"></i>
            </button>
          </li>
        </ul>
      </div>

      {/* SEARCH */}
      <div id="search" style={sectionStyle}>
        <form
          onSubmit={(e) => {
            e.preventDefault();
            searchAnime();
          }}
        >
          <input
            type="text"
            id="searchInput"
            placeholder="Search for products..."
            style={{
              padding: "10px",
              borderRadius: "10px",
              marginLeft: "200px",
              border: "1px solid #ccc",
              height: "20px",
              width: "1200px",
              marginTop: "20px",
              backgroundColor: darkMode ? "#333" : "white",
              color: darkMode ? "white" : "black",
            }}
          />
          <button
            type="submit"
            style={{
              height: "30px",
              width: "30px",
              borderRadius: "10px",
              border: "none",
              backgroundColor: darkMode ? "#555" : "#f0f0f0",
              color: darkMode ? "white" : "black",
            }}
          >
            <i className="fas fa-search"></i>
          </button>
          <button
            id="theme"
            type="button"
            style={{
              height: "30px",
              width: "30px",
              borderRadius: "10px",
              border: "none",
              backgroundColor: darkMode ? "#555" : "#f0f0f0",
              color: darkMode ? "white" : "black",
              marginLeft: "10px",
            }}
            onClick={() => setDarkMode(!darkMode)}
          >
            <i className="fas fa-moon"></i>
          </button>
        </form>
      </div>

      {/* MAIN CONTENT */}
      <div id="main">
        {!showList && (
          <>
            <h1 style={{ marginTop: "20px" }}>Welcome to Ecommerce App</h1>
            <p style={{ marginTop: "10px" }}>
              Discover our products!
            </p>
            <div>
              <h2>Available Products</h2>
              <ul>
                {products.map(product => (
                  <li key={product.id}>{product.name} - ${product.price}</li>
                ))}
              </ul>
            </div>
          </>
        )}

        {showList && <List />}
      </div>
    </div>
  );
}

export default Home;