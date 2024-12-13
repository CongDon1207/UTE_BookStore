package ute.bookstore.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ute.bookstore.repository.BookRepository;
import ute.bookstore.repository.CartItemRepository;
import ute.bookstore.repository.OrderItemRepository;
import ute.bookstore.repository.OrderRepository;
import ute.bookstore.entity.Address;
import ute.bookstore.entity.Book;
import ute.bookstore.entity.CartItem;
import ute.bookstore.entity.Order;
import ute.bookstore.entity.OrderItem;
import ute.bookstore.entity.Shop;
import ute.bookstore.entity.User;
import ute.bookstore.enums.OrderStatus;
import ute.bookstore.enums.PaymentMethod;
import ute.bookstore.exception.ResourceNotFoundException;
import ute.bookstore.service.INotificationService;
import ute.bookstore.service.IOrderService;


import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements IOrderService {
    private final OrderRepository orderRepository;
    private final INotificationService notificationService;
    
    @Autowired
    private BookRepository bookRepository;  // Thêm vào để truy cập và cập nhật Book
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;

    public OrderServiceImpl(OrderRepository orderRepository, INotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Page<Order> getOrdersByShopAndStatus(Long shopId, String status, Pageable pageable) {
        if ("ALL".equals(status)) {
            return orderRepository.findByShopId(shopId, pageable);
        }
        return orderRepository.findByShopIdAndStatus(shopId, OrderStatus.valueOf(status), pageable);
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    @Override
    public void updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrderById(id);
        
        // Validate status transition
        validateStatusTransition(order.getStatus(), status);
        
        // Update order
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        
        orderRepository.save(order);
        
        // Send notification to customer
        notificationService.sendOrderStatusNotification(order);
    }

    
     
    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Define allowed transitions
        switch (currentStatus) {
            case NEW:
                if (newStatus != OrderStatus.CONFIRMED && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from NEW to " + newStatus);
                }
                break;
            case CONFIRMED:
                if (newStatus != OrderStatus.SHIPPING && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from CONFIRMED to " + newStatus);
                }
                break;
            case SHIPPING:
                if (newStatus != OrderStatus.DELIVERED && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from SHIPPING to " + newStatus);
                }
                break;
            case DELIVERED:
                if (newStatus != OrderStatus.REFUNDED) {
                    throw new IllegalStateException("Invalid status transition from DELIVERED to " + newStatus);
                }
                break;
            case CANCELLED:
            case REFUNDED:
                throw new IllegalStateException("Cannot change status of " + currentStatus + " order");
            default:
                throw new IllegalStateException("Unknown order status: " + currentStatus);
        }
    }
 // Hàm tạo đơn hàng
    public Order createOrder(User user, Shop shop, Address address, List<Book> books, List<Integer> quantities, PaymentMethod paymentMethod, Double totalAmount) {
        if (books == null || books.isEmpty() || quantities == null || quantities.isEmpty()) {
            throw new IllegalArgumentException("Danh sách sách và số lượng không được rỗng.");
        }
        if (books.size() != quantities.size()) {
            throw new IllegalArgumentException("Danh sách sách và số lượng phải có cùng kích thước.");
        }

        // Khởi tạo Order
        Order order = new Order();
        order.setUser(user);
        order.setShop(shop);
        order.setDeliveryAddress(address);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(OrderStatus.NEW);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setTotalAmount(totalAmount);

        // Lưu đơn hàng vào cơ sở dữ liệu
        orderRepository.save(order);

        // Thêm các chi tiết đơn hàng
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);

            if (book.getQuantity() < quantities.get(i)) {
                throw new RuntimeException("Không đủ số lượng sách: " + book.getTitle());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(book);
            orderItem.setQuantity(quantities.get(i));
            orderItem.setPrice(book.getPrice());
            orderItemRepository.save(orderItem);

            // Cập nhật số lượng sách
            book.setQuantity(book.getQuantity() - quantities.get(i));
         // Kiểm tra nếu số lượng sách là 0, cập nhật isAvailable thành false
            if (book.getQuantity() == 0) {
                book.setIsAvailable(false);  // Cập nhật trạng thái có sẵn
            }
            bookRepository.save(book);
            
            
        }
        // Xóa các sản phẩm đã đặt khỏi giỏ hàng của người dùng
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            CartItem cartItem = cartItemRepository.findByCartUserAndBook(user, book);
            if (cartItem != null) {
                cartItemRepository.delete(cartItem);  // Xóa sản phẩm khỏi giỏ hàng
            }
        }

        return order;
    }
    public CartItem findCartItemByUserAndBook(User user, Book book) {
        return cartItemRepository.findByCartUserAndBook(user, book);
    }
    
 // Cập nhật phương thức với phân trang
    public Page<Order> getOrdersByStatus(OrderStatus status, Long userId, Pageable pageable) {
        return orderRepository.findByStatusAndUserId(status, userId, pageable);
    }

}