package com.travelbuddy.chat.controller;

import com.travelbuddy.chat.entity.OnlineUsers;
import com.travelbuddy.chat.exception.RoomNotFoundException;
import com.travelbuddy.chat.service.MongoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5001"})
@Slf4j
@RequestMapping("/onlineUsers")
public class OnlineUsersController {

    @Autowired
    MongoService mongoService;

    @GetMapping("/{roomId}")
    public ResponseEntity<Set<String>> getAllOnlineUsersForARoom(@PathVariable String roomId) {
        try {
            return new ResponseEntity<>(mongoService.getAllOnlineUsersForRoom(roomId), HttpStatus.OK);
        } catch (RoomNotFoundException exc) {
            return new ResponseEntity<>(new HashSet<>(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{roomId}/addUser")
    public ResponseEntity<String> addUserToRoom(@PathVariable String roomId, @RequestBody String username) {
        try {
            mongoService.addUserToRoom(roomId, username);
        } catch (RoomNotFoundException exc) {
            return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("User added successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{roomId}/removeUser")
    public ResponseEntity<String> removeUserFromRoom(@PathVariable String roomId, @RequestBody String username) {
        try {
            mongoService.removeUserFromRoom(roomId, username);
        } catch (RoomNotFoundException exc) {
            return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("User removed successfully", HttpStatus.OK);
    }

    @PostMapping()
    public OnlineUsers buildOnlineUserSpace() {
        OnlineUsers onlineUsersSpace = OnlineUsers.builder()
                .id("e6542efc-88ce-4a3f-a2e5-75eebe9b4e22")
                .build();
        return mongoService.saveOnlineUsersRoom(onlineUsersSpace);
    }
}
