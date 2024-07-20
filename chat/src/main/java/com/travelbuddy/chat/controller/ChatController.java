package com.travelbuddy.chat.controller;

import com.travelbuddy.chat.entity.ChatRoom;
import com.travelbuddy.chat.exception.RoomNotFoundException;
import com.travelbuddy.chat.model.Message;
import com.travelbuddy.chat.service.ChatRoomService;
import com.travelbuddy.chat.service.MongoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5001"})
@Slf4j
public class ChatController {

    @Autowired
    MongoService mongoService;
    @Autowired
    ChatRoomService chatRoomService;

    @PostMapping("/check")
    public Message message() {
        Message message = Message.builder()
                .content("Hello")
                .timestamp(LocalDate.now())
                .username("Aryan")
                .build();
        return mongoService.insertMessage(message, "e6542efc-88ce-4a3f-a2e5-75eebe9b4e22");
    }

    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/{roomId}")
    public void sendMessage(@DestinationVariable String roomId, Message message) {
        log.info("roomId is {}", roomId);
        log.info("Message is {}", message);
        message.setTimestamp(LocalDate.now());
        try {
            chatRoomService.sendMessage(roomId, message);
        } catch (RoomNotFoundException e) {

        }

    }

    @GetMapping("/messages/{roomId}")
    public List<Message> getAllMessagesForARoom(@PathVariable String roomId) {
        return chatRoomService.getAllMessagesForAChatRoom(roomId);
    }

    @PostMapping("/createRoom")
    public ChatRoom buildChatRoom() {
        ChatRoom chatRoom = ChatRoom.builder()
                .roomId("e6542efc-88ce-4a3f-a2e5-75eebe9b4e22")
                .build();
        return mongoService.saveRoom(chatRoom);
    }
}
