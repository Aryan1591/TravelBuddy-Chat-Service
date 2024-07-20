package com.travelbuddy.chat.service;

import com.travelbuddy.chat.exception.RoomNotFoundException;
import com.travelbuddy.chat.model.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatRoomService {

    private MongoService mongoService;
    private SimpMessagingTemplate messagingTemplate;

    public ChatRoomService(MongoService mongoService, SimpMessagingTemplate simpMessagingTemplate) {
        this.mongoService = mongoService;
        this.messagingTemplate = simpMessagingTemplate;
    }

    public Message sendMessage(String chatRoomId, Message message) {
        message = mongoService.insertMessage(message, chatRoomId);
        if (null == message)
            throw new RoomNotFoundException("Room is not available to send Message");
        messagingTemplate.convertAndSend("/topic/" + chatRoomId, message);
        return message;
    }

    public List<Message> getAllMessagesForAChatRoom(String chatRoomId) {
        return mongoService.getAllMessagesForAChatRoom(chatRoomId);
    }
}
