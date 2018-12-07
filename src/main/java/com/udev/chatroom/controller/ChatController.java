package com.udev.chatroom.controller;

import static java.lang.String.format;

import com.udev.chatroom.model.ChatMessage;
import com.udev.chatroom.model.Room;
import com.udev.chatroom.model.User;
import com.udev.chatroom.service.ChatMessageService;
import com.udev.chatroom.service.RoomService;
import com.udev.chatroom.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import java.util.Date;

@Controller
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private UserService userService;


    @Autowired
    private ChatMessageService messageService;

    @Autowired
    private RoomService roomService;

    @MessageMapping("/chat/{roomId}/sendMessage")
    @SendTo("/channel/{roomId}")
    public ChatMessage sendMessage(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
        chatMessage.setCreatedAt(new Date());
        User sender = userService.findByUsername(chatMessage.getSender().getUsername());
        Room room = roomService.findById(Long.parseLong(roomId));
        if (sender != null && room != null) {
            chatMessage.setSender(sender);
            chatMessage.setRoom(room);
            messageService.save(chatMessage);
            return chatMessage;
        }
        return null;
    }

    @MessageMapping("/chat/{roomId}/addUser")
    // @SendTo("/channel/{roomId}")
    public void addUser(@DestinationVariable String roomId, @Payload ChatMessage chatMessage,
                        SimpMessageHeaderAccessor headerAccessor) {
        String currentRoomId = (String) headerAccessor.getSessionAttributes().put("room_id", roomId);
        User user = userService.findByUsername(chatMessage.getSender().getUsername());
        if (currentRoomId != null) {
            ChatMessage leaveMessage = new ChatMessage();
            leaveMessage.setType(ChatMessage.MessageType.LEAVE);
            leaveMessage.setSender(user);
            messagingTemplate.convertAndSend(format("/channel/%s", currentRoomId), leaveMessage);
        }
        headerAccessor.getSessionAttributes().put("username", user.getUsername());
        chatMessage.setSender(user);
        messagingTemplate.convertAndSend(format("/channel/%s", roomId), chatMessage);
    }
}