package ute.bookstore.controller.seller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import ute.bookstore.entity.Address;
import ute.bookstore.entity.Book;
import ute.bookstore.entity.Category;
import ute.bookstore.entity.Order;
import ute.bookstore.entity.Shop;
import ute.bookstore.enums.OrderStatus;
import ute.bookstore.entity.User;


@Controller
@RequestMapping("/seller")
public class SellerHomeController {
    
	@GetMapping({"","/home"})
	public String home() {
		return "seller/dashboard";
	}
	
	
	
	
	@GetMapping("/shop")
	public String shopManagement(Model model) {
	    Shop shop = new Shop();
	    shop.setName("Book Shop UTE");
	    shop.setPhone("0123456789"); 
	    shop.setDescription("Cửa hàng sách UTE");
	    shop.setLogo("/images/logos/bookstore3.png");
	    shop.setRating(4.5);
	    shop.setIsActive(true);
	    shop.setBooks(new ArrayList<>()); // Thêm empty list
	    shop.setOrders(new ArrayList<>()); // Thêm empty list

	    Address address = new Address();
	    address.setStreet("Số 1 Võ Văn Ngân");
	    address.setDistrict("Thủ Đức");
	    address.setCity("TP.HCM");
	    shop.setAddress(address);

	    model.addAttribute("shop", shop);
	    return "seller/shop-management";
	}
	
	
	
	
}