package com.example.skillsh.domain.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MessageDto {
        private String id;
        private String chatId;
        private String sender;
        private String receiver;
        private String content;
        // Add this with your other fields
        // Add this to your DTO
        private String replyToMessageId;// <-- ADD THIS
        private boolean edited;               // <-- ADD THIS
        private boolean indicatorForDeletion;

}
