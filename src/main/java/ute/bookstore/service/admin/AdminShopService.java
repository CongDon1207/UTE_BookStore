package ute.bookstore.service.admin;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import ute.bookstore.dto.admin.ShopRevenueStats;
import ute.bookstore.dto.admin.ShopRevenueStats.RevenueByTime;
import ute.bookstore.entity.Shop;
import ute.bookstore.entity.User;
import ute.bookstore.enums.ApprovalStatus;
import ute.bookstore.enums.OrderStatus;
import ute.bookstore.enums.UserRole;
import ute.bookstore.exception.ResourceNotFoundException;
import ute.bookstore.repository.OrderRepository;
import ute.bookstore.repository.ShopRepository;
import ute.bookstore.repository.UserRepository;
import ute.bookstore.service.admin.impl.IAdminShopService;

@Service
public class AdminShopService implements IAdminShopService {

	@Autowired
	private ShopRepository shopRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Override
	public Shop createShop(Shop shop) {
		// Validate
        if (shop.getUser() == null) {
            throw new ValidationException("Chủ shop không được để trống");
        }
        if (!StringUtils.hasText(shop.getName())) {
            throw new ValidationException("Tên shop không được để trống");
        }
        
        shop.setIsActive(true);
        shop.setRating(0.0);
        return shopRepository.save(shop);
	}

	@Override
	@Transactional
	public void updateShop(Long id, Shop shopDetails) {
		Shop shop = getShopById(id);
        shop.setName(shopDetails.getName());
        shop.setDescription(shopDetails.getDescription());
        shop.setPhone(shopDetails.getPhone());
        shop.setLogo(shopDetails.getLogo());
        shop.setAddress(shopDetails.getAddress());
        shopRepository.save(shop);
	}

	@Override
	public Page<Shop> getAllShopsForAdmin(int page, int size, String search) {
	    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
	    if (StringUtils.hasText(search)) {
	        return shopRepository.findByApprovalStatusAndNameContainingOrPhoneContainingOrUser_EmailContaining(
	            ApprovalStatus.APPROVED, search, search, search, pageable);
	    }
	    return shopRepository.findByApprovalStatus(ApprovalStatus.APPROVED, pageable);
	}

	@Override
	@Transactional
	public void deleteShop(Long id) {
	    Shop shop = getShopById(id);
	    
	    // Kiểm tra các ràng buộc
	    if (!shop.getOrders().isEmpty()) {
	        throw new ValidationException("Không thể xóa shop đã có đơn hàng");
	    }
	    
	    if (!shop.getBooks().isEmpty()) {
	        throw new ValidationException("Vui lòng xóa tất cả sách trước khi xóa shop");
	    }
	    
	    if (!shop.getPromotions().isEmpty()) {
	        throw new ValidationException("Vui lòng xóa tất cả khuyến mãi trước khi xóa shop");
	    }
	    
	    // Xóa shop
	    shopRepository.delete(shop);
	}

	@Override
	public Shop getShopById(Long id) {
		return shopRepository.findById(id).orElseThrow(() -> new RuntimeException("Shop không tồn tại"));
	}
	
	
	// Lấy danh sách shop chờ duyệt
    @Override
	public Page<Shop> getPendingShops(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return shopRepository.findByApprovalStatus(ApprovalStatus.PENDING, pageable);
    }
    
    // Bật/tắt trạng thái shop
    @Override
    public boolean toggleShopStatus(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy shop"));
        shop.setIsActive(!shop.getIsActive());
        shopRepository.save(shop);
        return shop.getIsActive();
    }
    
    // Duyệt shop
    
    @Override
    @Transactional
    public Shop approveShop(Long shopId) {
        // Tìm shop và validate
        Shop shop = shopRepository.findById(shopId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy shop với ID: " + shopId));
            
        // Kiểm tra trạng thái hiện tại
        if (shop.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new ValidationException("Chỉ có thể duyệt shop đang ở trạng thái chờ duyệt");
        }

        // Cập nhật trạng thái shop
        shop.setApprovalStatus(ApprovalStatus.APPROVED);
        shop.setIsActive(true);
        shop.setRejectionReason(null); // Xóa lý do từ chối nếu có

        // Lấy và cập nhật role của user thành VENDOR
        User user = shop.getUser();
        if (user != null) {
            user.setRole(UserRole.VENDOR);  // Đổi thành VENDOR
            userRepository.save(user);
        }

        // Lưu shop
        return shopRepository.save(shop);
    }
    
    // Từ chối shop
    @Override
	public Shop rejectShop(Long shopId, String reason) {
        Shop shop = shopRepository.findById(shopId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy shop"));
        shop.setApprovalStatus(ApprovalStatus.REJECTED);
        shop.setRejectionReason(reason);
        return shopRepository.save(shop);
    }
    
    // Cập nhật chiết khấu
    @Override
	public Shop updateCommissionRate(Long shopId, Double rate) {
        if (rate < 0 || rate > 100) {
            throw new IllegalArgumentException("Tỷ lệ chiết khấu phải từ 0-100%");
        }
        Shop shop = shopRepository.findById(shopId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy shop"));
        shop.setCommissionRate(rate);
        return shopRepository.save(shop);
    }
    
    @Override
	public Shop getShopDetail(Long shopId) {
        return shopRepository.findById(shopId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy shop"));
    }
    
    
    public List<Shop> getRecentShops(int limit) {
        return shopRepository.findAllByOrderByIdDesc(PageRequest.of(0, limit));
    }
    
    @Override
    public Page<Shop> getRejectedShops(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return shopRepository.findByApprovalStatus(ApprovalStatus.REJECTED, pageable);
    }

    @Override
	public RevenueByTime getRevenueStats(Shop shop, LocalDateTime from, LocalDateTime to) {
        RevenueByTime stats = new RevenueByTime();
        
        // Tính tổng doanh thu từ đơn hàng DELIVERED
        Double revenue = orderRepository.calculateRevenue(
            shop.getId(), OrderStatus.DELIVERED, from, to);
        stats.setRevenue(revenue != null ? revenue : 0.0);
        
        // Tổng số đơn hàng
        Long total = orderRepository.countOrders(shop.getId(), from, to);
        stats.setTotalOrders(total != null ? total : 0L);
        
        // Đơn đã giao
        Long delivered = orderRepository.countOrdersByStatus(
            shop.getId(), OrderStatus.DELIVERED, from, to);
        stats.setDeliveredOrders(delivered != null ? delivered : 0L);
        
        // Đơn đã hủy
        Long cancelled = orderRepository.countOrdersByStatus(
            shop.getId(), OrderStatus.CANCELLED, from, to); 
        stats.setCancelledOrders(cancelled != null ? cancelled : 0L);
        
        // Tính chiết khấu
        stats.setCommission(stats.getRevenue() * shop.getCommissionRate() / 100);
        
        return stats;
    }
    
    @Override
    public ShopRevenueStats getShopStats(Long shopId) {
        Shop shop = getShopById(shopId);
        ShopRevenueStats stats = new ShopRevenueStats();
        stats.setShopName(shop.getName());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1).toLocalDate().atStartOfDay();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();

        stats.setTodayStats(getRevenueStats(shop, startOfDay, now));
        stats.setWeekStats(getRevenueStats(shop, startOfWeek, now));
        stats.setMonthStats(getRevenueStats(shop, startOfMonth, now));
        stats.setTotalStats(getRevenueStats(shop, null, now));

        return stats;
    }
}
