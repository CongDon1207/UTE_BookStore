package ute.bookstore.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

import ute.bookstore.entity.Order;
import ute.bookstore.entity.Shop;
import ute.bookstore.entity.User;
import ute.bookstore.enums.OrderStatus;
import ute.bookstore.repository.OrderRepository;
import ute.bookstore.repository.ShopRepository;
import ute.bookstore.repository.UserRepository;
import ute.bookstore.service.ISellerHomeService;

@Service
public class SellerHomeServiceImpl implements ISellerHomeService {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Long getTotalOrders(Shop shop) {
        return orderRepository.countByShop(shop);
    }

    @Override
    public Double getMonthlyRevenue(Shop shop) {
        LocalDateTime startDate = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endDate = startDate.plusMonths(1);
        return orderRepository.sumTotalAmountByShopAndCreatedAtBetween(shop, startDate, endDate);
    }

    @Override
    public Long getTotalProducts(Shop shop) {
        return (long) shop.getBooks().size(); // Ép kiểu int sang long
        // Hoặc sử dụng Long.valueOf()
        // return Long.valueOf(shop.getBooks().size());
    }

    @Override
    public Double getAverageRating(Shop shop) {
        // Implement logic to calculate average rating based on reviews 
        // (assuming you have a Review entity and relationships)
        // ...
        return 5.0; // Placeholder
    }

    @Override
    public List<Order> getRecentOrders(Shop shop) {
        return orderRepository.findTop5ByShopOrderByCreatedAtDesc(shop);
    }

    @Override
    public List<Object[]> getRevenueData(Shop shop) {
        return orderRepository.findMonthlyRevenueByShop(shop);
    }
}