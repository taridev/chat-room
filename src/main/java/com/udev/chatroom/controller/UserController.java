package com.udev.chatroom.controller;

import com.udev.chatroom.model.ChatMessage;
import com.udev.chatroom.model.User;
import com.udev.chatroom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @MessageMapping("/chat/{roomdId}/{username}")
    @SendTo("/channel/{roomId}")
    public User getUser(@DestinationVariable String username) {
        if (!StringUtils.isEmpty(username)) {
            return userService.findByUsername(username);
        }
        return null;
    }
}
