package br.com.raizesnordeste.api.api.controller;

import br.com.raizesnordeste.api.application.dto.response.ProdutoResponse;
import br.com.raizesnordeste.api.application.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Catálogo de produtos da rede")
@SecurityRequirement(name = "bearerAuth")
public class ProdutoController {

    private final ProdutoService produtoService;

    @Operation(summary = "Listar todos os produtos ativos")
    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listar() {
        return ResponseEntity.ok(produtoService.listarAtivos());
    }

    @Operation(summary = "Buscar produto por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @Operation(summary = "Criar produto", description = "Adiciona um novo produto ao catálogo. Apenas ADMIN.")
    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public org.springframework.http.ResponseEntity<ProdutoResponse> criar(
            @jakarta.validation.Valid @org.springframework.web.bind.annotation.RequestBody
            br.com.raizesnordeste.api.application.dto.request.CriarProdutoRequest request
    ) {
        return org.springframework.http.ResponseEntity
                .status(org.springframework.http.HttpStatus.CREATED)
                .body(produtoService.criar(request));
    }
}