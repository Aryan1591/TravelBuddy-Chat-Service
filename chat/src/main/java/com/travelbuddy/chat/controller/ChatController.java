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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin(origins = {"https://astonishing-kitsune-6007fe.netlify.app", "http://localhost:8080", "http://localhost:5173"}, allowCredentials = "true")
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

    @PostMapping("/check/{roomId}")
    public ChatRoom message(@PathVariable String roomId) {

        return mongoService.saveRoom(ChatRoom.builder().roomId(roomId).messageList(List.of(new Message("hello", LocalDate.now(), "avishekh"))).build());
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

    @PostMapping("/createRoom/{postId}")
    public String buildChatRoom(@PathVariable String postId) {
        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(postId)
                .messageList(new ArrayList<>())
                .build();
        mongoService.saveRoom(chatRoom);
        return "Room has been successfully created with chatId " + postId;
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

    @DeleteMapping("/chat/{id}")
    public String deleteChatRecord(@PathVariable String id) {
        try {
            mongoService.deleteChatRecord(id);
            return String.format("Record with id %s has been successfully deleted ", id);
        } catch (Exception e) {
            return String.format("Record with id %s has not been deleted for reason %s", id, e.getMessage());
        }
    }
}
