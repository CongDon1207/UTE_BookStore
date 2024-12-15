// ShopServiceImpl.java
package ute.bookstore.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.util.List;

import ute.bookstore.entity.Shop;
import ute.bookstore.entity.User; 
import ute.bookstore.entity.Address;
import ute.bookstore.entity.Author;
import ute.bookstore.entity.Book;
import ute.bookstore.entity.Review;
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
    public Shop getShopByUserEmail(String email) {
    	 System.out.println("shopRepository: " + shopRepository);
        return shopRepository.findByUserEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
    }
    
    @Override
    public Shop getShopById(Long id) {
        return shopRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
    }

 // ShopServiceImpl.java 
 // ShopServiceImpl.java
    @Override
    public Shop updateShop(Shop shopUpdate, MultipartFile logoFile, String email) {
       try {
           Shop existingShop = getShopByUserEmail(email);
           System.out.println("Shop info - ID: " + shopUpdate.getId() + ", Name: " + shopUpdate.getName());

           if (logoFile != null && !logoFile.isEmpty()) {
               if (existingShop.getLogo() != null) {
                   cloudinaryService.deleteImage(existingShop.getLogo()); 
               }
               String logoUrl = cloudinaryService.uploadImage(logoFile);
               existingShop.setLogo(logoUrl);
           }

           existingShop.setName(shopUpdate.getName());
           existingShop.setDescription(shopUpdate.getDescription());
           existingShop.setPhone(shopUpdate.getPhone());

           Address address = shopUpdate.getAddress();
           address.setShop(existingShop); 
           existingShop.setAddress(address);

           return shopRepository.save(existingShop);
       } catch (Exception e) {
           throw new RuntimeException("Failed to update shop", e);
       }
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

    @Override
    public Shop save(Shop shop) {
        if (shop == null) {
            throw new IllegalArgumentException("Shop cannot be null");
        }
        try {
            return shopRepository.save(shop);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save shop: " + e.getMessage());
        }
    }
	
    public Double calculateShopRating(Shop shop) {
        if (shop.getBooks() == null || shop.getBooks().isEmpty()) {
            return 0.0;
        }
        
        double totalRating = 0;
        int totalReviews = 0;
        
        for (Book book : shop.getBooks()) {
            List<Review> reviews = book.getReviews();
            if (reviews != null && !reviews.isEmpty()) {
                for (Review review : reviews) {
                    if (review.getRating() != null) {
                        totalRating += review.getRating();
                        totalReviews++;
                    }
                }
            }
        }
        
        return totalReviews > 0 ? totalRating / totalReviews : 0.0;
    }
    
    @Override
    public void updateShopRating(Shop shop) {
        shop.setRating(calculateShopRating(shop));
        shopRepository.save(shop);
    }

	@Override
	public List<Shop> findAll() {
		// TODO Auto-generated method stub
		return shopRepository.findAll();
	}
    
    
}
