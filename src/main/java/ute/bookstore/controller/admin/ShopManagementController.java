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
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;
import ute.bookstore.entity.Shop;
import ute.bookstore.service.admin.AdminShopService;
import ute.bookstore.service.admin.impl.IAdminShopService;

@Controller
@RequestMapping("/admin/shop-management")
@RequiredArgsConstructor
public class ShopManagementController {
	private final IAdminShopService adminShopService;

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

	@GetMapping("/{id}/detail")
	public ModelAndView getShopDetail(@PathVariable Long id) {
		Shop shop = adminShopService.getShopDetail(id);
		ModelAndView mav = new ModelAndView("admin/shop/modals/detail-modal :: modalContent");
		mav.addObject("shop", shop);
		return mav;
	}

	@GetMapping("/pending")
	public String getPendingShops(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Page<Shop> pendingShops = adminShopService.getPendingShops(page, size);
		model.addAttribute("shops", pendingShops);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", pendingShops.getTotalPages());
		return "admin/shop/pending";
	}

	@PostMapping("/toggle-status/{id}")
	@ResponseBody
	public ResponseEntity<?> toggleShopStatus(@PathVariable Long id) {
		boolean newStatus = adminShopService.toggleShopStatus(id);
		return ResponseEntity.ok(Map.of("status", newStatus));
	}

	@PostMapping("/{id}/approve")
	@ResponseBody
	public ResponseEntity<Shop> approveShop(@PathVariable Long id) {
		return ResponseEntity.ok(adminShopService.approveShop(id));
	}

	@PostMapping("/{id}/reject")
	@ResponseBody
	public ResponseEntity<Shop> rejectShop(@PathVariable Long id, @RequestParam String reason) {
		return ResponseEntity.ok(adminShopService.rejectShop(id, reason));
	}

	@PostMapping("/{id}/commission")
	@ResponseBody
	public ResponseEntity<Shop> updateCommission(@PathVariable Long id, @RequestParam Double rate) {
		return ResponseEntity.ok(adminShopService.updateCommissionRate(id, rate));
	}
}