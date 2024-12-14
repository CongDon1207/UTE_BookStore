package ute.bookstore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ute.bookstore.dto.RegisterRequest;
import ute.bookstore.service.AuthService;
import ute.bookstore.service.EmailService;
import ute.bookstore.service.OtpService;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;
    private final OtpService otpService;

    // Hiển thị trang đăng ký
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register"; // Trả về trang đăng ký Thymeleaf
    }

    // Endpoint đăng ký tài khoản
    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequest registerRequest, Model model) {
        // Kiểm tra email đã tồn tại
        if (authService.isEmailExist(registerRequest.getEmail())) {
            model.addAttribute("error", "Email đã tồn tại.");
            return "register"; // Trả về trang đăng ký với thông báo lỗi
        }

        // Tạo và gửi OTP qua email
        String otp = emailService.generateOtp();
        otpService.storeOtp(registerRequest.getEmail(), otp);
        emailService.sendOtpToEmail(registerRequest.getEmail(), otp);

        model.addAttribute("message", "Tài khoản đã được tạo, OTP đã được gửi vào email.");
        return "verifyOtp"; // Chuyển sang trang nhập OTP
    }

    // Endpoint xác thực OTP
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email, @RequestParam String otp, Model model) {
        if (otpService.verifyOtp(email, otp)) {
            // Xóa OTP sau khi xác thực thành công
            otpService.clearOtp(email);
            model.addAttribute("message", "OTP xác thực thành công.");
            return "login"; // Chuyển đến trang đăng nhập sau khi xác thực thành công
        } else {
            model.addAttribute("error", "OTP không đúng.");
            return "verifyOtp"; // Trả về trang nhập OTP với thông báo lỗi
        }
    }
}
