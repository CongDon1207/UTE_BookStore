package ute.bookstore.controller.admin;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;  // Sửa import Model
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import ute.bookstore.entity.User;
import ute.bookstore.enums.UserRole;
import ute.bookstore.service.impl.UserServiceImpl;

@Controller
@RequestMapping("/admin/profile")
@RequiredArgsConstructor
public class AdminProfileController {

    private final UserServiceImpl userService;

	/*
	 * @GetMapping public String showProfile(Model model, Principal principal) {
	 * User user = userService.findByEmail(principal.getName()); // Sửa từ
	 * getUserByEmail sang findByEmail model.addAttribute("user", user); return
	 * "admin/profile"; }
	 */
    
    @GetMapping
    public String showProfile(Model model, Principal principal) {
        // Tạm hardcode user admin để test
        User adminUser = new User();
        adminUser.setEmail("admin@gmail.com");
        adminUser.setFullName("Administrator");
        adminUser.setRole(UserRole.ADMIN);
        adminUser.setPhone("0123456789");
        adminUser.setIsActive(true);
        
        model.addAttribute("user", adminUser);
        return "admin/profile";
    }
    
    @PostMapping("/update")
    public String updateProfile(
            @RequestParam String fullName,
            @RequestParam String phone,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            userService.updateProfile(principal.getName(), fullName, phone);
            redirectAttributes.addAttribute("message",
                    URLEncoder.encode("Cập nhật thông tin thành công", "UTF-8"));
        } catch (Exception e) {
            try {
                redirectAttributes.addAttribute("error", 
                    URLEncoder.encode("Có lỗi xảy ra: " + e.getMessage(), "UTF-8")); 
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }
        return "redirect:/admin/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            userService.changePassword(principal.getName(), currentPassword, newPassword);
            redirectAttributes.addAttribute("message",
                    URLEncoder.encode("Đổi mật khẩu thành công", "UTF-8"));
        } catch (Exception e) {
            try {
                redirectAttributes.addAttribute("error",
                    URLEncoder.encode("Có lỗi xảy ra: " + e.getMessage(), "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }
        return "redirect:/admin/profile";
    }
}