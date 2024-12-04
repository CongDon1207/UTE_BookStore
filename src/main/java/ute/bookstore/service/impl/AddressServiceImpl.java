package ute.bookstore.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ute.bookstore.entity.Address;
import ute.bookstore.repository.AddressRepository;
import ute.bookstore.service.IAddressService;

@Service
public class AddressServiceImpl implements IAddressService {
	
	@Autowired
    private AddressRepository addressRepository;

    @Override
    public List<Address> getAddressesByUserId(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    @Override
    public Address getAddressById(Long id) {
        return addressRepository.findById(id).orElse(null);
    }

    @Override
    public Address saveAddress(Address address) {
        return addressRepository.save(address);
    }

    @Override
    public void deleteAddressById(Long id) {
        addressRepository.deleteById(id);
    }

	

}
