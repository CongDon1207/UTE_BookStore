package ute.bookstore.service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import ute.bookstore.dto.LoginRequest;
import ute.bookstore.dto.RegisterRequest;
import ute.bookstore.entity.User;
import ute.bookstore.enums.UserRole;
import ute.bookstore.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	private final OtpService otpService; // Thêm OtpService để lưu và kiểm tra OTP
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public ResponseEntity<String> register(RegisterRequest registerRequest) {
		// Kiểm tra email có tồn tại trong hệ thống không
		if (userRepository.existsByEmail(registerRequest.getEmail())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email đã tồn tại.");
		}

		// Mã hóa mật khẩu
		String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

		// Tạo người dùng mới
		User user = new User();
		user.setEmail(registerRequest.getEmail());
		user.setPassword(encodedPassword);
		user.setFullName(registerRequest.getFullName());
		user.setPhone(registerRequest.getPhone());
		user.setRole(UserRole.GUEST); // Vai trò mặc định là GUEST
		user.setIsActive(false); // Người dùng mới chưa được kích hoạt

		// Lưu người dùng vào cơ sở dữ liệu
		userRepository.save(user);

		// Tạo OTP và gửi qua email
		String otp = emailService.generateOtp();
		otpService.storeOtp(registerRequest.getEmail(), otp); // Lưu OTP
		emailService.sendOtpToEmail(registerRequest.getEmail(), otp); // Gửi OTP qua email

		return ResponseEntity.status(HttpStatus.CREATED).body("Tài khoản đã được tạo. OTP đã được gửi vào email.");
	}

	// Kiểm tra nếu email đã tồn tại
	public boolean isEmailExist(String email) {
		return userRepository.existsByEmail(email);
	}

	public String authenticateAndGenerateToken(LoginRequest loginRequest) throws Exception {
	    // Kiểm tra người dùng có tồn tại không
	    User user = userRepository.findByEmail(loginRequest.getEmail())
	            .orElseThrow(() -> new Exception("Email không tồn tại"));

	    // Kiểm tra mật khẩu của người dùng
	    if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
	        throw new Exception("Mật khẩu không chính xác");
	    }

	    // Xác thực người dùng
	    Authentication authentication = authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

	    // Đặt thông tin người dùng vào SecurityContext
	    SecurityContextHolder.getContext().setAuthentication(authentication);

	    // Lấy danh sách roles từ user (có thể là một danh sách nếu có nhiều quyền)
	    List<String> roles = Collections.singletonList(user.getRole().name());

	    // Tạo JWT từ email hoặc username và danh sách quyền
	    return jwtService.generateToken(user.getEmail(), roles);
	}


	// Cập nhật mật khẩu của người dùng
	public void updatePassword(String email, String newPassword) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}
	public User createInactiveUser(RegisterRequest registerRequest) {
	    User user = new User();
	    user.setEmail(registerRequest.getEmail());
	    user.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // Mã hóa mật khẩu
	    user.setFullName(registerRequest.getFullName());
	    user.setRole(UserRole.USER); // Gán quyền USER mặc định
	    user.setIsActive(false); // Chưa kích hoạt
	    return userRepository.save(user);
	}
	public void saveOtpForUser(User user, String otp) {
	    user.setOtp(otp);
	    user.setOtpExpiry(LocalDateTime.now().plusMinutes(10)); // OTP hết hạn sau 10 phút
	    userRepository.save(user);
	}
	public boolean verifyOtp(String email, String otp) {
	    User user = userRepository.findByEmail(email)
	        .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

	    // Kiểm tra OTP và thời gian hết hạn
	    if (otp.equals(user.getOtp()) && user.getOtpExpiry().isAfter(LocalDateTime.now())) {
	        user.setIsActive(true); // Kích hoạt tài khoản
	        user.setOtp(null); // Xóa OTP sau khi xác thực
	        user.setOtpExpiry(null);
	        userRepository.save(user);
	        return true;
	    }
	    return false;
	}

	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
	}


}
