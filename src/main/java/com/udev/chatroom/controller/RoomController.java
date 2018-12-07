package com.udev.chatroom.controller;

import com.udev.chatroom.service.ChatMessageService;
import com.udev.chatroom.service.RoomService;
import com.udev.chatroom.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
public class RoomController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private UserService userService;


    @Autowired
    private ChatMessageService messageService;

    @Autowired
    private RoomService roomService;

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
}
