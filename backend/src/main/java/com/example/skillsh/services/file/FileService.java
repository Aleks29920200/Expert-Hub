package com.example.skillsh.services.file;

import com.example.skillsh.domain.dto.file.FileDownloadModel;
import com.example.skillsh.domain.dto.file.FileUploadModel;
import com.example.skillsh.domain.entity.FileEntity;
import com.example.skillsh.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileService {

    private FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {

        this.fileRepository = fileRepository;
    }
    public FileEntity getCurrentAddedImage() {
        return this.fileRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new RuntimeException("Няма намерени файлове"));
    }
    public int upload(FileUploadModel model) throws IOException {
        MultipartFile img = model.getImg();

        FileEntity newFile = new FileEntity();

        newFile.setFileData(img.getBytes());
        newFile.setContentType(img.getContentType());
        newFile.setFileName(img.getOriginalFilename());

        return fileRepository.saveAndFlush(newFile).getId();
    }




    public FileDownloadModel download(int fileId) {
        FileEntity file = fileRepository.findById(fileId).orElseThrow(() -> new IllegalArgumentException("File" + fileId + " not found!"));

        return new FileDownloadModel()
                .setContentType(file.getContentType())
                .setName(file.getFileName()).
                setDocument(file.getFileData());
    }


}

