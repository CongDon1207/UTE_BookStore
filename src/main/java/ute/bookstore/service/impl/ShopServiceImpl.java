// ShopServiceImpl.java
package ute.bookstore.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import ute.bookstore.entity.Shop;
import ute.bookstore.entity.User; 
import ute.bookstore.exception.ResourceNotFoundException;

import ute.bookstore.repository.ShopRepository;
import ute.bookstore.repository.UserRepository;
import ute.bookstore.service.ICloudinaryService;
import ute.bookstore.service.IShopService;



@Service
@Transactional
public class ShopServiceImpl implements IShopService {
	@Autowired
    private ShopRepository shopRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ICloudinaryService cloudinaryService;

    @Override
    public Shop getShopByUserId(String userId) {
        return shopRepository.findByUserEmail(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
    }
    
    @Override
    public Shop getShopById(Long id) {
        return shopRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
    }

    @Override
    public Shop updateShop(Shop shopUpdate, MultipartFile logoFile, String userId) {
        Shop existingShop = getShopByUserId(userId);
        
        if (!existingShop.getUser().getEmail().equals(userId)) {
            throw new EntityNotFoundException("Shop not found for user");
        }

        // Update basic info
        existingShop.setName(shopUpdate.getName());
        existingShop.setDescription(shopUpdate.getDescription());
        existingShop.setPhone(shopUpdate.getPhone());
        
        // Update address if provided
        if (shopUpdate.getAddress() != null) {
            existingShop.getAddress().setStreet(shopUpdate.getAddress().getStreet());
            existingShop.getAddress().setDistrict(shopUpdate.getAddress().getDistrict());
            existingShop.getAddress().setCity(shopUpdate.getAddress().getCity());
        }

        // Upload new logo if provided
        if (logoFile != null && !logoFile.isEmpty()) {
        	   try {
        	       String logoUrl = cloudinaryService.uploadImage(logoFile);
        	       // Delete old logo if exists
        	       if (existingShop.getLogo() != null) {
        	           cloudinaryService.deleteImage(existingShop.getLogo());
        	       }
        	       existingShop.setLogo(logoUrl);
        	   } catch (IOException e) {
        	       throw new RuntimeException("Failed to upload shop logo", e);
        	   }
        	}

        return shopRepository.save(existingShop);
    }

    @Override
    public Shop createShop(Shop shop, Long userId) {
    	User user = userRepository.findById(userId)
    	        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
        shop.setUser(user);
        shop.setIsActive(true);
        shop.setRating(0.0);
        
        return shopRepository.save(shop);
    }

    @Override
    public boolean isShopOwner(String userId, Long shopId) {
        return shopRepository.existsByUserEmailAndId(userId, shopId);
    }
	

    
}
