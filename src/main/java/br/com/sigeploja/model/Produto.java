package br.com.sigeploja.model;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Dados de um produto")
public class Produto {
    private Integer id;

    @NotBlank
    @Size(max = 100)
    private String nome;

    @Size(max = 255)
    private String descricao;

    @NotNull
    @Positive
    private Integer quantidadeDisponivel;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal valorUnitario;

    public Produto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(Integer quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }
}
