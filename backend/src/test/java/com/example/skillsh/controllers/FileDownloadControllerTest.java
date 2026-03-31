package com.example.skillsh.controllers;

import com.example.skillsh.domain.dto.file.FileDownloadModel;
import com.example.skillsh.services.file.FileService;
import com.example.skillsh.web.FileDownloadController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileDownloadController.class)
@AutoConfigureMockMvc(addFilters = false)
class FileDownloadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void testShowFile_ShouldReturnShowView() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/show/{fileId}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("show")) // Трябва да зареди show.html
                .andExpect(model().attribute("fileId", 1));
    }

    @Test
    void testDownloadFile_ShouldReturnFileAndHeaders() throws Exception {
        // Arrange
        int fileId = 99;
        byte[] fakeFileContent = "Това е тестов файл".getBytes();

        FileDownloadModel mockFile = new FileDownloadModel();
        mockFile.setName("document.txt");
        mockFile.setContentType("text/plain");
        mockFile.setDocument(fakeFileContent);

        when(fileService.download(fileId)).thenReturn(mockFile);

        // Act & Assert
        mockMvc.perform(get("/download/{fileId}", fileId))
                .andExpect(status().isOk())
                // Проверяваме дали браузърът ще разпознае типа на файла
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "text/plain"))
                // Проверяваме дали браузърът ще започне изтегляне с правилното име
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=document.txt"))
                // Проверяваме дали съдържанието на файла съвпада
                .andExpect(content().bytes(fakeFileContent));

        verify(fileService, times(1)).download(fileId);
    }
}


