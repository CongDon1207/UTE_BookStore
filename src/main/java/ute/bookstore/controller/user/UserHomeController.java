package ute.bookstore.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserHomeController {
	
	@GetMapping("/user")
	public String home() {
		return "user/home";
	}
	@GetMapping("/cart")
	public String getCartPage() {
		return "user/cart/show";
	}
	
}
