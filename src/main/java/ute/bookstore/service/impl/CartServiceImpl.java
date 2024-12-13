package ute.bookstore.service.impl;

import java.util.ArrayList;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ute.bookstore.entity.Book;
import ute.bookstore.entity.Cart;
import ute.bookstore.entity.CartItem;
import ute.bookstore.entity.User;
import ute.bookstore.repository.BookRepository;
import ute.bookstore.repository.CartItemRepository;
import ute.bookstore.repository.CartRepository;
import ute.bookstore.service.ICartService;

@Service
public class CartServiceImpl implements ICartService {
	
	@Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Override
    @Transactional
    public Cart addToCart(User user, Long bookId, Integer quantity) {
        // Tìm hoặc tạo giỏ hàng cho người dùng
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setItems(new ArrayList<>());
                    newCart.setTotalAmount(0.0);
                    return cartRepository.save(newCart);
                });

        // Tìm sách
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Sách không tồn tại"));

        // Kiểm tra xem sách đã có trong giỏ hàng chưa
        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndBook(cart, book);

        if (existingCartItem.isPresent()) {
            // Nếu sách đã có trong giỏ, thông báo cho người dùng
            throw new RuntimeException("Sách đã có trong giỏ hàng. Bạn không thể thêm nữa.");
        } else {
            // Nếu sách chưa có trong giỏ, thêm mới
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setBook(book);
            cartItem.setQuantity(quantity);
            cart.getItems().add(cartItem);
            cartItemRepository.save(cartItem);
        }

        // Tính tổng giá trị giỏ hàng
        double totalAmount = cart.getItems().stream()
                .mapToDouble(item -> item.getBook().getPrice() * item.getQuantity())
                .sum();
        cart.setTotalAmount(totalAmount);

        return cartRepository.save(cart);
    }

   
    
    @Override
    @Transactional
    public void removeFromCart(User user, Long bookId) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại"));
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Sách không tồn tại"));
        
        Optional<CartItem> cartItem = cartItemRepository.findByCartAndBook(cart, book);
        
        cartItem.ifPresent(item -> {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
            
            // Tính lại tổng giá trị
            double totalAmount = cart.getItems().stream()
                    .mapToDouble(i -> i.getBook().getPrice() * i.getQuantity())
                    .sum();
            cart.setTotalAmount(totalAmount);
            
            cartRepository.save(cart);
        });
    }
    
    @Override
    @Transactional
    public void updateCartItemQuantity(User user, Long bookId, Integer quantity) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại"));
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Sách không tồn tại"));
        
        CartItem cartItem = cartItemRepository.findByCartAndBook(cart, book)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ hàng"));
        
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        
        // Tính lại tổng giá trị
        double totalAmount = cart.getItems().stream()
                .mapToDouble(item -> item.getBook().getPrice() * item.getQuantity())
                .sum();
        cart.setTotalAmount(totalAmount);
        
        cartRepository.save(cart);
    }
    
    @Override
    public Cart getCartByUser(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setItems(new ArrayList<>());
                    newCart.setTotalAmount(0.0);
                    return cartRepository.save(newCart);
                });
    }

	
}
