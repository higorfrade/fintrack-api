package com.fintrack.repository;

import com.fintrack.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // SELECT * FROM users WHERE email = ?
    Optional<UserEntity> findByEmail(String email);

    // SELECT * FROM users WHERE phone_number = ?
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);

    // SELECT * FROM users WHERE activation_token = ?
    Optional<UserEntity> findByActivationToken(String activationToken);

    // DELETE FROM users WHERE email = ?
    @Transactional
    void deleteByEmail(String email);
}
