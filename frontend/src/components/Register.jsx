 import React, { useState } from "react";
 import "../App.css";

 function Register({ onSwitchToLogin }) {
   // State variables for user input
   const [username, setUsername] = useState("");
   const [email, setEmail] = useState("");
   const [password, setPassword] = useState("");
   const [message, setMessage] = useState("");

   // Handle registration form submission
   const handleRegister = async (e) => {
     e.preventDefault();

     const userData = { username, email, password };

     try {
       const response = await fetch("http://localhost:8080/api/auth/register", {
         method: "POST",
         headers: { "Content-Type": "application/json" },
         body: JSON.stringify(userData),
       });

       if (response.ok) {
         setMessage("Registration successful!");
       } else {
         const errorText = await response.text();
         setMessage(errorText || "Registration failed.");
       }
     } catch (error) {
       setMessage("Error connecting to backend.");
     }
   };

   return (
     <div className="form-container">
       <div className="form-box">
         <h2>User Registration</h2>
         <form onSubmit={handleRegister}>
           <input
             type="text"
             placeholder="Username"
             value={username}
             onChange={(e) => setUsername(e.target.value)}
           />
           <input
             type="email"
             placeholder="Email"
             value={email}
             onChange={(e) => setEmail(e.target.value)}
           />
           <input
             type="password"
             placeholder="Password"
             value={password}
             onChange={(e) => setPassword(e.target.value)}
           />
           <button type="submit">Register</button>
         </form>

         <p>{message}</p>

         <p style={{ marginTop: "20px" }}>
           Already have an account?{" "}
           <button onClick={onSwitchToLogin}>Login</button>
         </p>
       </div>
     </div>
   );
 }

 export default Register;
