// src/main/java/br/com/sigeploja/controller/ProdutoController.java
package br.com.sigeploja.controller;

import br.com.sigeploja.model.Produto;
import br.com.sigeploja.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todos os produtos")
    public List<Produto> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Encontrado"),
                    @ApiResponse(responseCode = "404", description = "Não encontrado")
            })
    public ResponseEntity<Produto> buscar(@PathVariable Integer id) {
        Produto p = service.buscarPorId(id);
        return ResponseEntity.ok(p);
    }

    @PostMapping
    @Operation(summary = "Criar novo produto")
    public ResponseEntity<Void> criar(@Valid @RequestBody Produto produto) {
        service.salvar(produto);
        return ResponseEntity
                .created(URI.create("/api/produtos/" + produto.getId()))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produto existente")
    public ResponseEntity<Void> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody Produto produto) {
        produto.setId(id);
        service.atualizar(produto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir produto por ID")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        try {
            service.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException ex) {
            // 409 Conflict indica que o recurso não pode ser removido por causa do estado atual
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/{id}/hasVendas")
    @Operation(summary = "Verifica se produto tem vendas associadas")
    public ResponseEntity<Boolean> hasVendas(@PathVariable Integer id) {
        return ResponseEntity.ok(service.hasVendas(id));
    }
}

