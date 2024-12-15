package ute.bookstore.controller;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.eyewear.DTO.request.AuthResponse;
import com.eyewear.DTO.request.UserRequest;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ute.bookstore.dto.AuthRequest;
import ute.bookstore.dto.LoginRequest;
import ute.bookstore.dto.LoginResponse;
import ute.bookstore.dto.RegisterRequest;
import ute.bookstore.entity.User;
import ute.bookstore.service.AuthService;
import ute.bookstore.service.EmailService;
import ute.bookstore.service.JwtService;
import ute.bookstore.service.OtpService;
import ute.bookstore.util.JwtUtil;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final EmailService emailService;
	private final OtpService otpService;
	@Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JwtService jwtService;
    @GetMapping("/login")
    public String login() {
        return "login" ;
    }
	// Hiển thị trang đăng ký
	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("registerRequest", new RegisterRequest());
		return "register"; // Trả về trang đăng ký Thymeleaf
	}
	@GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password"; // Tên file HTML của bạn (không cần phần mở rộng .html)
    }
	// Endpoint đăng ký tài khoản
	@PostMapping("/register")
	public String register(@ModelAttribute RegisterRequest registerRequest, Model model) {
	    // Kiểm tra email đã tồn tại
	    if (authService.isEmailExist(registerRequest.getEmail())) {
	        model.addAttribute("error", "Email đã tồn tại.");
	        return "register";
	    }

	    // Tạo tài khoản mới với trạng thái chưa kích hoạt
	    User user = authService.createInactiveUser(registerRequest);

	    // Tạo và gửi OTP qua email
	    String otp = emailService.generateOtp();
	    authService.saveOtpForUser(user, otp);
	    emailService.sendOtpToEmail(user.getEmail(), otp);

	    model.addAttribute("message", "OTP đã được gửi vào email của bạn.");
	    model.addAttribute("email", user.getEmail());
	    return "verifyOtp"; // Chuyển đến trang nhập OTP
	}


	// Endpoint xác thực OTP
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam String email, @RequestParam String otp, Model model) {
	    if (authService.verifyOtp(email, otp)) {
	        model.addAttribute("message", "Tài khoản đã được kích hoạt thành công. Vui lòng đăng nhập.");
	        return "login"; // Chuyển đến trang đăng nhập
	    } else {
	        model.addAttribute("error", "OTP không đúng hoặc đã hết hạn.");
	        return "verifyOtp"; // Quay lại trang nhập OTP với thông báo lỗi
	    }
	}


	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody AuthRequest authRequest) {
        System.out.println("LOGIN: ");

	    // Xác thực người dùng với tên đăng nhập và mật khẩu
	    Authentication authentication = authenticationManager.authenticate(
	        new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
	    System.out.println(authentication.isAuthenticated());
	    if (authentication.isAuthenticated()) {
	        // Lấy thông tin người dùng sau khi xác thực
	        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

	        // Lấy quyền của người dùng
	        Collection<String> roles = userDetails.getAuthorities().stream()
	                .map(GrantedAuthority::getAuthority)
	                .collect(Collectors.toList());

	        // Tạo token JWT
	        String token = jwtService.generateToken(userDetails.getUsername(), roles);
	        System.out.println("TOKEN LOGIN CONTROLLER: " + token);

	        // Định nghĩa URL redirect sau khi đăng nhập thành công (tuỳ chỉnh theo nhu cầu)
	        String redirectUrl = "/home";  // Thay đổi nếu cần

	        // Tạo đối tượng response chứa token và URL redirect
	        LoginResponse loginResponse = new LoginResponse(token, redirectUrl);
	        
	        // Trả về response với mã thành công (200 OK)
	        return ResponseEntity.ok(loginResponse);
	    } else {
	        // Trường hợp thông tin đăng nhập không hợp lệ
	        throw new RuntimeException("Invalid login credentials");
	    }
	}

	@PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody UserRequest authRequest, HttpSession session) throws Exception {
       try {
           authenticationManager.authenticate(
                   new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
       } catch (BadCredentialsException e) {
           throw new Exception("Incorrect username or password", e);
       }

       UserDetails userDetails = detailsService.loadUserByUsername(authRequest.getEmail());
       final String jwt = jwtService.generateToken(userDetails.getUsername());
       
       User user = userService.getUserByEmail(authRequest.getEmail());
       session.setAttribute("userId", user.getId());

       return ResponseEntity.ok(new AuthResponse(jwt));
    }
	// Xử lý yêu cầu gửi OTP
    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam String email, Model model) {
        if (!authService.isEmailExist(email)) {
            model.addAttribute("error", "Email không tồn tại.");
            return "forgot-password"; // Quay lại trang quên mật khẩu với thông báo lỗi
        }

        // Gửi OTP qua email
        String otp = emailService.generateOtp();
        otpService.storeOtp(email, otp);
        emailService.sendOtpToEmail(email, otp);

        model.addAttribute("message", "OTP đã được gửi đến email của bạn.");
        return "reset-password"; // Chuyển sang trang đặt lại mật khẩu
    }

    // Xử lý yêu cầu đặt lại mật khẩu
    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam String email, 
                                       @RequestParam String otp, 
                                       @RequestParam String newPassword, 
                                       Model model) {
        if (!otpService.verifyOtp(email, otp)) {
            model.addAttribute("error", "OTP không hợp lệ hoặc đã hết hạn.");
            return "reset-password"; // Quay lại trang đặt lại mật khẩu với thông báo lỗi
        }

        // Cập nhật mật khẩu
        authService.updatePassword(email, newPassword);
        otpService.clearOtp(email);

        model.addAttribute("message", "Mật khẩu đã được đặt lại thành công.");
        return "login"; // Quay lại trang đăng nhập
    }
}
