package ute.bookstore.enums;



import lombok.Getter;

@Getter
public enum UserRole {
    GUEST("Guest User"),
    USER("Regular User"),
    VENDOR("Shop Vendor"),
    ADMIN("Administrator"),
    SHIPPER("Delivery Person");
    
    private final String description;
    
    UserRole(String description) {
        this.description = description;
    }
}
