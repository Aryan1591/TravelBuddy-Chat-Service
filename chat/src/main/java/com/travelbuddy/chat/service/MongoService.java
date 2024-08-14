package com.travelbuddy.chat.service;

import com.travelbuddy.chat.entity.ChatRoom;
import com.travelbuddy.chat.model.Message;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class MongoService {

    private MongoTemplate mongoTemplate;

    public MongoService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Message insertMessage(Message message, String roomId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(roomId));
        ChatRoom chatRoom = mongoTemplate.findOne(query, ChatRoom.class);
        if (Objects.isNull(chatRoom)) {
            return null;
        }
        chatRoom.getMessageList().add(message);
        Update update = new Update();
        update.set("messageList", chatRoom.getMessageList());
        mongoTemplate.updateFirst(query, update, ChatRoom.class);
        return message;
    }

    public ChatRoom saveRoom(ChatRoom room) {
        return mongoTemplate.insert(room);
    }

    public List<Message> getAllMessagesForAChatRoom(String chatRoomId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(chatRoomId));
        ChatRoom chatRoom = mongoTemplate.findOne(query, ChatRoom.class);
        return Objects.isNull(chatRoom) ? List.of() : chatRoom.getMessageList();
    }

    public void deleteChatRecord(String chatRoomId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(chatRoomId));
        mongoTemplate.remove(query, "travelbuddy.chats");
    }
}
