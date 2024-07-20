package com.travelbuddy.chat.service;

import com.travelbuddy.chat.entity.ChatRoom;
import com.travelbuddy.chat.entity.OnlineUsers;
import com.travelbuddy.chat.exception.RoomNotFoundException;
import com.travelbuddy.chat.model.Message;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;

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
        room.setMessageList(new ArrayList<>());
        return mongoTemplate.insert(room);
    }

    public List<Message> getAllMessagesForAChatRoom(String chatRoomId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(chatRoomId));
        ChatRoom chatRoom = mongoTemplate.findOne(query, ChatRoom.class);
        return Objects.isNull(chatRoom) ? List.of() : chatRoom.getMessageList();
    }

    public OnlineUsers saveOnlineUsersRoom(OnlineUsers onlineUsersSpace) {
        onlineUsersSpace.setUsernameList(new HashSet<>());
        return mongoTemplate.insert(onlineUsersSpace);
    }

    public Set<String> getAllOnlineUsersForRoom(String roomId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(roomId));
        OnlineUsers onlineUsersSpace = mongoTemplate.findOne(query, OnlineUsers.class);
        if (Objects.isNull(onlineUsersSpace))
            throw new RoomNotFoundException("This Room doesn't exist");
        return onlineUsersSpace.getUsernameList();
    }

    public void addUserToRoom(String roomId, String userName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(roomId));
        OnlineUsers onlineUsersSpace = mongoTemplate.findOne(query, OnlineUsers.class);
        if (Objects.isNull(onlineUsersSpace))
            throw new RoomNotFoundException("This Room doesn't exist");
        onlineUsersSpace.getUsernameList().add(userName);
        Update update = new Update();
        update.set("usernameList", onlineUsersSpace.getUsernameList());
        mongoTemplate.updateFirst(query, update, OnlineUsers.class);
    }

    public void removeUserFromRoom(String roomId, String userName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(roomId));
        OnlineUsers onlineUsersSpace = mongoTemplate.findOne(query, OnlineUsers.class);
        if (Objects.isNull(onlineUsersSpace))
            throw new RoomNotFoundException("This Room doesn't exist");
        onlineUsersSpace.getUsernameList().remove(userName);
        Update update = new Update();
        update.set("usernameList", onlineUsersSpace.getUsernameList());
        mongoTemplate.updateFirst(query, update, OnlineUsers.class);
    }


}
