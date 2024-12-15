package ute.bookstore.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ute.bookstore.repository.UserRepository;
import ute.bookstore.repository.ShopRepository;
import ute.bookstore.repository.BookRepository;
import ute.bookstore.repository.PromotionRepository;
import ute.bookstore.service.AdminUserService;
import ute.bookstore.service.admin.AdminShopService;


@Controller
public class AdminHomeController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ShopRepository shopRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private PromotionRepository promotionRepository;
    
    @Autowired
    private AdminUserService userService;
    
    @Autowired
    private AdminShopService shopService;

    @GetMapping("/admin")
    public String home(Model model) {
        // Thống kê tổng quan
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalShops", shopRepository.count());
        model.addAttribute("totalBooks", bookRepository.count());
        model.addAttribute("totalPromotions", promotionRepository.count());
        
        // Lấy danh sách hoạt động gần đây
        model.addAttribute("recentUsers", userService.getRecentUsers(5));  // 5 user mới nhất
        model.addAttribute("recentShops", shopService.getRecentShops(5));  // 5 shop mới nhất
        
        return "admin/home";
    }
}