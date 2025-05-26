// src/main/java/br/com/sigeploja/model/ItemVenda.java
package br.com.sigeploja.model;

import java.math.BigDecimal;

public class ItemVenda {
    private Integer id;
    private Integer vendaId;
    private Integer produtoId;
    private Integer quantidade;
    private BigDecimal valorUnitario;  // ← novo campo

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVendaId() {
        return vendaId;
    }
    public void setVendaId(Integer vendaId) {
        this.vendaId = vendaId;
    }

    public Integer getProdutoId() {
        return produtoId;
    }
    public void setProdutoId(Integer produtoId) {
        this.produtoId = produtoId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }
    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }
}
