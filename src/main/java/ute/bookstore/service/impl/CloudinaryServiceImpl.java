package ute.bookstore.service.impl;

import com.cloudinary.Cloudinary;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ute.bookstore.service.ICloudinaryService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements ICloudinaryService {
   private Cloudinary cloudinary;
   
   @PostConstruct
   public void init() {
       Map<String, String> config = new HashMap<>();
       config.put("cloud_name", "BookStore");
       config.put("api_key", "448327469724436");
       config.put("api_secret", "2ZcSFxMGs5ZlLJ0Z94QKoFmxXiM");
       cloudinary = new Cloudinary(config);
   }

   @Override
   public String uploadImage(MultipartFile file) throws IOException {
       Map<String, Object> params = new HashMap<>();
       params.put("folder", "utebookstore");
       Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
       return (String) uploadResult.get("secure_url");
   }

   @Override 
   public void deleteImage(String imageUrl) {
       try {
           String publicId = extractPublicIdFromUrl(imageUrl);
           cloudinary.uploader().destroy(publicId, null);
       } catch (IOException e) {
           throw new RuntimeException("Failed to delete image from Cloudinary", e);
       }
   }
   
   private String extractPublicIdFromUrl(String imageUrl) {
       String[] urlParts = imageUrl.split("/");
       String fileName = urlParts[urlParts.length - 1];
       return "utebookstore/" + fileName.substring(0, fileName.lastIndexOf("."));
   }
}