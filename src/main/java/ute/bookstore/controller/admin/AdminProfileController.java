package ute.bookstore.controller.admin;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Sửa import Model
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import ute.bookstore.entity.User;
import ute.bookstore.enums.UserRole;
import ute.bookstore.service.impl.UserServiceImpl;

@Controller
@RequestMapping("/admin/profile")
@RequiredArgsConstructor
public class AdminProfileController {
    private final UserServiceImpl userService;
    
    @GetMapping
    public String showProfile(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login"; // Redirect về trang login nếu chưa đăng nhập
        }
        
        User user = userService.findByEmail(currentUser.getEmail());
        model.addAttribute("user", user);
        return "admin/profile";
    }

    @PostMapping("/update")
    public String updateProfile(
            @RequestParam String fullName,
            @RequestParam String phone,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser == null) {
                return "redirect:/login";
            }
            
            userService.updateProfile(currentUser.getEmail(), fullName, phone);
            
            // Cập nhật lại thông tin user trong session
            currentUser.setFullName(fullName);
            currentUser.setPhone(phone);
            session.setAttribute("currentUser", currentUser);
            
            redirectAttributes.addAttribute("message", URLEncoder.encode("Cập nhật thông tin thành công", "UTF-8"));
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
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser == null) {
                return "redirect:/login";
            }
            
            userService.changePassword(currentUser.getEmail(), currentPassword, newPassword);
            redirectAttributes.addAttribute("message", URLEncoder.encode("Đổi mật khẩu thành công", "UTF-8"));
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