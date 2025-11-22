package com.project.ecommerce.service;

import com.project.ecommerce.model.User;
import com.project.ecommerce.payload.AddressDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO,
                             User user);

    List<AddressDTO> getAllAddress();

    AddressDTO getAddressById(Long addressId);

    List<AddressDTO> getUserAddress(User user);

    AddressDTO updatedAddress(Long addressId,
                              @Valid AddressDTO addressDTO);

    String deleteAddress(Long addressId);
}
