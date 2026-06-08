package in.banking.aap.service;

 

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

/**
 * Service for file upload to AWS S3.
 * Handles image uploads for messages.
 * Generates unique file names and returns public URLs.
 */
@Slf4j
@Service
public class FileUploadService {
    
    @Value("${aws.s3.access-key}")
    private String accessKey;
    
    @Value("${aws.s3.secret-key}")
    private String secretKey;
    
    @Value("${aws.s3.region}")
    private String region;
    
    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    
    private AmazonS3 s3Client;
    
    @PostConstruct
    public void initializeAmazon() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        this.s3Client = AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
        
        log.info("AWS S3 client initialized for bucket: {}", bucketName);
    }
    
    /**
     * Upload file to S3 and return public URL
     */
    public String uploadFile(MultipartFile multipartFile) {
        try {
            File file = convertMultiPartToFile(multipartFile);
            String fileName = generateFileName(multipartFile);
            
            uploadFileToS3(fileName, file);
            
            file.delete(); // Clean up local file
            
            String fileUrl = getFileUrl(fileName);
            log.info("File uploaded successfully: {}", fileUrl);
            
            return fileUrl;
            
        } catch (Exception e) {
            log.error("Error uploading file to S3", e);
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }
    
    /**
     * Delete file from S3
     */
    public void deleteFile(String fileUrl) {
        try {
            String fileName = extractFileNameFromUrl(fileUrl);
            s3Client.deleteObject(bucketName, fileName);
            
            log.info("File deleted successfully: {}", fileName);
            
        } catch (Exception e) {
            log.error("Error deleting file from S3", e);
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }
    
    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }
    
    private String generateFileName(MultipartFile multiPart) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String originalFileName = multiPart.getOriginalFilename();
        String extension = originalFileName != null && originalFileName.contains(".") 
                ? originalFileName.substring(originalFileName.lastIndexOf("."))
                : "";
        
        return "chat-images/" + timestamp + "_" + uuid + extension;
    }
    
    private void uploadFileToS3(String fileName, File file) {
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }
    
    private String getFileUrl(String fileName) {
        return s3Client.getUrl(bucketName, fileName).toString();
    }
    
    private String extractFileNameFromUrl(String fileUrl) {
        // Extract the S3 object key from the URL.
        // URL format: https://<bucket>.s3.<region>.amazonaws.com/<key>
        // We strip everything up to and including the bucket host segment.
        try {
            java.net.URL url = new java.net.URL(fileUrl);
            String path = url.getPath();
            // path starts with '/', remove leading slash to get the S3 key
            return path.startsWith("/") ? path.substring(1) : path;
        } catch (java.net.MalformedURLException e) {
            log.warn("Could not parse file URL '{}', falling back to last-segment extraction", fileUrl);
            return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        }
    }
}
