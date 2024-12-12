package ute.bookstore.service.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import ute.bookstore.entity.Shop;
import ute.bookstore.repository.ShopRepository;
import ute.bookstore.service.admin.impl.IAdminShopService;

@Service
public class AdminShopService implements IAdminShopService {

	@Autowired
	private ShopRepository shopRepository;

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
			return shopRepository.findByNameContainingOrPhoneContainingOrUser_EmailContaining(search, search, search,
					pageable);
		}
		return shopRepository.findAll(pageable);
	}

	@Override
    @Transactional
    public boolean toggleShopStatus(Long id) {
        Shop shop = getShopById(id);
        shop.setIsActive(!shop.getIsActive());
        shopRepository.save(shop);
        return shop.getIsActive();
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

}
