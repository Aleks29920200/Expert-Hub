package com.example.skillsh.services.message;

import com.example.skillsh.domain.entity.Message;
import com.example.skillsh.repository.MessageRepo;
import com.example.skillsh.services.chat.ChatService;
import com.example.skillsh.services.user.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {
    private MessageRepo messageRepo;
    private ChatService chatService;
    private ModelMapper mapper=new ModelMapper();
    private UserService userService;
@Autowired
    public MessageServiceImpl(MessageRepo messageRepo, ChatService chatService, UserService userService) {
        this.messageRepo = messageRepo;
    this.chatService = chatService;
    this.userService = userService;
}
    @Override
    public Message saveMessage(Message message) {
        return messageRepo.save(message);
    }

    @Override
    public Optional<Message> getMessageById(Long id) {
        return messageRepo.findById(id);
    }
    @Override
    public List<Message> getChatHistory(String user1, String user2) {
        List<Message> recentMessages = messageRepo.findRecentChatHistory(user1, user2, PageRequest.of(0, 50));

        // Reverse them so the oldest of the 50 is at the top, and newest at the bottom
        Collections.reverse(recentMessages);

        return recentMessages;
    }
@Override
public Message editMessage(Long id, String newContent) {
        Optional<Message> optional = messageRepo.findById(id);
        if (optional.isPresent()) {
            Message message = optional.get();
            message.setContent(newContent);
            message.setEdited(true);
            return messageRepo.save(message);
        }
        return null;
    }
@Override
public Message deleteMessageById(Long id) {
    Optional<Message> optional = messageRepo.findById(id);
    if (optional.isPresent()) {
        Message message = optional.get();
        message.setIndicatorForDeletion(true); // 1. Маркираш го като изтрито (Soft Delete)
        messageRepo.deleteById(message.getId()); // 2. Директно го триеш от базата завинаги (Hard Delete)
        return null;
    }
    return null;
}
@Override
public List<Map<String, String>> findRecentContactsForUser(String username) {
        // Взимаме уникалните имена от базата
        List<String> contactUsernames = messageRepo.findRecentContactsForUser(username);

        // Преобразуваме ги в списък от Map обекти (JSON-подобна структура)
        return contactUsernames.stream()
                .map(contactName -> Map.of("username", contactName))
                .collect(Collectors.toList());
    }
    @Override
    public List<Message> findBySenderAndReceiverOrReceiverAndSender(String sender1, String receiver1, String receiver2, String sender2) {
        return this.messageRepo.findBySenderAndReceiverOrReceiverAndSender(sender1,receiver1,receiver2,sender2);
    }

}
