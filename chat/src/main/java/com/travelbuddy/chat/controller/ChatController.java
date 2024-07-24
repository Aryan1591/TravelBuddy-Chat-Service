package com.travelbuddy.chat.controller;

import com.travelbuddy.chat.entity.ChatRoom;
import com.travelbuddy.chat.exception.RoomNotFoundException;
import com.travelbuddy.chat.model.JoinMessage;
import com.travelbuddy.chat.model.LeaveMessage;
import com.travelbuddy.chat.model.Message;
import com.travelbuddy.chat.service.ActiveUsersService;
import com.travelbuddy.chat.service.ChatRoomService;
import com.travelbuddy.chat.service.MongoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin(origins = "https://astonishing-kitsune-6007fe.netlify.app", allowCredentials = "true")
@Slf4j
public class ChatController {

    @Autowired
    MongoService mongoService;
    @Autowired
    ChatRoomService chatRoomService;
    private final ActiveUsersService activeUserService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ActiveUsersService activeUserService, SimpMessagingTemplate simpMessagingTemplate) {
        this.activeUserService = activeUserService;
        this.messagingTemplate = simpMessagingTemplate;
    }

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

    @MessageMapping("/chat/join")
    public void joinRoom(JoinMessage message) {
        activeUserService.addUserToRoom(message.getRoomId(), message.getUsername());
        sendActiveUsers(message.getRoomId());
    }

    @MessageMapping("/chat/leave")
    public void leaveRoom(LeaveMessage message) {
        activeUserService.removeUserFromRoom(message.getRoomId(), message.getUsername());
        sendActiveUsers(message.getRoomId());
    }

    private void sendActiveUsers(String roomId) {
        Set<String> activeUsers = activeUserService.getActiveUsers(roomId);
        messagingTemplate.convertAndSend("/topic/" + roomId + "/active-users", activeUsers);
    }
}
