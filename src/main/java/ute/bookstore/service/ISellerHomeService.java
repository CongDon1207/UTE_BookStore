package ute.bookstore.service;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import ute.bookstore.entity.Order;
import ute.bookstore.entity.Shop;
public interface ISellerHomeService {

    Long getTotalOrders(Shop shop);

    Double getMonthlyRevenue(Shop shop);

    Long getTotalProducts(Shop shop);

    Double getAverageRating(Shop shop);

    List<Order> getRecentOrders(Shop shop);

    List<Object[]> getRevenueData(Shop shop); 
}
