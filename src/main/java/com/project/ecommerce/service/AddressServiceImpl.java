package com.project.ecommerce.service;

import com.project.ecommerce.exceptions.ResourceNotFoundException;
import com.project.ecommerce.model.Address;
import com.project.ecommerce.model.User;
import com.project.ecommerce.payload.AddressDTO;
import com.project.ecommerce.repositories.AddressRepository;
import com.project.ecommerce.security.repositories.UserRepository;
import com.project.ecommerce.utils.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl
        implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthUtil authUtil;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO,
                                    User user) {
        Address address = modelMapper.map(addressDTO,
                Address.class);

        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);

        address.setUser(user);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress,
                AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddress() {
        List<Address> addresses = addressRepository.findAll();
        return addresses.stream()
                .map(address -> modelMapper.map(address,
                        AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address",
                        "addressId",
                        addressId));
        return modelMapper.map(address,
                AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserAddress(User user) {
        List<Address> addresses = user.getAddresses();
        return addresses.stream()
                .map(address -> modelMapper.map(address,
                        AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO updatedAddress(Long addressId,
                                     AddressDTO addressDTO) {
        Address addressFromDatabase = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address",
                        "addressId",
                        addressId));
        addressFromDatabase.setCity(addressDTO.getCity());
        addressFromDatabase.setCountry(addressDTO.getCountry());
        addressFromDatabase.setPincode(addressDTO.getPincode());
        addressFromDatabase.setStreet(addressDTO.getStreet());
        addressFromDatabase.setState(addressDTO.getState());
        addressFromDatabase.setBuildingName(addressDTO.getBuildingName());
        Address updatedAddress = addressRepository.save(addressFromDatabase);

        User user = addressFromDatabase.getUser();
        user.getAddresses()
                .removeIf(address -> address.getAddressId()
                        .equals(addressId));
        user.getAddresses()
                .add(updatedAddress);
        userRepository.save(user);
        return modelMapper.map(updatedAddress,
                AddressDTO.class);

    }

    @Override
    public String deleteAddress(Long addressId) {
        Address addressFromDatabase = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address",
                        "addressId",
                        addressId));
        User user = addressFromDatabase.getUser();
        user.getAddresses()
                .removeIf(address -> address.getAddressId()
                        .equals(addressId));
        userRepository.save(user);
        addressRepository.delete(addressFromDatabase);
        return "Address deleted successfully with addressId: " + addressId;
    }

}
