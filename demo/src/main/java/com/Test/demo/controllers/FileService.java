package com.Test.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;


import org.springframework.stereotype.Service;

@Service
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    private static final String UPLOAD_DIR = "uploads/";

    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String filePath = UPLOAD_DIR + fileName;

        try (InputStream inputStream = file.getInputStream()) {
            File dest = new File(filePath);
            Files.copy(inputStream, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            handleFileServiceError(e);
        }

        return filePath;
    }

    private void handleFileServiceError(Exception e) throws IOException {
        String errorMessage = "Error saving file";
        logger.error(errorMessage, e);
        throw new IOException(errorMessage, e);
    }
}
