package ute.bookstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ute.bookstore.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findById(Long id);

	boolean existsByEmailAndIdNot(String email, Long id);

	Optional<User> findByEmail(String email);

	Page<User> findByEmailContainingOrFullNameContainingIgnoreCase(String email, String name, Pageable pageable);

	boolean existsByEmail(String email);

	List<User> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
