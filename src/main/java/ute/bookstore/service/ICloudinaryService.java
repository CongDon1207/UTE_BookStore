// ICloudinaryService.java
package ute.bookstore.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface ICloudinaryService {
   String uploadImage(MultipartFile file) throws IOException;
   void deleteImage(String imageUrl);
}
