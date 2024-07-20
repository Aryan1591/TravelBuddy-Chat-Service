package com.travelbuddy.chat.entity;

import com.travelbuddy.chat.model.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "travelbuddy.chats")
@Builder
public class ChatRoom {
    @Id
    @Indexed(unique = true)
    private String roomId;
    private List<Message> messageList = new ArrayList<>();
}
