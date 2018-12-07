package com.udev.chatroom.service;

import com.udev.chatroom.model.ChatMessage;
import com.udev.chatroom.model.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository repository;

    @Transactional
    public void save(ChatMessage message) {
        repository.save(message);
    }
}
