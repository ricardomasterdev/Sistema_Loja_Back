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

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;

    public VendaServiceImpl(VendaRepository vendaRepository, ProdutoRepository produtoRepository) {
        this.vendaRepository = vendaRepository;
        this.produtoRepository = produtoRepository;
    }

    @Override
    public List<Venda> listarTodos() {
        return vendaRepository.findAll();
    }

    @Override
    public Venda buscarPorId(Integer id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada: " + id));
    }

    @Override
    @Transactional
    public Venda salvar(Venda venda) {
        validarItens(venda);
        calcularTotalEVender(venda);
        Venda criada = vendaRepository.save(venda);
        ajustarEstoque(criada.getProdutos(), -1);
        return criada;
    }

    @Override
    @Transactional
    public void atualizar(Integer id, Venda venda) {
        Venda existente = buscarPorId(id);

        ajustarEstoque(existente.getProdutos(), +1);
        vendaRepository.deleteItensByVendaId(id);
        vendaRepository.updateVenda(id, venda.getCliente(), venda.getValorTotal());

        validarItens(venda);
        for (ItemVenda item : venda.getProdutos()) {
            vendaRepository.insertItem(id, item.getProdutoId(), item.getQuantidade(), item.getValorUnitario());
        }

        ajustarEstoque(venda.getProdutos(), -1);
    }

    @Override
    @Transactional
    public void deletar(Integer id) {
        Venda venda = buscarPorId(id);
        ajustarEstoque(venda.getProdutos(), +1);
        vendaRepository.deleteById(id);
    }

    @Override
    public BigDecimal getTotalVendasDoDia() {
        return vendaRepository.totalVendasDoDia();
    }

    @Override
    public BigDecimal getTotalVendasDoMes() {
        return vendaRepository.totalVendasDoMes();
    }

    // ===== Métodos auxiliares ===== //

    private void validarItens(Venda venda) {
        List<ItemVenda> itens = venda.getProdutos();
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("Uma venda deve possuir pelo menos um item.");
        }

        for (ItemVenda item : itens) {
            if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
                throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
            }

            var produto = produtoRepository.findById(item.getProdutoId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + item.getProdutoId()));

            if (produto.getQuantidadeDisponivel() < item.getQuantidade()) {
                throw new IllegalStateException("Estoque insuficiente para produto: " + produto.getNome());
            }
        }
    }

    private void calcularTotalEVender(Venda venda) {
        BigDecimal total = BigDecimal.ZERO;

        for (ItemVenda item : venda.getProdutos()) {
            var produto = produtoRepository.findById(item.getProdutoId()).get();
            BigDecimal valorUnitario = produto.getValorUnitario();
            BigDecimal subtotal = valorUnitario.multiply(BigDecimal.valueOf(item.getQuantidade()));

            item.setValorUnitario(valorUnitario); // Armazena o valor no item
            total = total.add(subtotal);
        }

        venda.setValorTotal(total);
    }

    private void ajustarEstoque(List<ItemVenda> itens, int fator) {
        for (ItemVenda item : itens) {
            var produto = produtoRepository.findById(item.getProdutoId()).get();
            int novaQtd = produto.getQuantidadeDisponivel() + fator * item.getQuantidade();
            produto.setQuantidadeDisponivel(novaQtd);
            produtoRepository.update(produto);
        }
    }
}
