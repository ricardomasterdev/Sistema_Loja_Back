package br.com.sigeploja.service;

import br.com.sigeploja.model.Venda;

import java.math.BigDecimal;
import java.util.List;

public interface VendaService {
    List<Venda> listarTodos();
    Venda buscarPorId(Integer id);

    /**
     * Persiste uma nova venda e devolve a entidade com ID gerado.
     */
    Venda salvar(Venda venda);

    /**
     * Atualiza uma venda existente (ID na URL) usando os dados e itens fornecidos.
     */
    void atualizar(Integer id, Venda venda);

    void deletar(Integer id);

    /**
     * Retorna o valor total de vendas realizadas no dia atual.
     */
    BigDecimal getTotalVendasDoDia();

    /**
     * Retorna o valor total de vendas realizadas no mês atual.
     */
    BigDecimal getTotalVendasDoMes();
}
