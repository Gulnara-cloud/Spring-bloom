 import React, { useState } from "react";
 import "../App.css";
 import { useLocation, useNavigate } from "react-router-dom";
 import { useEffect } from "react";

 function Login({ onSwitchToRegister }) {
   // State variables for user input
   const [email, setEmail] = useState("");
   const [password, setPassword] = useState("");
   const [message, setMessage] = useState("");

   const navigate = useNavigate();
   const location = useLocation();

   // Show alert message if redirected from a protected route
     useEffect(() => {
       if (location.state?.msg) {
         alert(location.state.msg);
       }
     }, [location.state]);

   // Handle login form submission
   const handleLogin = async (e) => {
     e.preventDefault();

     const loginData = { email, password };

     try {
       const response = await fetch("http://localhost:8080/api/auth/login", {
         method: "POST",
         headers: { "Content-Type": "application/json" },
         body: JSON.stringify(loginData),
       });

       if (response.ok) {
         const data = await response.json();

         // Save token to localStorage
         if (data.token) {
           localStorage.setItem("token", data.token);
         }

         // Show success message and navigate to chat
         setMessage(data.message || "Login successful!");
         navigate("/chat", { replace: true });
       } else {
           // Handle invalid credentials
           const errorData = await response.json();
           setMessage(errorData.message || "Invalid username or password");
          }
        } catch (error) {
            // Handle connection error
            setMessage("Error connecting to backend.");
           }
        };

   return (
     <div className="form-container">
       <div className="form-box">
         <h2>User Login</h2>

         <form onSubmit={handleLogin}>
           <input
             type="email"
             placeholder="Email"
             value={email}
             onChange={(e) => setEmail(e.target.value)}
             required
           />

           <input
             type="password"
             placeholder="Password"
             value={password}
             onChange={(e) => setPassword(e.target.value)}
             required
           />

           <button type="submit">Login</button>
         </form>

         <p>{message}</p>

         <p style={{ marginTop: "20px" }}>
           Don’t have an account?{" "}
           <p><button onClick={() => navigate("/register")}>Register</button></p>
         </p>
       </div>
     </div>
   );
 }

 export default Login;
