 import React, { useState } from "react";
 import "../App.css";

 function Login({ onSwitchToRegister }) {
   // State variables
   const [username, setUsername] = useState("");
   const [password, setPassword] = useState("");
   const [message, setMessage] = useState("");

   // Handle login
   const handleLogin = async (e) => {
     e.preventDefault();

     const loginData = { username, password };

     try {
       const response = await fetch("http://localhost:8080/api/auth/login", {
         method: "POST",
         headers: { "Content-Type": "application/json" },
         body: JSON.stringify(loginData),
       });

       if (response.ok) {
         setMessage("Login successful!");
       } else {
         setMessage("Invalid username or password.");
       }
     } catch (error) {
       setMessage("Error connecting to backend.");
     }
   };

   return (
     <div className="form-container">
       <div className="form-box">
         <h2>User Login</h2>
         <form onSubmit={handleLogin}>
           <input
             type="text"
             placeholder="Username"
             value={username}
             onChange={(e) => setUsername(e.target.value)}
           />
           <input
             type="password"
             placeholder="Password"
             value={password}
             onChange={(e) => setPassword(e.target.value)}
           />
           <button type="submit">Login</button>
         </form>

         <p>{message}</p>

         <p style={{ marginTop: "20px" }}>
           Don’t have an account?{" "}
           <button onClick={onSwitchToRegister}>Register</button>
         </p>
       </div>
     </div>
   );
 }

 export default Login;
