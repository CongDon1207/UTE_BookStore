package ute.bookstore.enums;

import lombok.Getter;

@Getter
public enum DiscountType {
    PERCENT("Giảm theo %"),
    AMOUNT("Giảm giá trực tiếp");

    private final String description;

    DiscountType(String description) {
        this.description = description;
    }
}