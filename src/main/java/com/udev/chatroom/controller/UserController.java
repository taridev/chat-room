package com.udev.chatroom.controller;

import com.udev.chatroom.model.ChatMessage;
import com.udev.chatroom.model.User;
import com.udev.chatroom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/{username}")
    @SendTo("/channel/")
    @ResponseBody
    public User getUser(@PathVariable String username) {
        if (!StringUtils.isEmpty(username)) {
            return userService.findByUsername(username);
        }
        return null;
    }
}
