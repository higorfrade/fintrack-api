package com.fintrack.controller;

import com.fintrack.dto.AuthenticationDTO;
import com.fintrack.dto.UserDTO;
import com.fintrack.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO) {
        UserDTO registeredUser = userService.registerUser(userDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateUser(@RequestParam String token) {
        boolean isActivated = userService.activateUser(token);
        if (isActivated) {
            return ResponseEntity.ok("Conta ativada com sucesso.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("O token de ativação não foi encontrado ou já foi usado.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthenticationDTO authDTO) {
        try {
            // Verifica se a conta existe
            if (!userService.doesAccountExist(authDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "Essa conta não existe. Cadastre-se antes de acessar a plataforma."
                ));
            }

            // Verifica se o perfil está ativo ou não
            if (!userService.isAccountActive(authDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "Sua conta não está ativa. Verifique a caixa de entrada do seu email."
                ));
            }
            Map<String, Object> res = userService.authenticateAndGenerateToken(authDTO);
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<?> deleteAccount() {
        userService.deleteAccount();

        return ResponseEntity.ok("Conta excluída com sucesso.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            userService.forgotPassword(request.get("email"));
            return ResponseEntity.ok(Map.of("message", "E-mail de recuperação enviado!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            userService.resetPassword(request.get("token"), request.get("password"));
            return ResponseEntity.ok(Map.of("message", "Senha alterada com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/update-image")
    public ResponseEntity<UserDTO> updateProfileImage(@RequestBody Map<String, String> request) {
        UserDTO updatedUser = userService.updateProfileImage(request.get("imageUrl"));
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/update-details")
    public ResponseEntity<UserDTO> updateUserDetails(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUserDetails(userDTO));
    }

    @GetMapping("/test")
    public String test() {
        return "Test successful";
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getPublicProfile() {
        UserDTO userDTO = userService.getPublicProfile(null);

        return ResponseEntity.ok(userDTO);
    }
}
