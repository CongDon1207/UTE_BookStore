package ute.bookstore.service;

import ute.bookstore.entity.User;

public interface IUserService {
	User getUserById(Long Id);
	void changePassword(User user,String currentPassword, String newPassword, String confirmPassword);
    void updateUserInfo(User user);

}
