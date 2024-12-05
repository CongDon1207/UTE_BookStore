package ute.bookstore.service;

import java.util.List;

import ute.bookstore.entity.Address;

public interface IAddressService {
	List<Address> getAddressesByUserId(Long userId);

    Address getAddressById(Long id);

    Address saveAddress(Address address);

    void deleteAddressById(Long id);

}
