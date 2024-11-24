package ute.bookstore.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    NEW("New Order", "Order has been placed"),
    CONFIRMED("Confirmed", "Order has been confirmed by seller"),
    SHIPPING("In Transit", "Order is being delivered"),
    DELIVERED("Delivered", "Order has been delivered successfully"),
    CANCELLED("Cancelled", "Order has been cancelled"),
    REFUNDED("Refunded", "Payment has been refunded");
    
    private final String status;
    private final String description;
    
    OrderStatus(String status, String description) {
        this.status = status;
        this.description = description;
    }
}

