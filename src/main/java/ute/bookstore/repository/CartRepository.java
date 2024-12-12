package ute.bookstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ute.bookstore.entity.Cart;
import ute.bookstore.entity.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
	  Optional<Cart> findByUser(User user);

}
