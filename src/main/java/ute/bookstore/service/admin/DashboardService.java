package ute.bookstore.service.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ute.bookstore.dto.admin.*;
import ute.bookstore.repository.UserRepository;
import ute.bookstore.repository.ShopRepository;
import ute.bookstore.repository.BookRepository;
import ute.bookstore.repository.PromotionRepository;
import ute.bookstore.enums.ApprovalStatus;

@Service
public class DashboardService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ShopRepository shopRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private PromotionRepository promotionRepository;
    
    public DashboardStatsDTO getDashboardStats() {
        return new DashboardStatsDTO(
            userRepository.count(),
            shopRepository.count(),
            bookRepository.count(),
            promotionRepository.count(),
            shopRepository.countByIsActiveTrue(),
            shopRepository.countByApprovalStatus(ApprovalStatus.PENDING)
        );
    }
}