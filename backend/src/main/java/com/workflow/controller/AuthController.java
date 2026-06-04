package com.workflow.controller;

import com.workflow.dto.LoginRequest;
import com.workflow.dto.LoginResponse;
import com.workflow.model.User;
import com.workflow.repository.UserRepository;
import com.workflow.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales incorrectas");
        }

        if (!user.isActivo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Usuario inactivo");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales incorrectas");
        }

        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(
                new LoginResponse(
                        token,
                        user.getId(),
                        user.getNombre(),
                        user.getEmail(),
                        user.getRol()
                )
        );
    }
}