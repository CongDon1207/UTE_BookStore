package ute.bookstore.controller.seller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SellerHomeController {
	
	@GetMapping("seller")
	public String Home() {
		return "seller/home";
	}
}
