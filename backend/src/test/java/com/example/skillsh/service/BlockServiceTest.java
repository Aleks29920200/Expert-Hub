package com.example.skillsh.service;

import com.example.skillsh.domain.entity.Block;
import com.example.skillsh.repository.BlockRepository;
import com.example.skillsh.services.chat.BlockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockServiceTest {

    @Mock
    private BlockRepository blockRepository;

    @InjectMocks
    private BlockService blockService;

    @Test
    void testBlockUser_WhenNotBlocked_ShouldSaveBlock() {
        // Arrange
        String blocker = "user1";
        String blocked = "user2";

        // Указваме, че потребителят все още НЕ е блокиран
        when(blockRepository.existsByBlockerUsernameAndBlockedUsername(blocker, blocked)).thenReturn(false);

        // Act
        blockService.blockUser(blocker, blocked);

        // Assert
        ArgumentCaptor<Block> blockCaptor = ArgumentCaptor.forClass(Block.class);
        verify(blockRepository, times(1)).save(blockCaptor.capture());

        Block savedBlock = blockCaptor.getValue();
        assertEquals("user1", savedBlock.getBlockerUsername());
        assertEquals("user2", savedBlock.getBlockedUsername());
    }

    @Test
    void testBlockUser_WhenAlreadyBlocked_ShouldNotSave() {
        // Arrange
        String blocker = "user1";
        String blocked = "user2";

        // Указваме, че потребителят ВЕЧЕ Е блокиран
        when(blockRepository.existsByBlockerUsernameAndBlockedUsername(blocker, blocked)).thenReturn(true);

        // Act
        blockService.blockUser(blocker, blocked);

        // Assert
        // Уверяваме се, че методът save() НЕ е извикан, защото вече има блок
        verify(blockRepository, never()).save(any(Block.class));
    }

    @Test
    void testUnblockUser() {
        // Arrange
        String blocker = "user1";
        String blocked = "user2";

        // Act
        blockService.unblockUser(blocker, blocked);

        // Assert
        verify(blockRepository, times(1)).forceUnblockUser(blocker, blocked);
    }

    @Test
    void testGetBlockedUsers() {
        // Arrange
        String blocker = "user1";
        Block block1 = new Block(blocker, "user2");
        Block block2 = new Block(blocker, "user3");

        when(blockRepository.findByBlockerUsername(blocker)).thenReturn(List.of(block1, block2));

        // Act
        List<String> blockedUsers = blockService.getBlockedUsers(blocker);

        // Assert
        assertEquals(2, blockedUsers.size());
        assertTrue(blockedUsers.contains("user2"));
        assertTrue(blockedUsers.contains("user3"));
        verify(blockRepository, times(1)).findByBlockerUsername(blocker);
    }

    @Test
    void testGetBlockedByUsers() {
        // Arrange
        String blocked = "user2";
        Block block1 = new Block("user1", blocked);
        Block block2 = new Block("user3", blocked);

        when(blockRepository.findByBlockedUsername(blocked)).thenReturn(List.of(block1, block2));

        // Act
        List<String> blockedByUsers = blockService.getBlockedByUsers(blocked);

        // Assert
        assertEquals(2, blockedByUsers.size());
        assertTrue(blockedByUsers.contains("user1"));
        assertTrue(blockedByUsers.contains("user3"));
        verify(blockRepository, times(1)).findByBlockedUsername(blocked);
    }
}


