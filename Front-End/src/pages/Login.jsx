import React, { useState } from "react";
import { useNavigate } from "react-router-dom"; 
import "./Login.css";
import bgVideo from "../pages/assets/background.mp4";

function Login() {
  const navigate = useNavigate(); 
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleRegister = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/users/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
      });

      const user = await response.json();
      alert('User registered successfully');
    } catch (error) {
      alert('Error registering user');
    }
  };

  return (
    <div className="login-page">
      {/* Video Background */}
      <video autoPlay loop muted className="bg-video">
        <source src={bgVideo} type="video/mp4" />
      </video>

     
      <div className="Login-container">
        <h1 className="fnt">Login</h1>
        <form onSubmit={handleSubmit}>
          <input
          placeholder="Gmail"
            className="btn"
            type="text"
            name="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
          <input
          placeholder="Password"
            className="btn"
            type="password"
            name="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />

          <button className="btn" type="submit">
            Login
          </button>
          <button className="btn" type="button" onClick={handleRegister}>
            Register
          </button>
        </form>
      </div>
    </div>
  );
}

export default Login;