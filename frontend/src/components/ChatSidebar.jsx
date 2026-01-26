import React from "react";
import "./ChatSidebar.css";

export default function ChatSidebar({
  conversations = [],
  activeConversationId,
  onSelectConversation,
  onNewConversation,
}) {
  return (
    <div className="sidebar-container">

        <div className="sidebar-header">

            <button
                type="button"
                className="sidebar-new-btn"
                onClick={onNewConversation}
            >
                + New chat
            </button>
        </div>

      {conversations.length === 0 ? (
        <p className="sidebar-empty">No conversations yet</p>
      ) : (
        <ul className="sidebar-list">
          {conversations.map((c) => {
            const id = c.id;
            const title = c.lastMessagePreview || c.title || `Conversation ${id}`;

            return (
              <li
                key={id}
                className={`sidebar-item ${
                  activeConversationId === id ? "active" : ""
                }`}
                onClick={() => onSelectConversation(id)}
              >
                {title}
              </li>
            );
          })}
        </ul>
      )}
    </div>
  );
}
