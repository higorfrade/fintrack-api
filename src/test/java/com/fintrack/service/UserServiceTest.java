package com.fintrack.service;

import com.fintrack.dto.UserDTO;
import com.fintrack.entity.UserEntity;
import com.fintrack.repository.UserRepository;
import com.fintrack.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("🧩 Testes unitários para UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(userService, "activationUrl", "http://localhost:8080/activate?token=");
    }

    @Test
    @DisplayName("Deve registrar um usuário com sucesso e enviar email de ativação")
    void registerUserSuccess() {

        UserDTO dto = new UserDTO();
        dto.setName("Teste");
        dto.setEmail("teste@email.com");
        dto.setPassword("123");

        when(passwordEncoder.encode("123")).thenReturn("encodedPass");
        when(userRepository.saveAndFlush(Mockito.<UserEntity>any()))
                .thenAnswer(invocation -> invocation.getArgument(0, UserEntity.class));

        UserDTO result = userService.registerUser(dto);

        assertThat(result.getName()).isEqualTo("Teste");
        verify(emailService, times(1))
                .sendActivationEmail(eq("teste@email.com"), anyString(), eq("Teste"), contains("activate?token="), eq(1L));
    }

    @Test
    @DisplayName("Deve enviar uma exceção ao salvar um usuário")
    void registerUserError() {

        UserDTO dto = new UserDTO();
        dto.setName("Teste Silva");
        dto.setEmail("testesilva@email.com");
        dto.setPassword("321");

        when(passwordEncoder.encode("321")).thenReturn("encodedPass");
        when(userRepository.saveAndFlush(any(UserEntity.class)))
                .thenThrow(new RuntimeException("Erro ao salvar no banco"));


        assertThatThrownBy(() -> userService.registerUser(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Erro ao salvar no banco");

        verify(emailService, never()).sendActivationEmail(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve ativar usuário com token válido")
    void activateUserSuccess() {

        UserEntity user = new UserEntity();
        user.setActivationToken("token123");

        when(userRepository.findByActivationToken("token123")).thenReturn(Optional.of(user));

        boolean activated = userService.activateUser("token123");

        assertThat(activated).isTrue();
        assertThat(user.getIsActive()).isTrue();
        verify(userRepository).saveAndFlush(user);
    }

    @Test
    @DisplayName("Deve retornar falso ao tentar ativar usuário com token inválido")
    void activateUserError() {

        when(userRepository.findByActivationToken("invalid")).thenReturn(Optional.empty());

        boolean activated = userService.activateUser("invalid");

        assertThat(activated).isFalse();
        verify(userRepository,  never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Deve retornar true quando usuário está ativo")
    void isAccountActiveSuccess() {

        UserEntity user = new UserEntity();
        user.setIsActive(true);
        when(userRepository.findByEmail("teste@email.com")).thenReturn(Optional.of(user));

        boolean active = userService.isAccountActive("teste@email.com");

        assertThat(active).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando usuário não existe")
    void isAccountActiveError() {

        when(userRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());

        boolean active = userService.isAccountActive("naoexiste@email.com");

        assertThat(active).isFalse();
    }
}