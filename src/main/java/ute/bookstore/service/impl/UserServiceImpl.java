package ute.bookstore.service.impl;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import ute.bookstore.entity.User;
import ute.bookstore.entity.UserActivityLog;
import ute.bookstore.enums.UserRole;
import ute.bookstore.repository.UserActivityLogRepository;
import ute.bookstore.repository.UserRepository;
import ute.bookstore.service.IUserService;

@Service
public class UserServiceImpl implements IUserService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired 
	private UserActivityLogRepository activityLogRepository;

	private static final String EMAIL_REGEX = "^[\\w-\\.]+@[\\w-]+\\.[a-z]{2,6}$";
	private static final String PHONE_REGEX = "^\\+?[0-9]{10,12}$";

	@Override
	public User getUserById(Long Id) {
		return userRepository.findById(Id).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));
	}
	
	@Override
	public User findByEmail(String email) {
	    return userRepository.findByEmail(email)
	        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
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
	
	
	
	
	@Override
    public Page<User> getAllUsers(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        
        if (StringUtils.hasText(search)) {
            return userRepository.findByEmailContainingOrFullNameContainingIgnoreCase(
                search, search, pageable);
        }
        return userRepository.findAll(pageable);
    }
    
    @Override
    public User createUser(User user) {
        // Validate input
        if (!Pattern.matches(EMAIL_REGEX, user.getEmail())) {
            throw new ValidationException("Email không hợp lệ");
        }
        if (!Pattern.matches(PHONE_REGEX, user.getPhone())) {
            throw new ValidationException("Số điện thoại không hợp lệ");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ValidationException("Email đã tồn tại");
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set default values
        user.setIsActive(true);
        if (user.getRole() == null) {
            user.setRole(UserRole.USER);
        }
        
        return userRepository.save(user);
    }
    
    @Override
    @Transactional
    public boolean toggleUserStatus(Long userId) {
        User user = getUserById(userId);
        user.setIsActive(!user.getIsActive());
        userRepository.save(user);
        
        // Log activity
        UserActivityLog log = new UserActivityLog();
        log.setUser(user);
        log.setActivity(user.getIsActive() ? "ACTIVATE_ACCOUNT" : "DEACTIVATE_ACCOUNT");
        log.setDescription("Admin " + (user.getIsActive() ? "kích hoạt" : "vô hiệu hóa") + " tài khoản");
        activityLogRepository.save(log);
        
        return user.getIsActive();
    }
    
    @Override
    @Transactional
    public void updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        
        // Validate input
        if (!Pattern.matches(EMAIL_REGEX, userDetails.getEmail())) {
            throw new ValidationException("Email không hợp lệ");
        }
        if (!Pattern.matches(PHONE_REGEX, userDetails.getPhone())) {
            throw new ValidationException("Số điện thoại không hợp lệ");
        }
        if (!user.getEmail().equals(userDetails.getEmail()) && 
            userRepository.existsByEmail(userDetails.getEmail())) {
            throw new ValidationException("Email đã tồn tại");
        }
        
        // Update info
        user.setFullName(userDetails.getFullName());
        user.setPhone(userDetails.getPhone());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        
        // Update password if provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        
        userRepository.save(user);
        
        // Log activity
        UserActivityLog log = new UserActivityLog();
        log.setUser(user);
        log.setActivity("UPDATE_USER");
        log.setDescription("Cập nhật thông tin user");
        activityLogRepository.save(log);
    }
    
    @Override
    @Transactional
    public void updateUserRole(Long userId, UserRole newRole) {
        User user = getUserById(userId);
        UserRole oldRole = user.getRole();
        user.setRole(newRole);
        userRepository.save(user);
        
        // Log activity
        UserActivityLog log = new UserActivityLog();
        log.setUser(user);
        log.setActivity("ROLE_CHANGE");
        log.setDescription("Thay đổi role từ " + oldRole + " thành " + newRole);
        activityLogRepository.save(log);
    }
    
    @Override
    public List<UserActivityLog> getUserActivities(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return activityLogRepository.findByUserId(userId, pageable).getContent();
    }
    
    
    @Override
    @Transactional
    public void updateProfile(String email, String fullName, String phone) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        
        // Validate phone format
        if (phone != null && !phone.isEmpty() && !Pattern.matches(PHONE_REGEX, phone)) {
            throw new ValidationException("Số điện thoại không hợp lệ");
        }
        
        // Update info
        user.setFullName(fullName);
        user.setPhone(phone);
        
        // Save user
        userRepository.save(user);
        
        // Log activity
        UserActivityLog log = new UserActivityLog();
        log.setUser(user);
        log.setActivity("UPDATE_PROFILE");
        log.setDescription("Cập nhật thông tin cá nhân");
        activityLogRepository.save(log);
    }

    @Override
    @Transactional  
    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        
        // Check current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new ValidationException("Mật khẩu hiện tại không đúng");
        }
        
        // Validate new password
        if (newPassword.length() < 6) {
            throw new ValidationException("Mật khẩu mới phải có ít nhất 6 ký tự");
        }
        
        // Update password  
        user.setPassword(passwordEncoder.encode(newPassword));
        
        // Save user
        userRepository.save(user);
        
        // Log activity
        UserActivityLog log = new UserActivityLog();
        log.setUser(user);
        log.setActivity("CHANGE_PASSWORD");
        log.setDescription("Đổi mật khẩu");
        activityLogRepository.save(log);
    }

}
