package br.com.sigeploja.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Venda {
    private Integer id;
    private String cliente;
    private BigDecimal valorTotal;
    private List<ItemVenda> produtos;

    // ✅ Corrigido: nome padronizado conforme getter/setter
    private LocalDateTime dataPedido;

    public Venda() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public List<ItemVenda> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<ItemVenda> produtos) {
        this.produtos = produtos;
    }

    public LocalDateTime getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(LocalDateTime dataPedido) {
        this.dataPedido = dataPedido;
    }
}
