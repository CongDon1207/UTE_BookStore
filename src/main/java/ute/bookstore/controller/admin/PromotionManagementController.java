package ute.bookstore.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ute.bookstore.entity.Promotion;
import ute.bookstore.service.admin.AdminPromotionService;
import ute.bookstore.enums.DiscountType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/promotions")
public class PromotionManagementController {

	@Autowired
	private AdminPromotionService promotionService;

	@GetMapping("")
	public String index(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search) {
		try {
			Page<Promotion> promotionPage;
			if (search != null && !search.trim().isEmpty()) {
				promotionPage = promotionService.searchPromotions(search.trim(), PageRequest.of(page, size));
			} else {
				promotionPage = promotionService.getAllPromotions(PageRequest.of(page, size));
			}

			model.addAttribute("promotions", promotionPage.getContent());
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", promotionPage.getTotalPages());
			model.addAttribute("search", search);

			return "admin/promotion/index";
		} catch (Exception e) {
			model.addAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
			return "admin/error";
		}
	}

	@GetMapping("/create")
	public String showCreateForm(Model model) {
		model.addAttribute("promotion", new Promotion());
		model.addAttribute("discountTypes", DiscountType.values());
		return "admin/promotion/create";
	}

	@PostMapping("/create")
	public String create(@ModelAttribute Promotion promotion, BindingResult result,
			RedirectAttributes redirectAttributes) {
		try {
			// Validate input
			if (result.hasErrors()) {
				return "admin/promotion/create";
			}

			// Validate promotion code format
			if (!promotion.getCode().matches("[A-Za-z0-9]+")) {
				redirectAttributes.addFlashAttribute("errorMessage", "Mã khuyến mãi chỉ được chứa chữ cái và số!");
				return "redirect:/admin/promotions/create";
			}

			// Validate discount value
			if (promotion.getDiscountType().equals(DiscountType.PERCENT)
					&& (promotion.getDiscount() <= 0 || promotion.getDiscount() > 100)) {
				redirectAttributes.addFlashAttribute("errorMessage", "Phần trăm giảm giá phải từ 0-100!");
				return "redirect:/admin/promotions/create";
			}

			// Validate dates
			if (promotion.getStartDate().isAfter(promotion.getEndDate())) {
				redirectAttributes.addFlashAttribute("errorMessage", "Thời gian kết thúc phải sau thời gian bắt đầu!");
				return "redirect:/admin/promotions/create";
			}

			// Check if promotion has expired
			if (promotion.getEndDate().isBefore(LocalDateTime.now())) {
				promotion.setIsActive(false); // Auto deactivate expired promotions
			}

			promotionService.createPromotion(promotion);
			redirectAttributes.addFlashAttribute("successMessage", "Tạo khuyến mãi thành công!");
			return "redirect:/admin/promotions";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/admin/promotions/create";
		}
	}

	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable Long id, Model model) {
		try {
			Promotion promotion = promotionService.getPromotionById(id);

			// Check if promotion has expired
			if (promotion.getEndDate().isBefore(LocalDateTime.now())) {
				promotion.setIsActive(false);
				promotionService.updatePromotion(promotion);
			}

			model.addAttribute("promotion", promotion);
			model.addAttribute("discountTypes", DiscountType.values());
			return "admin/promotion/edit";
		} catch (Exception e) {
			model.addAttribute("errorMessage", "Không tìm thấy khuyến mãi: " + e.getMessage());
			return "redirect:/admin/promotions";
		}
	}

	@PostMapping("/edit/{id}")
	public String update(@PathVariable Long id, @ModelAttribute Promotion promotion, BindingResult result,
			RedirectAttributes redirectAttributes) {
		try {
			if (result.hasErrors()) {
				return "admin/promotion/edit";
			}

			// Validate promotion code format
			if (!promotion.getCode().matches("[A-Za-z0-9]+")) {
				redirectAttributes.addFlashAttribute("errorMessage", "Mã khuyến mãi chỉ được chứa chữ cái và số!");
				return "redirect:/admin/promotions/edit/" + id;
			}

			// Validate discount value
			if (promotion.getDiscountType().equals(DiscountType.PERCENT)
					&& (promotion.getDiscount() <= 0 || promotion.getDiscount() > 100)) {
				redirectAttributes.addFlashAttribute("errorMessage", "Phần trăm giảm giá phải từ 0-100!");
				return "redirect:/admin/promotions/edit/" + id;
			}

			// Validate dates
			if (promotion.getStartDate().isAfter(promotion.getEndDate())) {
				redirectAttributes.addFlashAttribute("errorMessage", "Thời gian kết thúc phải sau thời gian bắt đầu!");
				return "redirect:/admin/promotions/edit/" + id;
			}

			// Check if promotion has expired
			if (promotion.getEndDate().isBefore(LocalDateTime.now())) {
				promotion.setIsActive(false); // Auto deactivate expired promotions
			}

			promotion.setId(id);
			promotionService.updatePromotion(promotion);
			redirectAttributes.addFlashAttribute("successMessage", "Cập nhật khuyến mãi thành công!");
			return "redirect:/admin/promotions";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/admin/promotions/edit/" + id;
		}
	}

	@PostMapping("/{id}/toggle-status")
	@ResponseBody
	public ResponseEntity<?> toggleStatus(@PathVariable Long id) {
		try {
			Promotion promotion = promotionService.getPromotionById(id);

			// Check if promotion has expired
			if (promotion.getEndDate().isBefore(LocalDateTime.now())) {
				return ResponseEntity.badRequest().body("Không thể kích hoạt khuyến mãi đã hết hạn!");
			}

			boolean newStatus = promotionService.togglePromotionStatus(id);
			Map<String, Object> response = new HashMap<>();
			response.put("status", newStatus);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/delete/{id}")
	@ResponseBody
	public ResponseEntity<?> delete(@PathVariable Long id) {
		try {
			promotionService.deletePromotion(id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}