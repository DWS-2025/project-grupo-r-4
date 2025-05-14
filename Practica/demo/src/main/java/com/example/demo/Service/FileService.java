package com.example.demo.Service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.UUID;

@Service
public class FileService {

    private static final Path FILES_FOLDER = Paths.get(System.getProperty("user.dir"), "files");

    public String createFile(MultipartFile multiPartFile) {

        String originalName = multiPartFile.getOriginalFilename();

        if(!originalName.matches(".*\\.(pdf|txt)")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The url is not a file resource");
        }


        try {
            String safeFilePath = sanitize(multiPartFile.getOriginalFilename());
            Path imagePath = Paths.get(safeFilePath);
            multiPartFile.transferTo(imagePath);
            multiPartFile.transferTo(imagePath);
        } catch (Exception ex) {
            System.err.println(ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't save file locally", ex);
        }

        return originalName;
    }

    public Resource getFile(String fileName) throws IOException {
        Path filePath = FILES_FOLDER.resolve(sanitize(fileName));
        try {
            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't get local file");
        }
    }


    public String sanitize(String originalFileName) throws IOException {

        Files.createDirectories(FILES_FOLDER);
        if (originalFileName.contains("..")) {
            throw new IOException("Filename contains invalid characters: .. (are you using ../ ?)");
        }
        if (originalFileName.contains("/")) {
            throw new IOException("Filename contains invalid characters: / (are you using ../ ?)");
        }
        if (originalFileName.contains("\\")) {
            throw new IOException("Filename contains invalid characters: \\ (are you using ../ ?)");
        }

        Path filePath =FILES_FOLDER.resolve(originalFileName);
        var pathFile = filePath.toFile().getCanonicalPath().toString();
        var pathFolder = FILES_FOLDER.toAbsolutePath().toString();
        if(pathFile.startsWith(pathFolder)){
            return pathFile;
        }else{
            throw new IOException("File is outside of the allowed directory (are you using ../ ?)");
        }

    }
    public void deleteFile(String file_url) {
        String[] tokens = file_url.split("/");
        String file_name = tokens[tokens.length -1 ];

        try {
            FILES_FOLDER.resolve(file_name).toFile().delete();
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't delete local file");
        }
    }


}