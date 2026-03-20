package com.example.skillsh.web;

import com.example.skillsh.domain.dto.chat.MessageDto;
import com.example.skillsh.domain.entity.Message; // Assuming this entity exists
import com.example.skillsh.domain.view.UserView; // Assuming this view exists
import com.example.skillsh.services.chat.BlockService; // Crucial for blocking logic
import com.example.skillsh.services.message.MessageService; // For message persistence
import com.example.skillsh.services.user.UserService; // For user-related operations
import org.modelmapper.ModelMapper; // For mapping DTOs/entities
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final UserService userService; // Changed from UserServiceImpl to UserService interface
    private final BlockService blockService;
    private final ModelMapper mapper = new ModelMapper();
    private static final String UPLOAD_DIR = "uploads/voice-messages/";// Final for immutability

    @Autowired // Constructor injection is preferred
    public ChatController(SimpMessagingTemplate messagingTemplate, MessageService messageService, UserService userService, BlockService blockService) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
        this.userService = userService;
        this.blockService = blockService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVoiceMessage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try {
            // 1. Create the directory if it doesn't exist yet
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 2. Generate a unique file name so users don't overwrite each other's audio
            String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "audio.webm";
            String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFilename;

            // 3. Save the file to the server
            Path filePath = Paths.get(UPLOAD_DIR + uniqueFileName);
            Files.write(filePath, file.getBytes());

            // 4. Return the path/URL so the Angular frontend can send it in the chat
            // (Note: To play this back, we will configure a resource handler next)
            String fileUrl = "/uploads/voice-messages/" + uniqueFileName;

            return ResponseEntity.ok(fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save audio file");
        }
    }
    /**
     * Handles incoming chat messages.
     * Applies blocking logic before saving and sending messages.
     *
     * @param message The chat message payload.
     */
    @MessageMapping("/chat")
    public void sendMessage(@Payload Message message) {
        String senderUsername = message.getSender();
        String receiverUsername = message.getReceiver(); // Assuming all messages are private for now

        if (senderUsername == null || senderUsername.isEmpty()) {
            System.err.println("Received message with null/empty sender.");
            return;
        }
        if (receiverUsername == null || receiverUsername.isEmpty()) {
            System.err.println("Received message with null/empty receiver, cannot send private message.");
            // For public messages, you'd send to a topic like "/topic/public"
            // For this app, we assume private chats with a selected receiver.
            return;
        }

        try {
            // --- Blocking Logic ---
            // Check if sender has blocked receiver
            if (blockService.isBlocked(senderUsername, receiverUsername)) {
                System.out.println("Blocked: " + senderUsername + " has blocked " + receiverUsername + ". Message not sent.");
                messagingTemplate.convertAndSendToUser(senderUsername, "/queue/errors",
                        "You cannot send messages to " + receiverUsername + " because you have blocked them.");
                return; // IMPORTANT: Stop processing the message
            }

            // Check if receiver has blocked sender
            if (blockService.isBlocked(receiverUsername, senderUsername)) {
                System.out.println("Blocked: " + receiverUsername + " has blocked " + senderUsername + ". Message not sent.");
                messagingTemplate.convertAndSendToUser(senderUsername, "/queue/errors",
                        receiverUsername + " has blocked you, so you cannot send messages to them.");
                return; // IMPORTANT: Stop processing the message
            }

            // If not blocked, save the message
            messageService.saveMessage(message);

            // Send the message to the receiver
            messagingTemplate.convertAndSendToUser(receiverUsername, "/queue/messages", message);
            // Also send the message back to the sender for their own chat view update
            messagingTemplate.convertAndSendToUser(senderUsername, "/queue/messages", message);

        } catch (Exception e) {
            System.err.println("Error processing chat message with blocking logic: " + e.getMessage());
            e.printStackTrace();
            // Send a general error back to the sender
            messagingTemplate.convertAndSendToUser(senderUsername, "/queue/errors", "Failed to send message due to server error.");
        }
    }

    /**
     * Handles message editing requests.
     *
     * @param message The message with updated content.
     */
    @MessageMapping("/edit")
    public void editMessage(@Payload Message message) {
        messageService.getMessageById(message.getId()).ifPresent(m -> {
            // Ensure the message is being edited by its original sender
            if (m.getSender().equals(message.getSender())) {
                m.setContent(message.getContent());
                m.setEdited(true);
                messageService.saveMessage(m);
                // Send updated message to both sender and receiver
                messagingTemplate.convertAndSendToUser(m.getReceiver(), "/queue/messages", m);
                messagingTemplate.convertAndSendToUser(m.getSender(), "/queue/messages", m);
            } else {
                System.err.println("Unauthorized edit attempt for message " + message.getId() + " by " + message.getSender());
                messagingTemplate.convertAndSendToUser(message.getSender(), "/queue/errors", "You are not authorized to edit this message.");
            }
        });
    }

    /**
     * Handles message deletion requests (soft delete).
     *
     * @param message The message to be deleted (contains ID, sender, receiver).
     */
    @MessageMapping("/delete")
    public void deleteMessage(@Payload Message message) {
        messageService.getMessageById(message.getId()).ifPresent(m -> {
            // Ensure the message is being deleted by its original sender
            if (m.getSender().equals(message.getSender())) {
                m.setIndicatorForDeletion(true); // Assuming this flag exists in your Message entity
                m.setContent("This message was deleted."); // Update content for soft delete
                messageService.saveMessage(m); // Save the updated message (soft delete)
                // Send updated message to both sender and receiver
                messagingTemplate.convertAndSendToUser(m.getReceiver(), "/queue/messages", m);
                messagingTemplate.convertAndSendToUser(m.getSender(), "/queue/messages", m);
            } else {
                System.err.println("Unauthorized delete attempt for message " + message.getId() + " by " + message.getSender());
                messagingTemplate.convertAndSendToUser(message.getSender(), "/queue/errors", "You are not authorized to delete this message.");
            }
        });
    }

    /**
     * Retrieves the block status for a given username.
     *
     * @param username The username to check block status for.
     * @return A map containing lists of users blocked by this user ("blocked") and users who blocked this user ("blockedBy").
     */
    @GetMapping("/{username}/block-status")
    public ResponseEntity<Map<String, List<String>>> getBlockStatus(@PathVariable String username) {
        List<String> blocked = blockService.getBlockedUsers(username); // Users 'username' has blocked
        List<String> blockedBy = blockService.getBlockedByUsers(username); // Users who have blocked 'username'

        Map<String, List<String>> response = new HashMap<>();
        response.put("blocked", blocked);
        response.put("blockedBy", blockedBy);

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves chat messages between two users.
     *
     * @param from Sender username.
     * @param to   Receiver username.
     * @return ResponseEntity with a list of messages.
     */
    @GetMapping("/messages/{from}/{to}")
    public ResponseEntity<Object> getMessages(@PathVariable String from, @PathVariable String to) {
        // Assuming messageService.findBySenderAndReceiverOrReceiverAndSender filters out deleted messages
        return ResponseEntity.ok(messageService.findBySenderAndReceiverOrReceiverAndSender(from, to, from, to));
    }

    /**
     * Handles WebRTC call offers.
     * Applies blocking logic before relaying the offer.
     *
     * @param message The call offer message payload.
     */
    @MessageMapping("/call/offer")
    public void handleOffer(@Payload Message message) {
        String caller = message.getSender();
        String callee = message.getReceiver();

        // --- Blocking Logic for Calls ---
        // Check if caller has blocked callee
        if (blockService.isBlocked(caller, callee)) {
            System.out.println("Call from " + caller + " to " + callee + " blocked: Caller blocked Callee.");
            messagingTemplate.convertAndSendToUser(caller, "/queue/call/reject",
                    Map.of("from", callee, "reason", "You have blocked " + callee));
            return;
        }

        // Check if callee has blocked caller
        if (blockService.isBlocked(callee, caller)) {
            System.out.println("Call from " + caller + " to " + callee + " blocked: Callee blocked Caller.");
            messagingTemplate.convertAndSendToUser(caller, "/queue/call/reject",
                    Map.of("from", callee, "reason", callee + " has blocked you"));
            return;
        }

        // If not blocked, proceed to send the offer
        messagingTemplate.convertAndSendToUser(
                message.getReceiver(),
                "/queue/call/offer",
                message
        );
        System.out.println("Call offer relayed from " + caller + " to " + callee);
    }

    // Other call signaling methods (answer, ice, end, reject) would follow similar patterns
    // but typically don't need blocking checks as the initial offer is the gatekeeper.

    @MessageMapping("/call/answer")
    public void handleAnswer(@Payload Message message) {
        messagingTemplate.convertAndSendToUser(
                message.getReceiver(),
                "/queue/call/answer",
                message
        );
    }

    @MessageMapping("/call/ice")
    public void handleIceCandidate(@Payload Message message) {
        messagingTemplate.convertAndSendToUser(
                message.getReceiver(),
                "/queue/call/ice",
                message
        );
    }

    @MessageMapping("/call/end")
    public void handleCallEnd(@Payload Message message) {
        messagingTemplate.convertAndSendToUser(
                message.getReceiver(),
                "/queue/call/end",
                message
        );
    }

    @MessageMapping("/call/reject")
    public void handleCallReject(@Payload Message message) {
        messagingTemplate.convertAndSendToUser(
                message.getReceiver(),
                "/queue/call/reject",
                message
        );
    }
    @GetMapping("/api/messages/{sender}/{receiver}")
    public ResponseEntity<List<MessageDto>> getChatHistory(@PathVariable String sender, @PathVariable String receiver) {

        List<Message> history = messageService.getChatHistory(sender, receiver);


        // Convert Entity -> DTO
        List<MessageDto> historyDtos = new ArrayList<>();
        for (int i = 0; i < history.size(); i++) {
            Message msg = history.get(i);
            MessageDto chat = null;
            if (msg.getReplyToMessageId() != null) {
                chat = new MessageDto(
                        msg.getId() != null ? msg.getId().toString() : "",
                        "chat",
                        msg.getSender(),   // ✅ It is already a String, just get it
                        msg.getReceiver(), // ✅ It is already a String, just get it
                        msg.getContent(),
                        String.valueOf(msg.getReplyToMessageId()),     // <-- ADD THIS
                        msg.isEdited(),                // <-- ADD THIS
                        msg.isIndicatorForDeletion()
                );
            } else {
                chat = new MessageDto(
                        msg.getId() != null ? msg.getId().toString() : "",
                        "chat",
                        msg.getSender(),   // ✅ It is already a String, just get it
                        msg.getReceiver(), // ✅ It is already a String, just get it
                        msg.getContent(),
                        "",     // <-- ADD THIS
                        msg.isEdited(),                // <-- ADD THIS
                        msg.isIndicatorForDeletion()
                );
            }
            historyDtos.add(chat);
        }

        return ResponseEntity.ok(historyDtos);
    }
    @GetMapping("/api/chat/contacts/{username}")
    public ResponseEntity<List<Map<String, String>>> getRecentContacts(@PathVariable String username) {
        List<Map<String, String>> contacts = messageService.findRecentContactsForUser(username);
        return ResponseEntity.ok(contacts);
    }
    @GetMapping("/user/videochat/{userId}")
    public ModelAndView videoCall(@PathVariable("userId") Long userId, Principal principal, ModelAndView modelAndView) {
        modelAndView.setViewName("video-call");
        modelAndView.addObject("username", principal.getName());
        modelAndView.addObject("userId", userId); // ПРОМЕНЕНО: Подаваме ID-то на човека, на когото звъним
        return modelAndView;
    }

    @ModelAttribute("user")
    public UserView user() {
        return new UserView();
    }
}