// src/main/java/br/com/sigeploja/controller/VendaController.java
package br.com.sigeploja.controller;

import br.com.sigeploja.model.Venda;
import br.com.sigeploja.service.VendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/vendas")
@CrossOrigin(origins = "*", allowedHeaders = "*",
        methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS })
public class VendaController {

    private final VendaService service;

    public VendaController(VendaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todas as vendas")
    public ResponseEntity<List<Venda>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar venda por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Encontrada"),
                    @ApiResponse(responseCode = "404", description = "Não encontrada")
            })
    public ResponseEntity<Venda> buscar(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Criar nova venda")
    public ResponseEntity<Void> criar(@Valid @RequestBody Venda venda) {
        Venda criada = service.salvar(venda);
        return ResponseEntity
                .created(URI.create("/api/vendas/" + criada.getId()))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar venda existente",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Atualizada"),
                    @ApiResponse(responseCode = "404", description = "Não encontrada")
            })
    public ResponseEntity<Void> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody Venda venda
    ) {
        service.atualizar(id, venda);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir venda por ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Excluída"),
                    @ApiResponse(responseCode = "404", description = "Não encontrada")
            })
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }


}
