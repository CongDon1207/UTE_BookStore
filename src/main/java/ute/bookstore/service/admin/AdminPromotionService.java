package ute.bookstore.service.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ute.bookstore.entity.Promotion;
import ute.bookstore.repository.PromotionRepository;
import java.time.LocalDateTime;

@Service
@Transactional
public class AdminPromotionService {
    
    @Autowired
    private PromotionRepository promotionRepository;
    
    public Page<Promotion> getAllPromotions(Pageable pageable) {
        return promotionRepository.findAllByFilters(null, pageable);
    }
    
    public Page<Promotion> searchPromotions(String keyword, Pageable pageable) {
        return promotionRepository.findAllByFilters(keyword, pageable);
    }
    
    public Promotion getPromotionById(Long id) {
        return promotionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi"));
    }
    
    public Promotion createPromotion(Promotion promotion) {
        validatePromotion(promotion);
        
        if (promotionRepository.existsByCode(promotion.getCode())) {
            throw new RuntimeException("Mã khuyến mãi đã tồn tại");
        }
        
        if (promotion.getStartDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Thời gian bắt đầu phải sau thời điểm hiện tại");
        }
        
        promotion.setIsActive(true);
        return promotionRepository.save(promotion);
    }
    
    public Promotion updatePromotion(Promotion promotion) {
        validatePromotion(promotion);
        
        Promotion existingPromotion = getPromotionById(promotion.getId());
        if (!existingPromotion.getCode().equals(promotion.getCode()) && 
            promotionRepository.existsByCode(promotion.getCode())) {
            throw new RuntimeException("Mã khuyến mãi đã tồn tại");
        }
        
        // Giữ nguyên trạng thái active
        promotion.setIsActive(existingPromotion.getIsActive());
        
        return promotionRepository.save(promotion);
    }
    
    public void deletePromotion(Long id) {
        Promotion promotion = getPromotionById(id);
        promotionRepository.delete(promotion);
    }
    
    public boolean togglePromotionStatus(Long id) {
        Promotion promotion = getPromotionById(id);
        promotion.setIsActive(!promotion.getIsActive());
        promotionRepository.save(promotion);
        return promotion.getIsActive();
    }
    
    private void validatePromotion(Promotion promotion) {
        if (promotion.getCode() == null || promotion.getCode().trim().isEmpty()) {
            throw new RuntimeException("Mã khuyến mãi không được để trống");
        }
        
        if (!promotion.getCode().matches("^[A-Za-z0-9]+$")) {
            throw new RuntimeException("Mã khuyến mãi chỉ được chứa chữ cái và số");
        }
        
        if (promotion.getDiscount() == null || promotion.getDiscount() <= 0) {
            throw new RuntimeException("Giá trị giảm giá phải lớn hơn 0");
        }
        
        if (promotion.getDiscountType() == null) {
            throw new RuntimeException("Vui lòng chọn loại giảm giá");
        }
        
        // So sánh string thay vì enum
        if ("PERCENT".equals(promotion.getDiscountType()) && promotion.getDiscount() > 100) {
            throw new RuntimeException("Giảm giá theo phần trăm không được vượt quá 100%");
        }
        
        if (promotion.getStartDate() == null || promotion.getEndDate() == null) {
            throw new RuntimeException("Thời gian bắt đầu và kết thúc không được để trống");
        }
        
        if (promotion.getStartDate().isAfter(promotion.getEndDate())) {
            throw new RuntimeException("Thời gian bắt đầu phải trước thời gian kết thúc");
        }
        
        if (promotion.getMinOrderAmount() != null && promotion.getMinOrderAmount() < 0) {
            throw new RuntimeException("Giá trị đơn hàng tối thiểu không được âm");
        }
        
        if (promotion.getMaxUses() != null && promotion.getMaxUses() <= 0) {
            throw new RuntimeException("Số lần sử dụng tối đa phải lớn hơn 0");
        }
    }
}