package com.example.skillsh.services.message;

import com.example.skillsh.domain.entity.Message;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public interface MessageService{


    Message saveMessage(Message message);

    Optional<Message> getMessageById(Long id);

    List<Message> getChatHistory(String user1, String user2);

    Message editMessage(Long id, String newContent);

    Message deleteMessageById(Long id);

    List<Map<String, String>> findRecentContactsForUser(String username);

    List<Message> findBySenderAndReceiverOrReceiverAndSender(
            String sender1, String receiver1,
            String receiver2, String sender2
    );
}
