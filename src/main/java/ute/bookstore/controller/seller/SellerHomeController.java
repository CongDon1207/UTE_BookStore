package ute.bookstore.controller.seller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

import ute.bookstore.entity.Order;
import ute.bookstore.entity.Shop;
import ute.bookstore.enums.OrderStatus;
import ute.bookstore.service.ICloudinaryService;
import ute.bookstore.service.ISellerHomeService;
import ute.bookstore.service.IShopService;
import ute.bookstore.service.impl.SellerHomeServiceImpl;
import ute.bookstore.service.impl.ShopServiceImpl;

@Controller
@RequestMapping("/seller")
public class SellerHomeController {

    @Autowired
    private ISellerHomeService sellerHomeService;
    @Autowired private IShopService shopService;
    private static final String DEFAULT_EMAIL = "vendor@gmail.com";
    
    @ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {
		model.addAttribute("requestURI", request.getRequestURI());
	}
    
    @GetMapping({"","/home"})
    public String home(Model model, HttpServletRequest request) {
        Shop shop = shopService.getShopByUserEmail(DEFAULT_EMAIL);
        
        model.addAttribute("totalOrders", sellerHomeService.getTotalOrders(shop));
        model.addAttribute("monthlyRevenue", sellerHomeService.getMonthlyRevenue(shop));
        model.addAttribute("totalProducts", sellerHomeService.getTotalProducts(shop));
        model.addAttribute("averageRating", sellerHomeService.getAverageRating(shop));
        model.addAttribute("recentOrders", sellerHomeService.getRecentOrders(shop));
        model.addAttribute("revenueData", sellerHomeService.getRevenueData(shop));
        model.addAttribute("requestURI", request.getRequestURI());
        model.addAttribute("topSellingBooks", sellerHomeService.getTopSellingBooks(shop));
        model.addAttribute("booksByCategory", sellerHomeService.getBooksByCategory(shop));
        
        return "seller/dashboard";
    }
    
    public static void main(String args[]) {
    	try {
    		ISellerHomeService sellerHomeService = new SellerHomeServiceImpl();
    		IShopService shopService = new ShopServiceImpl();
    		Shop shop = shopService.getShopByUserEmail(DEFAULT_EMAIL);
    		
    		System.out.println("Revenue Data: " + sellerHomeService.getTopSellingBooks(shop)); // Thêm log để kiểm tra
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
}