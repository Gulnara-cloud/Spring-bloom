package com.gulnara.internship.dto;

public class ChatResponseDto {

    private String reply; // More clear name for frontend

    public ChatResponseDto() {
    }

    public ChatResponseDto(String reply) {
        this.reply = reply;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
