package ute.bookstore.service;

import java.util.List;

import ute.bookstore.entity.Promotion;

public interface IPromotionService {
	List<Promotion> getAllPromotions();
    Promotion savePromotion(Promotion promotion);
    Promotion updatePromotion(Long id, Promotion promotion);
    void deletePromotion(Long id);
}
