import React from "react";
import { Navigate } from "react-router-dom";

const ProtectedRoute = ({ element }) => {
  const token = localStorage.getItem("token");

  if (!token) {
    alert("You must be logged in to access the chat page.");
    return <Navigate to="/login" replace />;
  }

  return element;
};

export default ProtectedRoute;
