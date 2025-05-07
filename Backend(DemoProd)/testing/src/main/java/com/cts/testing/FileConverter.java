package com.cts.testing;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileConverter {

 public static MultipartFile convertStringToMultipartFile(String imagePath) throws IOException {
     Path path = Paths.get(imagePath);
     String name = path.getFileName().toString();
     String originalFileName = path.getFileName().toString();
     String contentType = Files.probeContentType(path); // Detect content type

     byte[] content = Files.readAllBytes(path);

     return new MockMultipartFile(name, originalFileName, contentType, content);
 }
}
