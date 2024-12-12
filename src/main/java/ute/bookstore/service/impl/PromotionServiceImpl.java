package ute.bookstore.service.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ute.bookstore.entity.Promotion;
import ute.bookstore.repository.PromotionRepository;
import ute.bookstore.service.IPromotionService;

import java.util.List;


@Service
public class PromotionServiceImpl implements IPromotionService {

   @Autowired
   private PromotionRepository promotionRepository;

   @Override
   public List<Promotion> getAllPromotions() {
       return promotionRepository.findAll();
   }

   @Override
   public Promotion savePromotion(Promotion promotion) {
       validatePromotion(promotion);
       return promotionRepository.save(promotion);
   }

   @Override
   public Promotion updatePromotion(Long id, Promotion promotion) {
      promotionRepository.findById(id)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Promotion not found"));
          
      validatePromotion(promotion);
      promotion.setId(id);
      return promotionRepository.save(promotion);
   }

   @Override
   public void deletePromotion(Long id) {
       promotionRepository.deleteById(id);
   }

   private void validatePromotion(Promotion promotion) {
       if(promotion.getStartDate().isAfter(promotion.getEndDate())) {
           throw new IllegalArgumentException("Start date must be before end date");
       }
       
       if(promotion.getDiscount() <= 0) {
           throw new IllegalArgumentException("Discount value must be positive");
       }
   }
}