package com.fintrack.controller;

import com.fintrack.dto.UserDTO;
import com.fintrack.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
