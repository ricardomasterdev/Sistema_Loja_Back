package br.com.sigeploja.service;

import br.com.sigeploja.model.Usuario;

import java.util.List;

public interface UsuarioService {
    List<Usuario> listarTodos();
    Usuario buscarPorId(Long id);
    Usuario buscarPorUsername(String username);
    Usuario criar(Usuario usuario);
    void atualizar(Usuario usuario);
    void deletar(Long id);
    boolean existsByUsername(String username);
    boolean existsById(Long id);
}