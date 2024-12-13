package ute.bookstore.entity;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ute.bookstore.enums.DiscountType;
@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String code;
    
    private Double discount;
    
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(name = "max_uses")
    private Integer maxUses;
    
    @Column(name = "min_order_amount")
    private Double minOrderAmount;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;
    
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    // Thêm trường để phân biệt loại giảm giá
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type")
    private DiscountType discountType; // PERCENT hoặc AMOUNT

    public String getDiscountType(){
        if(this.discountType != null){
            return this.discountType.name();
        }
        return null;
    }
}
