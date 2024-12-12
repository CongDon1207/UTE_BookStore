package ute.bookstore.service.admin.impl;

import org.springframework.data.domain.Page;

import ute.bookstore.entity.Shop;

public interface IAdminShopService {
	Shop createShop(Shop shop);
	public void updateShop(Long id, Shop shopDetails);
    public Shop getShopById(Long id);
    Page<Shop> getAllShopsForAdmin(int page, int size, String search);
    boolean toggleShopStatus(Long id);
    void deleteShop(Long id);
}
