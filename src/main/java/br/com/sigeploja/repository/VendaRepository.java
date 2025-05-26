package br.com.sigeploja.repository;

import br.com.sigeploja.model.Venda;
import br.com.sigeploja.model.ItemVenda;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;                             // NOVO: para data_pedido
import java.util.List;
import java.util.Optional;

@Repository
public class VendaRepository {
    private final JdbcTemplate jdbc;

    public VendaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<ItemVenda> itemVendaRowMapper = (rs, rowNum) -> {
        ItemVenda item = new ItemVenda();
        item.setProdutoId(rs.getInt("produto_id"));
        item.setQuantidade(rs.getInt("quantidade"));
        item.setValorUnitario(rs.getBigDecimal("valor_unitario"));
        return item;
    };

    private final RowMapper<Venda> vendaRowMapper = (rs, rowNum) -> {
        Venda venda = new Venda();
        venda.setId(rs.getInt("id"));
        venda.setCliente(rs.getString("cliente"));
        venda.setValorTotal(rs.getBigDecimal("valor_total"));
        venda.setDataPedido(                            // NOVO: popula dataPedido
                rs.getTimestamp("data_pedido").toLocalDateTime()
        );
        venda.setProdutos(findItemsByVendaId(venda.getId()));
        return venda;
    };

    /**
     * Busca todas as vendas, do mais recente para o mais antigo.
     */
    public List<Venda> findAll() {
        String sql = "SELECT id, cliente, valor_total, data_pedido FROM venda ORDER BY id DESC";  // NOVO: inclui data_pedido
        return jdbc.query(sql, vendaRowMapper);
    }

    /**
     * Busca uma venda pelo ID, incluindo seus itens.
     */
    public Optional<Venda> findById(Integer id) {
        String sql = "SELECT id, cliente, valor_total, data_pedido FROM venda WHERE id = ?";      // NOVO
        return jdbc.query(sql, vendaRowMapper, id)
                .stream()
                .findFirst();
    }

    /**
     * Persiste uma nova venda e seus itens, e retorna a entidade com ID gerado.
     */
    public Venda save(Venda venda) {
        // 1) Insere na tabela venda, agora com data_pedido
        String sqlVenda = "INSERT INTO venda (cliente, valor_total, data_pedido) VALUES (?, ?, ?)";  // NOVO
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps =
                    con.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, venda.getCliente());
            ps.setBigDecimal(2, venda.getValorTotal());
            ps.setTimestamp(3, Timestamp.valueOf(venda.getDataPedido()));               // NOVO
            return ps;
        }, keyHolder);
        Integer id = keyHolder.getKey().intValue();
        venda.setId(id);

        // 2) Insere itens na tabela item_venda, incluindo valor_unitario
        String sqlItem = "INSERT INTO item_venda (venda_id, produto_id, quantidade, valor_unitario) VALUES (?, ?, ?, ?)";
        for (ItemVenda item : venda.getProdutos()) {
            jdbc.update(sqlItem,
                    id,
                    item.getProdutoId(),
                    item.getQuantidade(),
                    item.getValorUnitario()
            );
        }

        return venda;
    }

    /**
     * Atualiza apenas os campos 'cliente' e 'valor_total' da venda.
     */
    public void updateVenda(Integer vendaId, String cliente, java.math.BigDecimal valorTotal) {
        String sql = "UPDATE venda SET cliente = ?, valor_total = ? WHERE id = ?";
        jdbc.update(sql, cliente, valorTotal, vendaId);
    }

    /**
     * Insere um único item de venda (usado no fluxo de edição após remoção dos antigos).
     */
    public void insertItem(Integer vendaId, Integer produtoId, Integer quantidade, java.math.BigDecimal valorUnitario) {
        String sqlItem = "INSERT INTO item_venda (venda_id, produto_id, quantidade, valor_unitario) VALUES (?, ?, ?, ?)";
        jdbc.update(sqlItem, vendaId, produtoId, quantidade, valorUnitario);
    }

    /**
     * Remove todos os itens associados a uma venda.
     */
    public void deleteItensByVendaId(Integer vendaId) {
        String sql = "DELETE FROM item_venda WHERE venda_id = ?";
        jdbc.update(sql, vendaId);
    }

    /**
     * Remove todos os itens e a própria venda.
     */
    public void deleteById(Integer id) {
        deleteItensByVendaId(id);
        String sql = "DELETE FROM venda WHERE id = ?";
        jdbc.update(sql, id);
    }

    /**
     * Verifica se existe venda com o ID dado.
     */
    public boolean existsById(Integer id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM venda WHERE id = ?",
                Integer.class, id);
        return count != null && count > 0;
    }

    /**
     * Busca todos os itens de uma venda para popular o objeto Venda, incluindo preço unitário histórico.
     */
    public List<ItemVenda> findItemsByVendaId(Integer vendaId) {
        String sql = "SELECT produto_id, quantidade, valor_unitario FROM item_venda WHERE venda_id = ?";
        return jdbc.query(sql, itemVendaRowMapper, vendaId);
    }
}
