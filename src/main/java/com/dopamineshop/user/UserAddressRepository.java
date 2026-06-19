package com.dopamineshop.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, UUID> {

    List<UserAddress> findAllByUserIdOrderByIsDefaultDescCreatedAtDesc(UUID userId);

    Optional<UserAddress> findByIdAndUserId(UUID id, UUID userId);

    @Modifying
    @Query("UPDATE UserAddress a SET a.isDefault = false WHERE a.user.id = :userId AND a.isDefault = true")
    void clearDefaultForUser(@Param("userId") UUID userId);

    long countByUserId(UUID userId);
}