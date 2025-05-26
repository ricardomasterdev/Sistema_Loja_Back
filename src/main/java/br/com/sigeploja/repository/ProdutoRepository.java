package br.com.sigeploja.repository;


import br.com.sigeploja.model.Produto;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProdutoRepository {

    private final JdbcTemplate jdbc;

    public ProdutoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final BeanPropertyRowMapper<Produto> ROW_MAPPER =
            new BeanPropertyRowMapper<>(Produto.class);

    public List<Produto> findAll() {
        String sql = "SELECT id, nome, descricao, quantidade_disponivel AS quantidadeDisponivel, valor_unitario AS valorUnitario FROM produto ORDER BY id DESC";
        return jdbc.query(sql, ROW_MAPPER);
    }

    public Optional<Produto> findById(Integer id) {
        String sql = "SELECT id, nome, descricao, quantidade_disponivel AS quantidadeDisponivel, valor_unitario AS valorUnitario "
                + "FROM produto WHERE id = ?";
        return jdbc.query(sql, ROW_MAPPER, id)
                .stream()
                .findFirst();
    }

    public void save(Produto p) {
        String sql = "INSERT INTO produto (nome, descricao, quantidade_disponivel, valor_unitario) VALUES (?, ?, ?, ?)";
        jdbc.update(sql,
                p.getNome(),
                p.getDescricao(),
                p.getQuantidadeDisponivel(),
                p.getValorUnitario()
        );
    }

    public void update(Produto p) {
        String sql = "UPDATE produto SET nome = ?, descricao = ?, quantidade_disponivel = ?, valor_unitario = ? WHERE id = ?";
        jdbc.update(sql,
                p.getNome(),
                p.getDescricao(),
                p.getQuantidadeDisponivel(),
                p.getValorUnitario(),
                p.getId()
        );
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM produto WHERE id = ?";
        jdbc.update(sql, id);
    }

    public boolean existsById(Integer id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM produto WHERE id = ?",
                Integer.class, id);
        return count != null && count > 0;
    }

    public boolean hasVendas(Integer produtoId) {
        Integer cnt = jdbc.queryForObject(
                "SELECT COUNT(*) FROM item_venda WHERE produto_id = ?",
                Integer.class, produtoId);
        return cnt != null && cnt > 0;
    }
}
