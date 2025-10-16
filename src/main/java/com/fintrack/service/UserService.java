package com.fintrack.service;

import com.fintrack.dto.AuthenticationDTO;
import com.fintrack.dto.UserDTO;
import com.fintrack.entity.UserEntity;
import com.fintrack.repository.UserRepository;
import com.fintrack.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Value("${activation.url}")
    private String activationUrl;

    public UserDTO registerUser(UserDTO userDTO) {
        UserEntity newUser = toEntity(userDTO);
        newUser.setActivationToken(UUID.randomUUID().toString());
        newUser = userRepository.saveAndFlush(newUser);

        // Mandar email de ativação
        String activationLink = activationUrl + newUser.getActivationToken();
        String subject = "Ative sua conta na Fintrack e comece a organizar suas finanças!";
        String name = newUser.getName();

        emailService.sendActivationEmail(newUser.getEmail(), subject, name, activationLink, 1L);


        return toDTO(newUser);
    }

    public UserEntity toEntity(UserDTO userDTO) {
        return UserEntity.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .phoneNumber(userDTO.getPhoneNumber())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .profileImageUrl(userDTO.getProfileImageUrl())
                .createdAt(userDTO.getCreatedAt())
                .updatedAt(userDTO.getUpdatedAt())
                .build();
    }

    public UserDTO toDTO(UserEntity userEntity) {
        return UserDTO.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .phoneNumber(userEntity.getPhoneNumber())
                .email(userEntity.getEmail())
                .profileImageUrl(userEntity.getProfileImageUrl())
                .createdAt(userEntity.getCreatedAt())
                .updatedAt(userEntity.getUpdatedAt())
                .build();
    }

    public boolean activateUser(String activationToken) {
        return userRepository.findByActivationToken(activationToken)
                .map(user -> {
                    user.setIsActive(true);
                    userRepository.saveAndFlush(user);
                    return true;
                })
                .orElse(false);
    }

    // Verifica se o usuário está ativo
    public boolean isAccountActive(String email) {
        return userRepository.findByEmail(email)
                .map(UserEntity::getIsActive)
                .orElse(false);
    }

    // Retorna o usuário atual
    public UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + authentication.getName()));
    }

    // Retorna o perfil público do usuário
    public UserDTO getPublicProfile(String email) {
        UserEntity currentUser = null;

        if (email == null) {
            currentUser = getCurrentUser();
        } else {
            currentUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));
        }

        return UserDTO.builder()
                .id(currentUser.getId())
                .name(currentUser.getName())
                .phoneNumber(currentUser.getPhoneNumber())
                .email(currentUser.getEmail())
                .profileImageUrl(currentUser.getProfileImageUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthenticationDTO authDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));

            // Gera JWT token
            String token = jwtUtil.generateToken(authDTO.getEmail());
            return Map.of(
                    "token", token,
                    "user", getPublicProfile(authDTO.getEmail())
            );
        } catch (Exception e) {
            throw new RuntimeException("Email ou senha incorreta.");
        }
    }

    @Transactional
    public void deleteAccount() {
        UserEntity user = getCurrentUser();
        userRepository.delete(user);

        SecurityContextHolder.clearContext();
    }
}
