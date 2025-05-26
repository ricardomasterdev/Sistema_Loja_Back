package br.com.sigeploja.service;

import br.com.sigeploja.model.Produto;
import br.com.sigeploja.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Interface definindo operações de produto
public interface ProdutoService {
    List<Produto> listarTodos();
    Produto buscarPorId(Integer id);
    void salvar(Produto produto);
    void atualizar(Produto produto);
    void deletar(Integer id);
    boolean hasVendas(Integer id);
}
