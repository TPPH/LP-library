package com.example.streamingservice.Service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.sas.*;
import com.azure.storage.blob.specialized.BlobClientBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
public class AudioStreamingService {

    @Autowired
    private BlobContainerClient blobContainerClient;

    public ResponseEntity<byte[]> streamAudio(String fileName) {
        try {
            BlobClient blobClient = blobContainerClient.getBlobClient(fileName);

            if (!blobClient.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("File not found".getBytes());
            }

            InputStream inputStream = blobClient.openInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            byte[] audioContent = outputStream.toByteArray();

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

    // ✅ Generate a SAS Token URL for a file
    public String generateReadSasUrl(String fileName) {
        BlobClient blobClient = blobContainerClient.getBlobClient(fileName);
        BlobClientBase blobClientBase = blobClient.getBlockBlobClient();

        // Define SAS permissions
        BlobSasPermission permission = new BlobSasPermission()
                .setReadPermission(true);

        // Set expiry time (1 hour)
        OffsetDateTime expiryTime = OffsetDateTime.now(ZoneOffset.UTC).plusHours(1);

        // Create SAS values
        BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(expiryTime, permission)
                .setStartTime(OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(5))
                .setContentDisposition("inline");

        // Generate SAS token and return full URL
        String sasToken = blobClientBase.generateSas(values);
        return blobClient.getBlobUrl() + "?" + sasToken;
    }

    // Optional helper to get URL without SAS
    public String getBlobUrl(String fileName) {
        return blobContainerClient.getBlobClient(fileName).getBlobUrl();
    }
}
