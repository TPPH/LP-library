package com.example.streamingservice.Service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class AudioStreamingService {

    @Autowired
    private Storage storage;

    private static final String BUCKET_NAME = "your-bucket-name";

    public ResponseEntity<byte[]> streamAudio(String fileName) {
        try {
            // Retrieve the audio file from Google Cloud Storage
            Blob blob = storage.get(BUCKET_NAME, fileName);
            if (blob == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found".getBytes());
            }

            // Stream the content as byte array
            InputStream audioStream = blob.getContent();
            byte[] audioContent = audioStream.readAllBytes();

            // Prepare the response with the audio content
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
                    .body(audioContent);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error streaming the audio".getBytes());
        }
    }
}
