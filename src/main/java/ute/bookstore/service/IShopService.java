package ute.bookstore.service;

import org.springframework.web.multipart.MultipartFile;

import ute.bookstore.entity.Shop;

public interface IShopService {
	Shop getShopById(Long id);
	Shop getShopByUserId(String userId);
    Shop updateShop(Shop shop, MultipartFile logoFile, String userId);
    Shop createShop(Shop shop, Long userId);
    boolean isShopOwner(String userId, Long shopId);
}
