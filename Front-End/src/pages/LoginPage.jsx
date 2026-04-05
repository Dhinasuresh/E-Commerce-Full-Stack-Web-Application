import { useState } from "react";
import { useNavigate } from "react-router-dom";

const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

function LoginPage({ onLogin }) {
  const navigate = useNavigate();
  const [mode, setMode] = useState("login");
  const [form, setForm] = useState({
    fullName: "",
    username: "",
    password: "",
    role: "OWNER",
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function handleSubmit(event) {
    event.preventDefault();
    setLoading(true);
    setError("");

    try {
      const response = await fetch(`${API_BASE}/auth/${mode}`, {
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
              role: data.role,
            }
          : {
              userId: data.id,
              username: data.username,
              fullName: data.fullName,
              role: data.role,
            };

      if (mode === "login" && data.status !== "success") {
        throw new Error(data.message || "Invalid credentials.");
      }

      onLogin(session);
      navigate("/app");
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="auth-shell">
      <section className="auth-hero">
        <div className="brand-chip">Fertilizer Shop Manager</div>
        <h1>Run your fertilizer store from billing to dues in one place.</h1>
        <p>
          Built for a real counter workflow: inventory, customer ledgers, due tracking, daily sales,
          and a printable bill.
        </p>
        <div className="demo-credentials">
          <strong>Test logins</strong>
          <span>owner@fertilizer.com / admin123</span>
          <span>staff@fertilizer.com / staff123</span>
        </div>
      </section>

      <section className="auth-card">
        <div className="segmented">
          <button className={mode === "login" ? "active" : ""} onClick={() => setMode("login")} type="button">
            Login
          </button>
          <button className={mode === "register" ? "active" : ""} onClick={() => setMode("register")} type="button">
            Register
          </button>
        </div>

        {error && <div className="banner error">{error}</div>}

        <form className="form-grid" onSubmit={handleSubmit}>
          {mode === "register" && (
            <>
              <label>
                Full name
                <input
                  required
                  value={form.fullName}
                  onChange={(event) => setForm({ ...form, fullName: event.target.value })}
                />
              </label>
              <label>
                Role
                <select value={form.role} onChange={(event) => setForm({ ...form, role: event.target.value })}>
                  <option value="OWNER">Owner</option>
                  <option value="STAFF">Staff</option>
                </select>
              </label>
            </>
          )}
          <label>
            Email
            <input
              required
              type="email"
              value={form.username}
              onChange={(event) => setForm({ ...form, username: event.target.value })}
            />
          </label>
          <label>
            Password
            <input
              required
              type="password"
              value={form.password}
              onChange={(event) => setForm({ ...form, password: event.target.value })}
            />
          </label>
          <button className="primary-button stretch" disabled={loading} type="submit">
            {loading ? "Please wait..." : mode === "login" ? "Open store app" : "Create account"}
          </button>
        </form>
      </section>
    </main>
  );
}

export default LoginPage;
