package com.example.skillsh.services.chat;

import com.example.skillsh.domain.entity.Block;
import com.example.skillsh.repository.BlockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlockService {

    private final BlockRepository blockRepository;

    @Autowired
    public BlockService(BlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

@Transactional
    public void blockUser(String blocker, String blocked) {
        if (!isBlocked(blocker, blocked)) {
            Block block = new Block(blocker, blocked);
            blockRepository.save(block);
        }
    }



    public boolean isBlocked(String blocker, String blocked) {
        return blockRepository.existsByBlockerUsernameAndBlockedUsername(blocker, blocked);
    }
    @Transactional
    public void unblockUser(String blocker, String blocked) {
        blockRepository.forceUnblockUser(blocker, blocked);
    }
    public List<String> getBlockedUsers(String username) {
        // Find all records where this user is the blocker
        // Assuming your repository has: List<Block> findByBlockerUsername(String blockerUsername);
        return blockRepository.findByBlockerUsername(username).stream()
                .map(Block::getBlockedUsername)
                .collect(Collectors.toList());
    }

    public List<String> getBlockedByUsers(String username) {
        // Find all records where this user is the blocked one
        // Assuming your repository has: List<Block> findByBlockedUsername(String blockedUsername);
        return blockRepository.findByBlockedUsername(username).stream()
                .map(Block::getBlockerUsername)
                .collect(Collectors.toList());
    }
}