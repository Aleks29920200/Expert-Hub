package com.example.skillsh.service;

import com.example.skillsh.domain.dto.file.FileDownloadModel;
import com.example.skillsh.domain.dto.file.FileUploadModel;
import com.example.skillsh.domain.entity.FileEntity;
import com.example.skillsh.repository.FileRepository;
import com.example.skillsh.services.file.FileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileService fileService;

    @Test
    void testGetCurrentAddedImage_Success() {
        FileEntity mockFile = new FileEntity();
        mockFile.setId(10);
        when(fileRepository.findTopByOrderByIdDesc()).thenReturn(Optional.of(mockFile));

        FileEntity result = fileService.getCurrentAddedImage();

        assertNotNull(result);
        assertEquals(10, result.getId());
    }

    @Test
    void testGetCurrentAddedImage_ThrowsException() {
        when(fileRepository.findTopByOrderByIdDesc()).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fileService.getCurrentAddedImage();
        });

        assertEquals("Няма намерени файлове", exception.getMessage());
    }

    @Test
    void testUpload() throws IOException {
        // Arrange
        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        when(mockMultipartFile.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(mockMultipartFile.getContentType()).thenReturn("image/png");
        when(mockMultipartFile.getOriginalFilename()).thenReturn("test.png");

        FileUploadModel model = new FileUploadModel(mockMultipartFile);


        FileEntity savedFile = new FileEntity();
        savedFile.setId(5);

        when(fileRepository.saveAndFlush(any(FileEntity.class))).thenReturn(savedFile);

        // Act
        int resultId = fileService.upload(model);

        // Assert
        assertEquals(5, resultId);
        verify(fileRepository, times(1)).saveAndFlush(any(FileEntity.class));
    }

    @Test
    void testDownload_Success() {
        // Arrange
        FileEntity mockFile = new FileEntity();
        mockFile.setId(1);
        mockFile.setContentType("image/jpeg");
        mockFile.setFileName("avatar.jpg");
        mockFile.setFileData(new byte[]{10, 20});

        when(fileRepository.findById(1)).thenReturn(Optional.of(mockFile));

        // Act
        FileDownloadModel result = fileService.download(1);

        // Assert
        assertNotNull(result);
        assertEquals("image/jpeg", result.getContentType());
        assertEquals("avatar.jpg", result.getName());
        assertArrayEquals(new byte[]{10, 20}, result.getDocument());
    }

    @Test
    void testDownload_ThrowsExceptionWhenNotFound() {
        when(fileRepository.findById(99)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            fileService.download(99);
        });

        assertEquals("File99 not found!", exception.getMessage());
    }
}
