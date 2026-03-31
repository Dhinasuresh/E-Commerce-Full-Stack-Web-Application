import { Navigate, Route, Routes } from "react-router-dom";
import { useEffect, useState } from "react";
import LoginPage from "./pages/LoginPage";
import HomePage from "./pages/HomePage";

function App() {
  const [session, setSession] = useState(() => {
    const saved = window.localStorage.getItem("shop-session");
    return saved ? JSON.parse(saved) : null;
  });
  const [theme, setTheme] = useState(() => window.localStorage.getItem("shop-theme") || "light");

  useEffect(() => {
    if (session) {
      window.localStorage.setItem("shop-session", JSON.stringify(session));
    } else {
      window.localStorage.removeItem("shop-session");
    }
  }, [session]);

  useEffect(() => {
    document.documentElement.dataset.theme = theme;
    window.localStorage.setItem("shop-theme", theme);
  }, [theme]);

  return (
    <Routes>
      <Route
        path="/"
        element={session ? <Navigate replace to="/home" /> : <LoginPage onLogin={setSession} />}
      />
      <Route
        path="/home"
        element={
          session ? (
            <HomePage
              onLogout={() => setSession(null)}
              session={session}
              theme={theme}
              toggleTheme={() => setTheme((current) => (current === "light" ? "dark" : "light"))}
            />
          ) : (
            <Navigate replace to="/" />
          )
        }
      />
      <Route path="*" element={<Navigate replace to={session ? "/home" : "/"} />} />
    </Routes>
  );
}

export default App;
