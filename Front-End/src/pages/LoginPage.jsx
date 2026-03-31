import { useState } from "react";
import { useNavigate } from "react-router-dom";

const API_BASE = "http://localhost:8080/api";

function LoginPage({ onLogin }) {
  const navigate = useNavigate();
  const [mode, setMode] = useState("login");
  const [form, setForm] = useState({ fullName: "", username: "", password: "" });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function submitForm(event) {
    event.preventDefault();
    setLoading(true);
    setError("");

    try {
      const endpoint = mode === "login" ? "login" : "register";
      const response = await fetch(`${API_BASE}/auth/${endpoint}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      });
      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.message || "Request failed.");
      }

      const session =
        mode === "login"
          ? {
              userId: data.userId,
              username: data.username,
              fullName: data.fullName,
            }
          : {
              userId: data.id,
              username: data.username,
              fullName: data.fullName,
            };

      if (mode === "login" && data.status !== "success") {
        throw new Error(data.message || "Invalid email or password.");
      }

      onLogin(session);
      navigate("/home");
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-panel">
        <div className="auth-copy">
          <p className="eyebrow">Nova Market</p>
          <h1>Login first, then we take you to the home page.</h1>
      
          
        </div>

        <div className="auth-card">
          <div className="auth-toggle">
            <button
              className={mode === "login" ? "active" : ""}
              onClick={() => setMode("login")}
              type="button"
            >
              Login
            </button>
            <button
              className={mode === "register" ? "active" : ""}
              onClick={() => setMode("register")}
              type="button"
            >
              Register
            </button>
          </div>

          {error && <div className="notice error">{error}</div>}

          <form className="stack" onSubmit={submitForm}>
            {mode === "register" && (
              <input
                placeholder="Full name"
                value={form.fullName}
                onChange={(event) => setForm({ ...form, fullName: event.target.value })}
              />
            )}
            <input
              placeholder="Email"
              value={form.username}
              onChange={(event) => setForm({ ...form, username: event.target.value })}
            />
            <input
              placeholder="Password"
              type="password"
              value={form.password}
              onChange={(event) => setForm({ ...form, password: event.target.value })}
            />
            <button className="primary-button" disabled={loading} type="submit">
              {loading ? "Please wait..." : mode === "login" ? "Login" : "Create account"}
            </button>
          </form>
        </div>
      </section>
    </main>
  );
}

export default LoginPage;
