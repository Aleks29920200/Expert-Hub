package com.example.skillsh.controllers;

import com.example.skillsh.domain.dto.file.FileUploadModel;
import com.example.skillsh.services.file.FileService;
import com.example.skillsh.web.FileUploadController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileUploadController.class)
@AutoConfigureMockMvc(addFilters = false)
class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    // ЗАДЪЛЖИТЕЛНИЯТ МОК ЗА СЕКЮРИТИТО
    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void testUploadModel_ShouldReturn200AndFileId() throws Exception {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile(
                "img", // Трябва да съвпада с полето в FileUploadModel (ако се казва img)
                "document.pdf",
                "application/pdf",
                "dummy content".getBytes()
        );

        when(fileService.upload(any(FileUploadModel.class))).thenReturn(42);

        // Act & Assert
        mockMvc.perform(multipart("/api/upload")
                        .file(mockFile)
                        .param("title", "My Title")) // Симулираме подаване на допълнителни текстови полета
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("File uploaded successfully"))
                .andExpect(jsonPath("$.fileId").value(42));

        verify(fileService, times(1)).upload(any(FileUploadModel.class));
    }
}

