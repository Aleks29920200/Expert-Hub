package com.example.skillsh.controllers;

import com.example.skillsh.web.AudioController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService; // <-- ДОБАВЕН ИМПОРТ
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AudioController.class)
@AutoConfigureMockMvc(addFilters = false)
class AudioControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void testUploadVoiceMessage_ShouldReturnOkAndFileUrl() throws Exception {
        // Arrange: Създаваме фалшив аудио файл
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",               // Името на параметъра в контролера (@RequestParam("file"))
                "test-audio.webm",    // Оригинално име на файла
                "audio/webm",         // MIME тип
                "dummy audio content".getBytes() // Съдържание на файла
        );

        // Act & Assert: Използваме multipart() вместо post()
        mockMvc.perform(multipart("/api/audio/upload")
                        .file(mockFile))
                .andExpect(status().isOk())
                // Проверяваме дали върнатият URL съдържа папката и името на файла
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/uploads/voice-messages/")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("test-audio.webm")));
    }

    @Test
    void testUploadVoiceMessage_EmptyFile_ShouldReturnBadRequest() throws Exception {
        // Arrange: Създаваме ПРАЗЕН файл
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.webm",
                "audio/webm",
                new byte[0]
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/audio/upload")
                        .file(emptyFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("File is empty"));
    }
}
