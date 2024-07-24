package com.travelbuddy.chat.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class ActiveUsersService {

    private final Map<String, Set<String>> activeUsers = new HashMap<>();

    public void addUserToRoom(String roomId, String username) {
        activeUsers.computeIfAbsent(roomId, k -> new HashSet<>()).add(username);
    }

    public void removeUserFromRoom(String roomId, String username) {
        Set<String> users = activeUsers.get(roomId);
        if (users != null) {
            users.remove(username);
            if (users.isEmpty()) {
                activeUsers.remove(roomId);
            }
        }
    }

    public Set<String> getActiveUsers(String roomId) {
        return activeUsers.getOrDefault(roomId, new HashSet<>());
    }
}
