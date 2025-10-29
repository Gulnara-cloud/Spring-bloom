import React, { useState, useRef, useEffect } from "react";
import "./Chat.css";

console.log(">> Rendering Chat.jsx");

export default function Chat() {
  const [messages, setMessages] = useState([
    { id: 1, role: "ai", text: "Hello!" },
    { id: 2, role: "ai", text: "How can I assist you today?" },
  ]);
  const [prompt, setPrompt] = useState("");
  const [loading, setLoading] = useState(false);
  const scrollRef = useRef(null);

  //  Auto scroll to the latest message
  useEffect(() => {
    scrollRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const handleSend = (e) => {
    e.preventDefault();
    if (!prompt.trim()) return;

    const userMsg = { id: Date.now(), role: "user", text: prompt.trim() };
    setMessages((prev) => [...prev, userMsg]);
    setPrompt("");
    setLoading(true);

    // simulate AI reply reliably
    setTimeout(() => {
      const aiResponse = {
        id: Date.now() + 1,
        role: "ai",
        text: "That's interesting!",
      };
      setMessages((prev) => [...prev, aiResponse]);
      setLoading(false);
    }, 800);
  };

  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "flex-start",
        minHeight: "90vh",
        paddingTop: "200px",
        backgroundColor: "#f8f6f3",
      }}
    >
      <h2
        style={{
          textAlign: "center",
          color: "#3a2f2f",
          fontWeight: "600",
          fontSize: "30px",
          marginBottom: "35px",
          marginTop: "10px",
          letterSpacing: "0.0px",
          fontFamily:  "'Avenir','Helvetica Neue',sans-serif",
        }}
      >
        AI Chat Assistant
      </h2>

      <div className="chat-container">
        <div className="chat-box">
          {messages.map((msg) => (
            <div key={msg.id} className={`message ${msg.role}`}>
              {msg.text}
            </div>
          ))}
        </div>

        <form className="input-row" onSubmit={handleSend}>
          <input
            type="text"
            placeholder="Type your message..."
            value={prompt}
            onChange={(e) => setPrompt(e.target.value)}
            disabled={loading}
          />
          <button type="submit" disabled={loading}>
            {loading ? "..." : "Send"}
          </button>
        </form>
      </div>
    </div>
  );
}