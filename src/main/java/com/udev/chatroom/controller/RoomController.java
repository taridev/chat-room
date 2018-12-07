package com.udev.chatroom.controller;

import com.udev.chatroom.model.ChatMessage;
import com.udev.chatroom.model.Room;
import com.udev.chatroom.model.User;
import com.udev.chatroom.service.ChatMessageService;
import com.udev.chatroom.service.RoomService;
import com.udev.chatroom.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

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

    @GetMapping("/room/{roomId}")
    @SendTo("/channel/")
    @ResponseBody
    public Room findById(@PathVariable Long roomId) {
        if (roomId != null) {
            return roomService.findById(roomId);
        }
        return null;
    }

    public List<ChatMessage> findAllMessageInRoom(Long roomId) {
        return null;
    }
}
