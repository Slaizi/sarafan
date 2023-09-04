package ru.Bogachev.sarafan.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.Bogachev.sarafan.utilsPath.MyPath;


import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final MyPath myPath;

    @GetMapping("/img/{filename}")
    public ResponseEntity<Resource> serveFile (@PathVariable String filename) {
        try {
            Path filePath = Paths.get(myPath.myPathForFile(), filename);
            Resource file = new UrlResource(filePath.toUri());

            if (file.exists() && file.isReadable()) {
                return ResponseEntity.ok()
                                     .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                                     .body(file);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to load image: " + e.getMessage());
        }
    }
}
