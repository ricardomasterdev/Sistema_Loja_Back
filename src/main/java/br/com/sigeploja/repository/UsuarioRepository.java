package br.com.sigeploja.repository;

import br.com.sigeploja.model.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepository {
    private final JdbcTemplate jdbc;

    public UsuarioRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }


    private final RowMapper<Usuario> usuarioRowMapper = (rs, rowNum) -> {
        Usuario u = new Usuario();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        return u;
    };

    /**
     * Busca todos os usuários.
     */
    public List<Usuario> findAll() {
        String sql = "SELECT id, username, password, role FROM usuario ORDER BY id";
        return jdbc.query(sql, usuarioRowMapper);
    }

    /**
     * Busca usuário por ID.
     */
    public Optional<Usuario> findById(Long id) {
        String sql = "SELECT id, username, password, role FROM usuario WHERE id = ?";
        return jdbc.query(sql, usuarioRowMapper, id).stream().findFirst();
    }

    /**
     * Busca usuário por username.
     */
    public Optional<Usuario> findByUsername(String username) {
        String sql = "SELECT id, username, password, role FROM usuario WHERE username = ?";
        return jdbc.query(sql, usuarioRowMapper, username).stream().findFirst();
    }

    /**
     * Persiste um novo usuário e retorna com ID gerado.
     */
    public Usuario save(Usuario user) {
        String sql = "INSERT INTO usuario (username, password, role) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    /**
     * Atualiza usuário existente.
     */
    public void update(Usuario user) {
        String sql = "UPDATE usuario SET username = ?, password = ?, role = ? WHERE id = ?";
        jdbc.update(sql,
                user.getUsername(),
                user.getPassword(),
                user.getRole(),
                user.getId()
        );
    }

    /**
     * Remove usuário por ID.
     */
    public void deleteById(Long id) {
        String sql = "DELETE FROM usuario WHERE id = ?";
        jdbc.update(sql, id);
    }

    /**
     * Verifica existência por username.
     */
    public boolean existsByUsername(String username) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM usuario WHERE username = ?",
                Integer.class, username);
        return count != null && count > 0;
    }

    /**
     * Verifica existência por ID.
     */
    public boolean existsById(Long id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM usuario WHERE id = ?",
                Integer.class, id);
        return count != null && count > 0;
    }
}
