package com.dopamineshop.user;

import com.dopamineshop.user.dto.CreateAddressRequest;
import com.dopamineshop.user.dto.UserAddressDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAddressService {

    private final UserAddressRepository addressRepository;
    private final UserRepository userRepository;

    public List<UserAddressDto> getMyAddresses(UUID userId) {
        return addressRepository.findAllByUserIdOrderByIsDefaultDescCreatedAtDesc(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public UserAddressDto createAddress(UUID userId, CreateAddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        boolean isDefault = Boolean.TRUE.equals(request.isDefault())
                || addressRepository.countByUserId(userId) == 0;

        if (isDefault) {
            addressRepository.clearDefaultForUser(userId);
        }

        UserAddress address = UserAddress.builder()
                .user(user)
                .label(request.label())
                .city(request.city())
                .street(request.street())
                .house(request.house())
                .apartment(request.apartment())
                .entrance(request.entrance())
                .floor(request.floor())
                .comment(request.comment())
                .isDefault(isDefault)
                .build();

        UserAddress saved = addressRepository.save(address);
        return toDto(saved);
    }

    @Transactional
    public UserAddressDto updateAddress(UUID userId, UUID addressId, CreateAddressRequest request) {
        UserAddress address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        boolean isDefault = Boolean.TRUE.equals(request.isDefault());

        if (isDefault && !Boolean.TRUE.equals(address.getIsDefault())) {
            addressRepository.clearDefaultForUser(userId);
        }

        address.setLabel(request.label());
        address.setCity(request.city());
        address.setStreet(request.street());
        address.setHouse(request.house());
        address.setApartment(request.apartment());
        address.setEntrance(request.entrance());
        address.setFloor(request.floor());
        address.setComment(request.comment());
        address.setIsDefault(isDefault);

        return toDto(addressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(UUID userId, UUID addressId) {
        UserAddress address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        addressRepository.delete(address);
    }

    @Transactional
    public void setDefaultAddress(UUID userId, UUID addressId) {
        UserAddress address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        if (Boolean.TRUE.equals(address.getIsDefault())) {
            return;
        }

        addressRepository.clearDefaultForUser(userId);
        address.setIsDefault(true);
        addressRepository.save(address);
    }

    private UserAddressDto toDto(UserAddress address) {
        return new UserAddressDto(
                address.getId(),
                address.getLabel(),
                address.getCity(),
                address.getStreet(),
                address.getHouse(),
                address.getApartment(),
                address.getEntrance(),
                address.getFloor(),
                address.getComment(),
                address.getIsDefault()
        );
    }
}