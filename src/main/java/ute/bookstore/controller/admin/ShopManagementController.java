package ute.bookstore.controller.admin;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import ute.bookstore.entity.Shop;
import ute.bookstore.service.admin.AdminShopService;

@Controller
@RequestMapping("/admin/shop-management")
public class ShopManagementController {

	@Autowired
	private AdminShopService adminShopService;

	@GetMapping
	public String getShopManagementPage(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search) {
		Page<Shop> shops = adminShopService.getAllShopsForAdmin(page, size, search);
		model.addAttribute("shops", shops);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", shops.getTotalPages());
		model.addAttribute("search", search);
		return "admin/shop/index";
	}

	@PostMapping("/toggle-status/{id}")
	@ResponseBody
	public ResponseEntity<?> toggleShopStatus(@PathVariable Long id) {
		boolean newStatus = adminShopService.toggleShopStatus(id);
		return ResponseEntity.ok(Map.of("status", newStatus));
	}
}
