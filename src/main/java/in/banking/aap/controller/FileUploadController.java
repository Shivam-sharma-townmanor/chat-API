package in.banking.aap.controller;

import in.banking.aap.service.FileUploadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for file uploads.
 * 
 * Endpoints:
 * POST /api/upload/image - Upload image file
 * DELETE /api/upload/image - Delete image file
 */
@Slf4j
@CrossOrigin
@Tag(name = "File Upload", description = "Image Upload APIs")
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {
    
    private final FileUploadService fileUploadService;
    
    @PostMapping("/image")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file) {
        
        log.info("Uploading image file: {}", file.getOriginalFilename());
        
        String fileUrl = fileUploadService.uploadFile(file);
        
        Map<String, String> response = new HashMap<>();
        response.put("url", fileUrl);
        response.put("fileName", file.getOriginalFilename());
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/image")
    public ResponseEntity<Void> deleteImage(
            @RequestParam String fileUrl) {
        
        log.info("Deleting image: {}", fileUrl);
        
        fileUploadService.deleteFile(fileUrl);
        return ResponseEntity.noContent().build();
    }
}
