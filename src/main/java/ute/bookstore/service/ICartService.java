package ute.bookstore.service;

import ute.bookstore.entity.Cart;
import ute.bookstore.entity.User;

public interface ICartService {
	Cart addToCart(User user, Long bookId, Integer quantity) ;
	void removeFromCart(User user, Long bookId) ;
	void updateCartItemQuantity(User user, Long bookId, Integer quantity);
	Cart getCartByUser(User user);

}
