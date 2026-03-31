package com.example.skillsh.service;

import com.example.skillsh.domain.entity.ChatRoom;
import com.example.skillsh.repository.ChatRepo;
import com.example.skillsh.services.chat.ChatService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRepo chatRoomRepository;

    @InjectMocks
    private ChatService chatService;

    @Test
    void testGetChatRoomId_WhenRoomExists_ShouldReturnChatId() {
        // Arrange
        String senderId = "user1";
        String recipientId = "user2";

        ChatRoom existingRoom = ChatRoom.builder()
                .chatId("user1_user2")
                .senderId(senderId)
                .recipientId(recipientId)
                .build();

        // Указваме, че стаята вече съществува
        when(chatRoomRepository.findBySenderIdAndAndRecipientId(senderId, recipientId))
                .thenReturn(Optional.of(existingRoom));

        // Act
        Optional<String> chatId = chatService.getChatRoomId(senderId, recipientId, true);

        // Assert
        assertTrue(chatId.isPresent());
        assertEquals("user1_user2", chatId.get());
        // Проверяваме, че НЕ е създавана нова стая
        verify(chatRoomRepository, never()).save(any(ChatRoom.class));
    }

    @Test
    void testGetChatRoomId_WhenRoomDoesNotExist_AndCreateIsFalse_ShouldReturnEmpty() {
        // Arrange
        String senderId = "user1";
        String recipientId = "user2";

        // Указваме, че стаята НЕ съществува
        when(chatRoomRepository.findBySenderIdAndAndRecipientId(senderId, recipientId))
                .thenReturn(Optional.empty());

        // Act (подаваме createNewRoomIfNotExists = false)
        Optional<String> chatId = chatService.getChatRoomId(senderId, recipientId, false);

        // Assert
        assertFalse(chatId.isPresent());
        verify(chatRoomRepository, never()).save(any(ChatRoom.class));
    }

    @Test
    void testGetChatRoomId_WhenRoomDoesNotExist_AndCreateIsTrue_ShouldCreateAndReturnChatId() {
        // Arrange
        String senderId = "user1";
        String recipientId = "user2";

        // Указваме, че стаята НЕ съществува
        when(chatRoomRepository.findBySenderIdAndAndRecipientId(senderId, recipientId))
                .thenReturn(Optional.empty());

        // Act (подаваме createNewRoomIfNotExists = true)
        Optional<String> chatId = chatService.getChatRoomId(senderId, recipientId, true);

        // Assert
        assertTrue(chatId.isPresent());
        assertEquals("user1_user2", chatId.get());

        // Тъй като се създават ДВЕ стаи (от A до B и от B до A),
        // проверяваме дали save() е извикан точно 2 пъти
        ArgumentCaptor<ChatRoom> chatRoomCaptor = ArgumentCaptor.forClass(ChatRoom.class);
        verify(chatRoomRepository, times(2)).save(chatRoomCaptor.capture());

        List<ChatRoom> savedRooms = chatRoomCaptor.getAllValues();

        // Проверка на първата стая (sender -> recipient)
        assertEquals("user1_user2", savedRooms.get(0).getChatId());
        assertEquals("user1", savedRooms.get(0).getSenderId());
        assertEquals("user2", savedRooms.get(0).getRecipientId());

        // Проверка на втората стая (recipient -> sender)
        assertEquals("user1_user2", savedRooms.get(1).getChatId());
        assertEquals("user2", savedRooms.get(1).getSenderId());
        assertEquals("user1", savedRooms.get(1).getRecipientId());
    }
}


