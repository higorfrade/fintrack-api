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
@RequestMapping("/profile")
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

    @GetMapping("/test")
    public String test() {
        return "Test successful";
    }
}
