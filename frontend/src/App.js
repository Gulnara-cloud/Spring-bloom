import React from "react";
import "./App.css";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Register from "./components/Register.jsx";
import Login from "./components/Login.jsx";
import Chat from "./components/Chat.jsx";
import ProtectedRoute from "./components/ProtectedRoute";

function App() {
  return (
    <Router>
      {/*  background color */}
      <div
        style={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          height: "100vh",
          backgroundColor: "#f8f6f3",
        }}
      >
        <Routes>
          {/* Registration page */}
          <Route path="/" element={<Register />} />

          {/* Login page */}
          <Route path="/login" element={<Login />} />

          {/* Chat page (only for authorized users) */}
          <Route
            path="/chat"
            element={
              <ProtectedRoute>
                <Chat />
              </ProtectedRoute>
            }
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
