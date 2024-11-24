package ute.bookstore.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    COD("Cash On Delivery", "Pay when receiving the order"),
    VNPAY("VNPay", "Pay via VNPay e-wallet"),
    MOMO("MoMo", "Pay via MoMo e-wallet");
    
    private final String method;
    private final String description;
    
    PaymentMethod(String method, String description) {
        this.method = method;
        this.description = description;
    }
}
