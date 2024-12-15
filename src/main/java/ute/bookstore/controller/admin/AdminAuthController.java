package ute.bookstore.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminAuthController {
    
	@PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        // Xóa toàn bộ thông tin trong session
        session.invalidate();
        
        // Thêm thông báo thành công (optional)
        redirectAttributes.addFlashAttribute("message", "Đăng xuất thành công");
        
        // Redirect về trang login
        return "redirect:/login";
    }
}