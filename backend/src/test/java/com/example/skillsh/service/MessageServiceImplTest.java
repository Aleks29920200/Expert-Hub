package com.example.skillsh.service;

import com.example.skillsh.domain.entity.Message;
import com.example.skillsh.repository.MessageRepo;
import com.example.skillsh.services.chat.ChatService;
import com.example.skillsh.services.message.MessageServiceImpl;
import com.example.skillsh.services.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock
    private MessageRepo messageRepo;

    @Mock
    private ChatService chatService;

    @Mock
    private UserService userService;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Test
    void testSaveMessage() {
        Message message = new Message();
        when(messageRepo.save(message)).thenReturn(message);

        Message savedMessage = messageService.saveMessage(message);

        assertNotNull(savedMessage);
        verify(messageRepo, times(1)).save(message);
    }

    @Test
    void testGetChatHistory_ShouldReverseList() {
        // Arrange
        Message oldMsg = new Message();
        oldMsg.setContent("Старо");
        Message newMsg = new Message();
        newMsg.setContent("Ново");

        // ВАЖНО: Трябва да върнем променим списък (ArrayList), защото Collections.reverse го променя
        List<Message> mockList = new ArrayList<>(List.of(newMsg, oldMsg));
        when(messageRepo.findRecentChatHistory(eq("user1"), eq("user2"), any(PageRequest.class)))
                .thenReturn(mockList);

        // Act
        List<Message> result = messageService.getChatHistory("user1", "user2");

        // Assert
        assertEquals(2, result.size());
        assertEquals("Старо", result.get(0).getContent()); // Проверяваме дали списъкът е обърнат
        assertEquals("Ново", result.get(1).getContent());
    }

    @Test
    void testEditMessage_Success() {
        Long messageId = 1L;
        Message existingMessage = new Message();
        existingMessage.setId(messageId);
        existingMessage.setContent("Старо съдържание");

        when(messageRepo.findById(messageId)).thenReturn(Optional.of(existingMessage));
        when(messageRepo.save(any(Message.class))).thenReturn(existingMessage);

        Message result = messageService.editMessage(messageId, "Ново съдържание");

        assertNotNull(result);
        assertEquals("Ново съдържание", result.getContent());
        assertTrue(result.isEdited());
        verify(messageRepo, times(1)).save(existingMessage);
    }

    @Test
    void testDeleteMessageById_Success() {
        Long messageId = 1L;
        Message message = new Message();
        message.setId(messageId);

        when(messageRepo.findById(messageId)).thenReturn(Optional.of(message));

        messageService.deleteMessageById(messageId);

        assertTrue(message.isIndicatorForDeletion());
        verify(messageRepo, times(1)).deleteById(messageId);
    }

    @Test
    void testFindRecentContactsForUser() {
        when(messageRepo.findRecentContactsForUser("testUser"))
                .thenReturn(List.of("contact1", "contact2"));

        List<Map<String, String>> result = messageService.findRecentContactsForUser("testUser");

        assertEquals(2, result.size());
        assertEquals("contact1", result.get(0).get("username"));
        assertEquals("contact2", result.get(1).get("username"));
    }
}



