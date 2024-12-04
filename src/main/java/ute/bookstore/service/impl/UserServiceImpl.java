package ute.bookstore.service.impl;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ute.bookstore.entity.User;
import ute.bookstore.repository.UserRepository;
import ute.bookstore.service.IUserService;

@Service
public class UserServiceImpl implements IUserService {
	 @Autowired
	  private UserRepository userRepository;
	 
	 private static final String EMAIL_REGEX = "^[\\w-\\.]+@[\\w-]+\\.[a-z]{2,6}$";
	 private static final String PHONE_REGEX = "^\\+?[0-9]{10,12}$";
	 
	 @Override
	    public User getUserById(Long Id) {
	        return userRepository.findById(Id).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));
	    }
	 @Override
	    public void updateUserInfo(User updatedUser) {
		// Kiểm tra cấu trúc email
	        if (!Pattern.matches(EMAIL_REGEX, updatedUser.getEmail())) {
	            throw new RuntimeException("Email không hợp lệ.");
	        }

	        // Kiểm tra email trùng
	        if (userRepository.existsByEmailAndIdNot(updatedUser.getEmail(), updatedUser.getId())) {
	            throw new RuntimeException("Email đã được sử dụng bởi người dùng khác.");
	        }

	        // Kiểm tra cấu trúc số điện thoại
	        if (!Pattern.matches(PHONE_REGEX, updatedUser.getPhone())) {
	            throw new RuntimeException("Số điện thoại không hợp lệ.");
	        }
		  // Tìm người dùng bằng ID
		    User existingUser = getUserById(updatedUser.getId());
		          
		 // Cập nhật thông tin
	        existingUser.setFullName(updatedUser.getFullName());
	        existingUser.setPhone(updatedUser.getPhone());
	        existingUser.setEmail(updatedUser.getEmail());
	        
	     // Lưu thông tin cập nhật
	        userRepository.save(existingUser);
	    }
	 
	 @Override
	 public void changePassword(User user, String currentPassword, String newPassword, String confirmPassword) {
	        // Kiểm tra nếu mật khẩu mới và xác nhận mật khẩu mới khớp
	        if (!newPassword.equals(confirmPassword)) {
	            throw new RuntimeException("Mật khẩu mới và xác nhận mật khẩu không khớp.");
	        }

	        // Kiểm tra mật khẩu cũ
	        if (user == null || !user.getPassword().equals(currentPassword)) {
	            throw new RuntimeException("Mật khẩu cũ không chính xác.");
	        }

	        // Cập nhật mật khẩu mới
	        user.setPassword(newPassword);
	        userRepository.save(user);
	    }

}
