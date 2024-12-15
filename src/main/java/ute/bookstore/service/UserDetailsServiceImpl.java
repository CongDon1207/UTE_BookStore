package ute.bookstore.service;

import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ute.bookstore.entity.User;
import ute.bookstore.repository.UserRepository;
import ute.bookstore.security.CustomUserDetails;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	System.out.println("Attempting to load user by username: " + username);  // Logging debug
        // Tìm người dùng trong cơ sở dữ liệu theo email (hoặc username)
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Trả về CustomUserDetails thay vì User entity
        return new CustomUserDetails(user);
    }
}
