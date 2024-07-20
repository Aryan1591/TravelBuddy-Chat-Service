package com.travelbuddy.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "travelbuddy.onlineusers")
@Builder
public class OnlineUsers {
    @Id
    @Indexed(unique = true)
    private String id;

    private Set<String> usernameList = new HashSet<>();
}
