package ute.bookstore.service.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ute.bookstore.entity.Book;
import ute.bookstore.entity.Promotion;
import ute.bookstore.entity.Shop;
import ute.bookstore.repository.BookRepository;
import ute.bookstore.repository.PromotionRepository;
import ute.bookstore.service.IPromotionService;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class PromotionServiceImpl implements IPromotionService {

   @Autowired
   private PromotionRepository promotionRepository;
   
   @Autowired 
   private BookRepository bookRepository;

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
   
   @Override
   public Promotion getPromotionById(Long id) {
       return promotionRepository.findById(id)
               .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khuyến mãi"));
   }
   
   @Override
   public List<Promotion> getShopVouchers(Shop shop) {
       return promotionRepository.findShopVouchers(shop);
   }
   
   @Override
   public List<Promotion> findShopDiscounts(Shop shop) {
       return promotionRepository.findShopDiscounts(shop);
   }
}