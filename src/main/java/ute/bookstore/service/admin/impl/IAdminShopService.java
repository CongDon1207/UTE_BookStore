package ute.bookstore.service.admin.impl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;

import ute.bookstore.dto.admin.ShopRevenueStats;
import ute.bookstore.dto.admin.ShopRevenueStats.RevenueByTime;
import ute.bookstore.entity.Shop;

public interface IAdminShopService {
	Shop createShop(Shop shop);
	public void updateShop(Long id, Shop shopDetails);
    public Shop getShopById(Long id);
    Page<Shop> getAllShopsForAdmin(int page, int size, String search);
    boolean toggleShopStatus(Long id);
    void deleteShop(Long id);
	Shop getShopDetail(Long shopId);
	Shop updateCommissionRate(Long shopId, Double rate);
	Shop rejectShop(Long shopId, String reason);
	Shop approveShop(Long shopId);
	Page<Shop> getPendingShops(int page, int size);
	Page<Shop> getRejectedShops(int page, int size);
	ShopRevenueStats getShopStats(Long shopId);
	RevenueByTime getRevenueStats(Shop shop, LocalDateTime from, LocalDateTime to);
}
