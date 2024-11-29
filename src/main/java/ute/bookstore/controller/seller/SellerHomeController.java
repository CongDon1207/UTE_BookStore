package ute.bookstore.controller.seller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import ute.bookstore.entity.Shop;


@Controller
public class SellerHomeController {
    
    @GetMapping("seller")
    public String Home(Model model) {
        // Thêm shop vào model
        model.addAttribute("shop", new Shop()); // Hoặc lấy từ service
        return "seller/home";
    }
}