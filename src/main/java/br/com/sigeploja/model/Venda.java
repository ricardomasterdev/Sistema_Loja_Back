package br.com.sigeploja.model;

import java.math.BigDecimal;
import java.util.List;
// NOVO: import para o tipo de data
import java.time.LocalDateTime;

public class Venda {
    private Integer id;
    private String cliente;
    private BigDecimal valorTotal;
    private List<ItemVenda> produtos;

    // NOVO: campo para armazenar a data do pedido
    private LocalDateTime data_Pedido;

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

    // NOVO: getter para dataPedido
    public LocalDateTime getDataPedido() {
        return data_Pedido;
    }

    // NOVO: setter para data_Pedido
    public void setDataPedido(LocalDateTime data_Pedido) {
        this.data_Pedido = data_Pedido;
    }
}
