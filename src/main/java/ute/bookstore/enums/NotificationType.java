package ute.bookstore.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
    ORDER_STATUS("Order Status Update"),
    PROMOTION("Promotional Notice"),
    SYSTEM("System Notification"),
    CHAT("Chat Message");
    
    private final String description;
    
    NotificationType(String description) {
        this.description = description;
    }
}
