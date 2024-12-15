package ute.bookstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ute.bookstore.entity.Book;
import ute.bookstore.entity.Cart;
import ute.bookstore.entity.CartItem;
import ute.bookstore.entity.User;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	Optional<CartItem> findByCartAndBook(Cart cart, Book book);
	
	 // Tìm CartItem theo User và Book
    CartItem findByCartUserAndBook(User user, Book book);

}
