package com.example.streamingservice.Controller;


import com.example.streamingservice.Service.AudioStorageService;
import com.example.streamingservice.Service.AudioStreamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/audio")
public class AudioController {

    @Autowired
    private AudioStorageService audioStorageService;

    @Autowired
    private AudioStreamingService audioStreamingService;

    // Upload audio file
    @PostMapping("/upload")
    public ResponseEntity<String> uploadAudio(@RequestParam("file") MultipartFile file) {
        String fileUrl = audioStorageService.uploadFile(file);
        return ResponseEntity.ok("File uploaded successfully: " + fileUrl);
    }

    // Stream audio file
    @GetMapping("/stream/{fileName}")
    public ResponseEntity<byte[]> streamAudio(@PathVariable String fileName) {
        return audioStreamingService.streamAudio(fileName);
    }

    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello, World!");
    }
}
