import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import React, { useState, useRef, useEffect } from "react";
import "./Chat.css";
import { useNavigate } from "react-router-dom";
import ChatSidebar from "./ChatSidebar";

export default function Chat() {
  const navigate = useNavigate();

  // сообщения в правой части
  const [messages, setMessages] = useState([]);
  const [prompt, setPrompt] = useState("");
  const [loading, setLoading] = useState(false);
  const scrollRef = useRef(null);

  // новое – список диалогов для сайдбара
  const [conversations, setConversations] = useState([]);
  const [activeConversationId, setActiveConversationId] = useState(null);

  // при открытии страницы грузим список диалогов
  useEffect(() => {
    loadConversations();
  }, []);

  async function loadConversations() {
    const token = localStorage.getItem("token");
    if (!token) return;

    try {
      const res = await fetch(
        "http://localhost:8080/api/chat/conversations",
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!res.ok) {
        console.error("Failed to load conversations list", res.status);
        return;
      }

      const data = await res.json();
      setConversations(data || []);
    } catch (e) {
      console.error("Error loading conversations list", e);
    }
  }

  // клик по существующему диалогу в сайдбаре
  const handleSelectConversation = async (id) => {
    setActiveConversationId(id);
    setMessages([]);

    const token = localStorage.getItem("token");
    if (!token) return;

    try {
      const res = await fetch(
        `http://localhost:8080/api/chat/conversations/${id}`,
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!res.ok) {
        console.error("Failed to load conversation", res.status);
        return;
      }

      const data = await res.json(); // ConversationDetailDto

      const history =
        (data.messages || []).map((m, index) => ({
          id: m.id ?? index,
          role: m.role.toLowerCase() === "user" ? "user" : "ai",
          text: m.content,
        })) || [];

      setMessages(history);
    } catch (e) {
      console.error("Error loading conversation", e);
    }
  };

  // кнопка "+ New chat" в сайдбаре
  const handleNewConversation = () => {
    setActiveConversationId(null);
    setMessages([]);
  };

  // отправка сообщения
  const handleSend = async (e) => {
    e.preventDefault();
    if (!prompt.trim()) return;

    const userMsg = { id: Date.now(), role: "user", text: prompt.trim() };
    setMessages((prev) => [...prev, userMsg]);
    setPrompt("");
    setLoading(true);

    try {
      const token = localStorage.getItem("token");
      if (!token) {
        setMessages((prev) => [
          ...prev,
          { id: Date.now() + 1, role: "ai", text: "Please login first." },
        ]);
        return;
      }

      const response = await fetch(
        "http://localhost:8080/api/chat/message",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            message: userMsg.text,
            conversationId: activeConversationId, // либо новый, либо существующий диалог
          }),
        }
      );

      if (response.ok) {
        const data = await response.json();

        // если это был самый первый месседж – сохранить conversationId
        if (!activeConversationId && data.conversationId) {
          setActiveConversationId(data.conversationId);
        }

        // ответ ИИ
        setMessages((prev) => [
          ...prev,
          { id: Date.now() + 1, role: "ai", text: data.response },
        ]);

        // обновить список диалогов слева
        await loadConversations();
      } else if (response.status === 401 || response.status === 403) {
        setMessages((prev) => [
          ...prev,
          {
            id: Date.now() + 1,
            role: "ai",
            text: "Unauthorized — please login again.",
          },
        ]);
        localStorage.removeItem("token");
        window.location.href = "/login";
      } else {
        const errorText = await response.text();
        setMessages((prev) => [
          ...prev,
          {
            id: Date.now() + 1,
            role: "ai",
            text: errorText || "Error: AI not available right now.",
          },
        ]);
      }
    } catch (err) {
      console.error("Network error:", err);
      setMessages((prev) => [
        ...prev,
        {
          id: Date.now() + 1,
          role: "ai",
          text: "Network error. Please try again.",
        },
      ]);
    } finally {
      setLoading(false);
      if (scrollRef.current) {
        scrollRef.current.scrollIntoView({ behavior: "smooth" });
      }
    }
  };

  return (
    <div style={{ display: "flex" }}>
      {/* ЛЕВЫЙ САЙДБАР */}
      <ChatSidebar
        conversations={conversations}
        activeConversationId={activeConversationId}
        onSelectConversation={handleSelectConversation}
        onNewConversation={handleNewConversation}
      />

      {/* ПРАВАЯ ЧАСТЬ — твой старый дизайн */}
      <div
        style={{
          flex: 1,
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "flex-start",
          height: "100vh",        // фиксируем высоту экрана
          paddingTop: "40px",    // чуть отступ сверху для хедера
          backgroundColor: "#f8f6f3",
          boxSizing: "border-box",
          overflow: "hidden",     // запрещаем прокрутку всей правой части
        }}
      >
        {/* Header */}
        <header className="chat-header">
          <h2>Spring Bloom Project</h2>
          <button
            className="logout-btn"
            onClick={() => {
              localStorage.removeItem("token");
              navigate("/login");
            }}
          >
            Logout
          </button>
        </header>

        <h2
          style={{
            textAlign: "center",
            color: "#3a2f2f",
            fontWeight: "600",
            fontSize: "25px",
            marginBottom: "15px",
            marginTop: "15px",
            letterSpacing: "0.8px",
            fontFamily: "Avenir, 'Helvetica Neue', sans-serif",
          }}
        >
          AI Assistant
        </h2>

        <div className="chat-container">
          <div className="messages-container">
            {messages.map((msg, i) => (
              <div key={i} className={`message ${msg.role}`}>
                <ReactMarkdown remarkPlugins={[remarkGfm]}>
                  {msg.text}
                </ReactMarkdown>
              </div>
            ))}
            <div ref={scrollRef} />
          </div>
          <form className="input-row" onSubmit={handleSend}>
            <input
              type="text"
              placeholder="Type your question about this section..."
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
    </div>
  );
}
