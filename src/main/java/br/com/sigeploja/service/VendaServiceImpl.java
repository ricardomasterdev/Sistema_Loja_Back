package br.com.sigeploja.service;

import br.com.sigeploja.model.Venda;
import br.com.sigeploja.model.ItemVenda;
import br.com.sigeploja.repository.VendaRepository;
import br.com.sigeploja.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class VendaServiceImpl implements VendaService {

    private final VendaRepository vendaRepo;
    private final ProdutoRepository produtoRepo;

    public VendaServiceImpl(VendaRepository vendaRepo,
                            ProdutoRepository produtoRepo) {
        this.vendaRepo = vendaRepo;
        this.produtoRepo = produtoRepo;
    }

    @Override
    public List<Venda> listarTodos() {
        return vendaRepo.findAll();
    }

    @Override
    public Venda buscarPorId(Integer id) {
        return vendaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada: " + id));
    }

    @Override
    @Transactional
    public Venda salvar(Venda venda) {
        validarItens(venda);
        calcularTotalEVender(venda);

        // Persiste nova venda e itens, retornando a entidade com ID
        Venda criada = vendaRepo.save(venda);

        // Desconta estoque pelos itens da venda
        ajustarEstoque(criada.getProdutos(), -1);

        return criada;
    }

    @Override
    @Transactional
    public void atualizar(Integer id, Venda venda) {
        // 1) Carrega a venda existente
        Venda existente = buscarPorId(id);

        // 2) Restaura o estoque dos itens antigos
        ajustarEstoque(existente.getProdutos(), +1);

        // 3) Remove todos os itens antigos no banco
        vendaRepo.deleteItensByVendaId(id);

        // 4) Atualiza dados da venda (cliente e valor_total)
        //    Esse método deve existir em VendaRepository
        vendaRepo.updateVenda(id, venda.getCliente(), venda.getValorTotal());

        // 5) Valida e insere os novos itens
        validarItens(venda);
        for (ItemVenda item : venda.getProdutos()) {
            vendaRepo.insertItem(id, item.getProdutoId(), item.getQuantidade(),item.getValorUnitario() );
        }

        // 6) Desconta o estoque pelos novos itens
        ajustarEstoque(venda.getProdutos(), -1);
    }

    @Override
    @Transactional
    public void deletar(Integer id) {
        Venda venda = buscarPorId(id);

        // Restaura estoque dos itens da venda antes de excluir
        ajustarEstoque(venda.getProdutos(), +1);

        // Remove itens e depois a venda
        vendaRepo.deleteById(id);
    }

    // ==== Métodos auxiliares ==== //

    private void validarItens(Venda venda) {
        List<ItemVenda> itens = venda.getProdutos();
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("Uma venda deve possuir pelo menos um item.");
        }
        for (ItemVenda item : itens) {
            if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
                throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
            }
            var produto = produtoRepo.findById(item.getProdutoId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Produto não encontrado: " + item.getProdutoId()));
            if (produto.getQuantidadeDisponivel() < item.getQuantidade()) {
                throw new IllegalStateException(
                        "Estoque insuficiente para produto: " + produto.getNome());
            }
        }
    }

    private void calcularTotalEVender(Venda venda) {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemVenda item : venda.getProdutos()) {
            var produto = produtoRepo.findById(item.getProdutoId()).get();
            BigDecimal itemTotal = produto.getValorUnitario()
                    .multiply(new BigDecimal(item.getQuantidade()));
            total = total.add(itemTotal);
        }
        venda.setValorTotal(total);
    }

    /**
     * Ajusta o estoque dos produtos:
     *   fator = -1 desconta (após salvar/editar),
     *   fator = +1 restaura (antes de editar/excluir).
     */
    private void ajustarEstoque(List<ItemVenda> itens, int fator) {
        for (ItemVenda item : itens) {
            var produto = produtoRepo.findById(item.getProdutoId()).get();
            int novaQtd = produto.getQuantidadeDisponivel() + fator * item.getQuantidade();
            produto.setQuantidadeDisponivel(novaQtd);
            produtoRepo.update(produto);
        }
    }
}
