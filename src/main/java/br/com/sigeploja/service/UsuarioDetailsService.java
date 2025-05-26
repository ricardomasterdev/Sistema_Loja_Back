// src/main/java/br/com/sigeploja/service/UsuarioDetailsService.java
package br.com.sigeploja.service;

import br.com.sigeploja.model.Usuario;
import br.com.sigeploja.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepo;

    public UsuarioDetailsService(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        // Converte sua entidade Usuario para o UserDetails do Spring
        return User.withUsername(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRole())   // se seu campo já vier "ADMIN" ou "USER"
                .build();
    }
}
