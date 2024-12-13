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

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/promotions")
public class PromotionManagementController {
    
    @Autowired
    private AdminPromotionService promotionService;
    
    @GetMapping("")
    public String index(Model model, 
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
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
    public String create(@ModelAttribute Promotion promotion, 
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                return "admin/promotion/create";
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
            model.addAttribute("promotion", promotion);
            model.addAttribute("discountTypes", DiscountType.values());
            return "admin/promotion/edit";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Không tìm thấy khuyến mãi: " + e.getMessage());
            return "redirect:/admin/promotions";
        }
    }
    
    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, 
            @ModelAttribute Promotion promotion,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                return "admin/promotion/edit";
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
    
    @PostMapping("/{id}/toggle-status")
    @ResponseBody
    public ResponseEntity<?> toggleStatus(@PathVariable Long id) {
        try {
            boolean newStatus = promotionService.togglePromotionStatus(id);
            Map<String, Object> response = new HashMap<>();
            response.put("status", newStatus);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}