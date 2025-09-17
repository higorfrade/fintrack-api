package com.fintrack.service;

import com.fintrack.dto.UserDTO;
import com.fintrack.entity.UserEntity;
import com.fintrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public UserDTO registerUser(UserDTO userDTO) {
        UserEntity newUser = toEntity(userDTO);
        newUser.setActivationToken(UUID.randomUUID().toString());
        newUser = userRepository.saveAndFlush(newUser);

        // Mandar email de ativação
        String activationLink = "http://localhost:8080/api/profile/activate?token=" + newUser.getActivationToken();
        String textLink = "Ativar Conta";
        String maskLink = "<a href=\"" + activationLink + "\">" + textLink + "</a>";
        String subject = "Ative sua conta na FinTrack";
        String body = """
                Olá %s,
                Seja muito bem-vindo à nossa plataforma de controle financeiro.
                
                Clique no link a seguir para ativar a sua conta: %s
                """.formatted(newUser.getName(), maskLink);
        emailService.sendEmail(newUser.getEmail(), subject, body);

        return toDTO(newUser);
    }

    public UserEntity toEntity(UserDTO userDTO) {
        return UserEntity.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .phoneNumber(userDTO.getPhoneNumber())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
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
}
