import { Navigate, Route, Routes } from "react-router-dom";
import { useEffect, useState } from "react";
import LoginPage from "./pages/LoginPage";
import HomePage from "./pages/HomePage";

function App() {
  const [session, setSession] = useState(() => {
    const saved = window.localStorage.getItem("fertilizer-shop-session");
    return saved ? JSON.parse(saved) : null;
  });

  useEffect(() => {
    if (session) {
      window.localStorage.setItem("fertilizer-shop-session", JSON.stringify(session));
    } else {
      window.localStorage.removeItem("fertilizer-shop-session");
    }
  }, [session]);

  return (
    <Routes>
      <Route
        path="/"
        element={session ? <Navigate replace to="/app" /> : <LoginPage onLogin={setSession} />}
      />
      <Route
        path="/app"
        element={session ? <HomePage onLogout={() => setSession(null)} session={session} /> : <Navigate replace to="/" />}
      />
      <Route path="*" element={<Navigate replace to={session ? "/app" : "/"} />} />
    </Routes>
  );
}

export default App;
