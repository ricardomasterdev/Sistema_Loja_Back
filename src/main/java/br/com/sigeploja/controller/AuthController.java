// src/main/java/br/com/sigeploja/controller/AuthController.java
package br.com.sigeploja.controller;

import br.com.sigeploja.payload.LoginRequest;
import br.com.sigeploja.payload.JwtResponse;
import br.com.sigeploja.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );
            UserDetails user = (UserDetails) auth.getPrincipal();

            // 1) Extrai nomes das roles (ex: "ROLE_ADMIN", "ROLE_USER")
            List<String> roles = user.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // 2) Gera o token incluindo as roles
            String token = jwtUtil.generateToken(user.getUsername(), roles);

            // 3) Retorna token + tipo + usuário + roles
            JwtResponse resp = new JwtResponse(
                    token,
                    "Bearer",
                    user.getUsername(),
                    roles
            );
            return ResponseEntity.ok(resp);

        } catch (BadCredentialsException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Usuário ou senha inválidos");
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno: " + ex.getMessage());
        }
    }
}
