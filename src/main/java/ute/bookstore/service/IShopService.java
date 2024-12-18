package ute.bookstore.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import ute.bookstore.entity.Author;
import ute.bookstore.entity.Shop;

public interface IShopService {
	Shop getShopById(Long id);
	Shop getShopByUserEmail(String email);
    Shop updateShop(Shop shop, MultipartFile logoFile, String userId);
    Shop createShop(Shop shop, Long userId);
    boolean isShopOwner(String userId, Long shopId);
    Shop save(Shop shop);
    
    Double calculateShopRating(Shop shop);
    void updateShopRating(Shop shop);
	List<Shop> findAll();
}
