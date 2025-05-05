package com.example.streamingservice.Service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class AudioStorageService {

    @Autowired
    private BlobContainerClient blobContainerClient;

    public String uploadFile(MultipartFile file) {
        try {
            // Get BlobClient for the new file
            BlobClient blobClient = blobContainerClient.getBlobClient(file.getOriginalFilename());

            // Get the file's input stream and upload it
            try (InputStream inputStream = file.getInputStream()) {
                blobClient.upload(inputStream, file.getSize(), true);
            }

            // Optionally set content type metadata (Azure doesn't set it automatically)
            blobClient.setHttpHeaders(new com.azure.storage.blob.models.BlobHttpHeaders()
                    .setContentType("audio/mpeg"));

            // Return public or API-accessible URL (you'll need to handle permissions or SAS tokens)
            return "https://<your-storage-account>.blob.core.windows.net/"
                    + blobContainerClient.getBlobContainerName() + "/"
                    + file.getOriginalFilename();

        } catch (IOException e) {
            throw new RuntimeException("Failed to read file content", e);
        } catch (BlobStorageException e) {
            throw new RuntimeException("Azure Blob Storage error: " + e.getMessage(), e);
        }
    }
}
