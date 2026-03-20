package com.example.skillsh.web;

import com.example.skillsh.services.chat.BlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/blocks") // Matches your Angular block.service.ts
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class BlockController {

    private final BlockService blockService;

    @Autowired
    public BlockController(BlockService blockService) {
        this.blockService = blockService;
    }

    // 1. Used when you click "Block" in Angular
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> blockUser(@RequestBody Map<String, String> payload) {
        String blocker = payload.get("blocker");
        String blocked = payload.get("blocked");
        blockService.blockUser(blocker, blocked);
        return ResponseEntity.ok("User blocked successfully.");
    }

    // 2. Used when you click "Unblock" in Angular
    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> unblockUser(
            @RequestParam("blocker") String blocker,
            @RequestParam("blocked") String blocked) {
        blockService.unblockUser(blocker, blocked);
        return ResponseEntity.ok("User unblocked successfully.");
    }

    // 3. Fixes the 404 Error! Used when loading a chat to check block status
    @GetMapping("/is-blocked")
    public ResponseEntity<Boolean> isBlocked(
            @RequestParam("source") String source,
            @RequestParam("target") String target) {
        boolean isBlocked = blockService.isBlocked(source, target);
        return ResponseEntity.ok(isBlocked);
    }
}