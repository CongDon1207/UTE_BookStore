package ute.bookstore.service;

import java.util.List;

import org.springframework.data.domain.Page;

import jakarta.transaction.Transactional;
import ute.bookstore.entity.User;
import ute.bookstore.entity.UserActivityLog;
import ute.bookstore.enums.UserRole;

public interface IUserService {
	User getUserById(Long Id);
	void changePassword(User user,String currentPassword, String newPassword, String confirmPassword);
    void updateUserInfo(User user);
    User findByEmail(String email);
    
    Page<User> getAllUsers(int page, int size, String search);
    User createUser(User user);
    boolean toggleUserStatus(Long userId);
    void updateUserRole(Long userId, UserRole newRole);
    List<UserActivityLog> getUserActivities(Long userId, int page, int size);
    void updateUser(Long id, User userDetails);
	void changePassword(String email, String currentPassword, String newPassword);
	void updateProfile(String email, String fullName, String phone);
}
