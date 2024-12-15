package ute.bookstore.controller;

import lombok.RequiredArgsConstructor;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpSession;

import ute.bookstore.dto.RegisterRequest;
import ute.bookstore.entity.User;
import ute.bookstore.enums.UserRole;
import ute.bookstore.service.AuthService;
import ute.bookstore.service.EmailService;
import ute.bookstore.service.IUserService;
import ute.bookstore.service.OtpService;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final EmailService emailService;
	private final OtpService otpService;

	@Autowired
    private IUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

  
    @GetMapping("/login")
    public String login() {
        return "/auth/login" ;
    }
	// Hiển thị trang đăng ký
	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("registerRequest", new RegisterRequest());
		return "/auth/register"; // Trả về trang đăng ký Thymeleaf
	}
	@GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "/auth/forgot-password"; // Tên file HTML của bạn (không cần phần mở rộng .html)
    }
	// Endpoint đăng ký tài khoản
	@PostMapping("/register")
	public String register(@ModelAttribute RegisterRequest registerRequest, Model model) {
	    // Kiểm tra email đã tồn tại
	    if (authService.isEmailExist(registerRequest.getEmail())) {
	        model.addAttribute("error", "Email đã tồn tại.");
	        return "/auth/register";
	    }

	    // Tạo tài khoản mới với trạng thái chưa kích hoạt
	    User user = authService.createInactiveUser(registerRequest);

	    // Tạo và gửi OTP qua email
	    String otp = emailService.generateOtp();
	    authService.saveOtpForUser(user, otp);
	    emailService.sendOtpToEmail(user.getEmail(), otp);

	    model.addAttribute("message", "OTP đã được gửi vào email của bạn.");
	    model.addAttribute("email", user.getEmail());
	    return "/auth/verifyOtp"; // Chuyển đến trang nhập OTP
	}


	// Endpoint xác thực OTP
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam String email, @RequestParam String otp, Model model) {
	    if (authService.verifyOtp(email, otp)) {
	        model.addAttribute("message", "Tài khoản đã được kích hoạt thành công. Vui lòng đăng nhập.");
	        return "/auth/login"; // Chuyển đến trang đăng nhập
	    } else {
	        model.addAttribute("error", "OTP không đúng hoặc đã hết hạn.");
	        return "/auth/verifyOtp"; // Quay lại trang nhập OTP với thông báo lỗi
	    }
	}



    @PostMapping("/login")
    public String handleLogin(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {
        // Find the user by email
        User user = userService.findByEmail(email);
        
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("error", "Invalid email or password");
            return "/auth/login"; // Show login page with error
        }

        // Check if the user is active
        if (!user.getIsActive()) {
            model.addAttribute("error", "Account is inactive. Please contact support.");
            return "/auth/login";
        }

        // Store user information in session
        session.setAttribute("currentUser", user);

        // Redirect based on role
        if (user.getRole() == UserRole.ADMIN) {
            return "redirect:/admin";
        } else if (user.getRole() == UserRole.USER) {
            return "redirect:/user/home";
        } else if (user.getRole() == UserRole.VENDOR) {
            return "redirect:/seller/home";
        } else if (user.getRole() == UserRole.SHIPPER) {
            return "redirect:/shipper/home";
        } else {
            return "redirect:/home";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Clear the session
        return "redirect:/home"; // Redirect to login page
    }

	
	// Xử lý yêu cầu gửi OTP
    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam String email, Model model) {
        if (!authService.isEmailExist(email)) {
            model.addAttribute("error", "Email không tồn tại.");
            return "/auth/forgot-password"; // Quay lại trang quên mật khẩu với thông báo lỗi
        }

        // Gửi OTP qua email
        String otp = emailService.generateOtp();
        otpService.storeOtp(email, otp);
        emailService.sendOtpToEmail(email, otp);

        model.addAttribute("message", "OTP đã được gửi đến email của bạn.");
        return "/auth/reset-password"; // Chuyển sang trang đặt lại mật khẩu
    }

    // Xử lý yêu cầu đặt lại mật khẩu
    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam String email, 
                                       @RequestParam String otp, 
                                       @RequestParam String newPassword, 
                                       Model model) {
        if (!otpService.verifyOtp(email, otp)) {
            model.addAttribute("error", "OTP không hợp lệ hoặc đã hết hạn.");
            return "/auth/reset-password"; // Quay lại trang đặt lại mật khẩu với thông báo lỗi
        }

        // Cập nhật mật khẩu
        authService.updatePassword(email, newPassword);
        otpService.clearOtp(email);

        model.addAttribute("message", "Mật khẩu đã được đặt lại thành công.");
        return "/auth/login"; // Quay lại trang đăng nhập
    }
}
