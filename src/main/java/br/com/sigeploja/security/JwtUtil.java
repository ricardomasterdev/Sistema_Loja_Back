// src/main/java/br/com/sigeploja/security/JwtUtil.java
package br.com.sigeploja.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {
    // Chave HS512 forte (≥ 512 bits)
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    /**
     * Gera um JWT contendo:
     *  - subject = username
     *  - claim "roles" = lista de perfis (e.g. ["ADMIN","USER"])
     *  - issuedAt = agora
     */
    public String generateToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                // opcional: .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 dia
                .signWith(key)
                .compact();
    }

    /** Extrai o nome de usuário (subject) do token JWT */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /** Extrai a lista de roles do token JWT */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        return (List<String>) parseClaims(token).get("roles");
    }

    /** Valida assinatura e formato do token */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // aqui você pode logar o erro, e retornar false
            return false;
        }
    }

    /** Helper para parsear e retornar as Claims do corpo do JWT */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
