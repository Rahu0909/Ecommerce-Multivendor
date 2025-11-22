package com.project.ecommerce.controller;

import com.project.ecommerce.model.User;
import com.project.ecommerce.payload.AddressDTO;
import com.project.ecommerce.service.AddressService;
import com.project.ecommerce.utils.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        User user = authUtil.loggedInUser();
        AddressDTO savedAddressDTO = addressService.createAddress(addressDTO,
                user);
        return new ResponseEntity<>(savedAddressDTO,
                HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        List<AddressDTO> addressList = addressService.getAllAddress();
        return new ResponseEntity<>(addressList,
                HttpStatus.OK);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable("addressId") Long addressId) {
        AddressDTO addressDTO = addressService.getAddressById(addressId);
        return new ResponseEntity<>(addressDTO,
                HttpStatus.OK);
    }

    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddress() {
        User user = authUtil.loggedInUser();
        List<AddressDTO> addressList = addressService.getUserAddress(user);
        return new ResponseEntity<>(addressList,
                HttpStatus.OK);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updatedAddress(@PathVariable("addressId") Long addressId,
                                                     @Valid @RequestBody AddressDTO addressDTO) {
        AddressDTO updatedAddress = addressService.updatedAddress(addressId,
                addressDTO);
        return new ResponseEntity<>(updatedAddress,
                HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable("addressId") Long addressId) {
        String status = addressService.deleteAddress(addressId);
        return new ResponseEntity<>(status,
                HttpStatus.OK);
    }
}
