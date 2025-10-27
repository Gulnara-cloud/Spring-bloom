package com.gulnara.internship.service;

import com.gulnara.internship.dto.ChatRequestDto;
import com.gulnara.internship.dto.ChatResponseDto;

public interface ChatService {
    ChatResponseDto getChatResponse(ChatRequestDto request);
}
