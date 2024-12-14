package ute.bookstore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import ute.bookstore.entity.User;
import ute.bookstore.repository.UserRepository;

@Service
public class AdminUserService {
	@Autowired
    private UserRepository userRepository;
    
    public List<User> getRecentUsers(int limit) {
        return userRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit));
    }
}
