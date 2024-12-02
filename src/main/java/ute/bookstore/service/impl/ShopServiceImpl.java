// ShopServiceImpl.java
package ute.bookstore.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ute.bookstore.entity.Shop;
import ute.bookstore.entity.User;
import ute.bookstore.exception.ResourceNotFoundException;
import ute.bookstore.repository.ShopRepository;
import ute.bookstore.repository.UserRepository;
import ute.bookstore.service.IShopService;



@Service
public class ShopServiceImpl implements IShopService {
    @Autowired
    private ShopRepository shopRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public Shop getShopById(Long id) {
        return shopRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
    }

	@Override
	public Shop getShopByUser(String username) {
		// TODO Auto-generated method stub
		return null;
	}

    
}
