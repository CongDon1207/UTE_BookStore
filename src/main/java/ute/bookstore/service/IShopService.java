package ute.bookstore.service;

import ute.bookstore.entity.Shop;

public interface IShopService {
	Shop getShopById(Long id);
    Shop getShopByUser(String username);
}
