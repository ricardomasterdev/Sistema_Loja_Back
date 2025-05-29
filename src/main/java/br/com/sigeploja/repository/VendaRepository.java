package br.com.sigeploja.repository;

import br.com.sigeploja.model.ItemVenda;
import br.com.sigeploja.model.Venda;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class VendaRepository {

    private final JdbcTemplate jdbc;

    public VendaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Venda> findAll() {
        String sql = "SELECT * FROM venda ORDER BY id DESC";
        return jdbc.query(sql, this::mapVenda);
    }

    public Optional<Venda> findById(Integer id) {
        String sql = "SELECT * FROM venda WHERE id = ?";
        List<Venda> list = jdbc.query(sql, this::mapVenda, id);
        return list.stream().findFirst();
    }

    public Venda save(Venda venda) {
        // ✅ Gravar data enviada no JSON (venda.getDataPedido()), e não getdate()
        String sql = "INSERT INTO venda (cliente, valor_total, data_pedido) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, venda.getCliente());
            ps.setBigDecimal(2, venda.getValorTotal());
            ps.setTimestamp(3, Timestamp.valueOf(venda.getDataPedido())); // <-- usa a data correta
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            venda.setId(key.intValue());
        }

        return venda;
    }

    // ✅ Novo método updateVenda com dataPedido
    public void updateVenda(Integer id, String cliente, BigDecimal valorTotal, LocalDateTime dataPedido) {
        String sql = "UPDATE venda SET cliente = ?, valor_total = ?, data_pedido = ? WHERE id = ?";
        jdbc.update(sql, cliente, valorTotal, Timestamp.valueOf(dataPedido), id);
    }

    public void deleteById(Integer id) {
        deleteItensByVendaId(id);
        String sql = "DELETE FROM venda WHERE id = ?";
        jdbc.update(sql, id);
    }

    public void insertItem(Integer vendaId, Integer produtoId, Integer quantidade, BigDecimal valorUnitario) {
        String sql = "INSERT INTO item_venda (venda_id, produto_id, quantidade, valor_unitario) VALUES (?, ?, ?, ?)";
        jdbc.update(sql, vendaId, produtoId, quantidade, valorUnitario);
    }

    public void deleteItensByVendaId(Integer vendaId) {
        String sql = "DELETE FROM item_venda WHERE venda_id = ?";
        jdbc.update(sql, vendaId);
    }

    public BigDecimal totalVendasDoDia() {
        String sql = "SELECT COALESCE(SUM(valor_total), 0) FROM venda WHERE CAST(data_pedido AS DATE) = CAST(GETDATE() AS DATE)";
        return jdbc.queryForObject(sql, BigDecimal.class);
    }

    public BigDecimal totalVendasDoMes() {
        String sql = "SELECT COALESCE(SUM(valor_total), 0) " +
                "FROM venda " +
                "WHERE YEAR(data_pedido) = YEAR(GETDATE()) " +
                "AND MONTH(data_pedido) = MONTH(GETDATE())";
        return jdbc.queryForObject(sql, BigDecimal.class);
    }

    // ==== Mapeamento da venda e seus itens ====

    private Venda mapVenda(ResultSet rs, int rowNum) throws SQLException {
        Venda v = new Venda();
        v.setId(rs.getInt("id"));
        v.setCliente(rs.getString("cliente"));
        v.setValorTotal(rs.getBigDecimal("valor_total"));
        v.setDataPedido(rs.getTimestamp("data_pedido").toLocalDateTime());
        v.setProdutos(buscarItensPorVenda(rs.getInt("id")));
        return v;
    }

    private List<ItemVenda> buscarItensPorVenda(Integer vendaId) {
        String sql = "SELECT * FROM item_venda WHERE venda_id = ?";
        return jdbc.query(sql, BeanPropertyRowMapper.newInstance(ItemVenda.class), vendaId);
    }
}
