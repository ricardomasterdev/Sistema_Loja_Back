package br.com.sigeploja.service;

import br.com.sigeploja.model.Produto;
import br.com.sigeploja.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository repo;

    public ProdutoServiceImpl(ProdutoRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Produto> listarTodos() {
        return repo.findAll();
    }

    @Override
    public Produto buscarPorId(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
    }

    @Override
    @Transactional
    public void salvar(Produto produto) {
        if (produto.getQuantidadeDisponivel() == null || produto.getQuantidadeDisponivel() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
        repo.save(produto);
    }

    @Override
    @Transactional
    public void atualizar(Produto produto) {
        if (!repo.existsById(produto.getId())) {
            throw new IllegalArgumentException("Produto não existe: " + produto.getId());
        }
        if (produto.getQuantidadeDisponivel() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
        repo.update(produto);
    }

    @Override
    @Transactional
    public void deletar(Integer id) {
        // Verifica vinculo de vendas antes de excluir
        if (repo.hasVendas(id)) {
            throw new IllegalStateException("Não é possível excluir: existem vendas vinculadas.");
        }
        repo.deleteById(id);
    }

    @Override
    public boolean hasVendas(Integer id) {
        return repo.hasVendas(id);
    }
}
