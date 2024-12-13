package ute.bookstore.enums;

import lombok.Getter;

@Getter
public enum ApprovalStatus {
	PENDING("Chờ duyệt"),
    APPROVED("Đã duyệt"),
    REJECTED("Từ chối");
    
    private String displayValue;
    
    ApprovalStatus(String displayValue) {
        this.displayValue = displayValue;
    }
}
