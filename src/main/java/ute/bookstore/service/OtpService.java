package ute.bookstore.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OtpService {

    private final Map<String, String> otpCache = new HashMap<>(); // Lưu OTP tạm thời

    // Lưu OTP
    public void storeOtp(String email, String otp) {
        otpCache.put(email, otp);
    }

    // Kiểm tra OTP
    public boolean verifyOtp(String email, String otp) {
        return otp.equals(otpCache.get(email));
    }

    // Xóa OTP sau khi sử dụng
    public void clearOtp(String email) {
        otpCache.remove(email);
    }
}
