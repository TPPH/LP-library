package com.example.streamingservice.Service;


import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AudioStorageService {

    @Autowired
    private Storage storage;

    private static final String BUCKET_NAME = "your-bucket-name";

    public String uploadFile(MultipartFile file) {
        try {
            // Create BlobId and BlobInfo for the file
            BlobId blobId = BlobId.of(BUCKET_NAME, file.getOriginalFilename());
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("audio/mpeg").build();
            // Upload file content
            storage.create(blobInfo, file.getBytes());
            return "https://your-server-url/api/audio/stream/" + file.getOriginalFilename();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to GCS", e);
        }
    }
}

