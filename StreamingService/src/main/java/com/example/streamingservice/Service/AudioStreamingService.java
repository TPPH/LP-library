package com.example.streamingservice.Service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
public class AudioStreamingService {

    @Autowired
    private BlobContainerClient blobContainerClient;

    public ResponseEntity<byte[]> streamAudio(String fileName) {
        try {
            // Get the BlobClient for the file
            BlobClient blobClient = blobContainerClient.getBlobClient(fileName);

            // Check if the blob exists
            if (!blobClient.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("File not found".getBytes());
            }

            // Open input stream
            InputStream inputStream = blobClient.openInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            byte[] audioContent = outputStream.toByteArray();

            // Return the audio content with proper headers
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
                    .body(audioContent);

        } catch (BlobStorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Azure error: " + e.getMessage()).getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error streaming the audio".getBytes());
        }
    }
}
