package com.udev.chatroom.service;

import com.udev.chatroom.model.Room;
import com.udev.chatroom.model.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

    @Autowired
    private RoomRepository repository;

    public Room findById(Long id) { return  repository.findById(id).orElse(null); }
}
