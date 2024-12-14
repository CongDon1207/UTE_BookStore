package ute.bookstore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
}
