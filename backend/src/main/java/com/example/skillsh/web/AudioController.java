package com.example.skillsh.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/audio")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AudioController {

    // The folder where voice messages will be saved locally
    private static final String UPLOAD_DIR = "uploads/voice-messages/";

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
}
