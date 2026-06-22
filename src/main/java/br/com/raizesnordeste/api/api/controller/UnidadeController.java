package br.com.raizesnordeste.api.api.controller;

import br.com.raizesnordeste.api.application.dto.response.CardapioItemResponse;
import br.com.raizesnordeste.api.application.dto.response.UnidadeResponse;
import br.com.raizesnordeste.api.application.service.EstoqueService;
import br.com.raizesnordeste.api.application.service.UnidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/unidades")
@RequiredArgsConstructor
@Tag(name = "Unidades", description = "Unidades da rede e cardápio por unidade")
@SecurityRequirement(name = "bearerAuth")
public class UnidadeController {

    private final UnidadeService unidadeService;
    private final EstoqueService estoqueService;

    @Operation(summary = "Listar unidades ativas")
    @GetMapping
    public ResponseEntity<List<UnidadeResponse>> listar() {
        return ResponseEntity.ok(unidadeService.listarAtivas());
    }

    @Operation(summary = "Buscar unidade por ID")
    @GetMapping("/{id}")
    public ResponseEntity<UnidadeResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(unidadeService.buscarPorId(id));
    }

    @Operation(
            summary = "Cardápio disponível da unidade",
            description = "Retorna apenas os produtos com estoque > 0 nesta unidade."
    )
    @GetMapping("/{id}/cardapio")
    public ResponseEntity<List<CardapioItemResponse>> cardapio(@PathVariable Long id) {
        return ResponseEntity.ok(estoqueService.listarCardapio(id));
    }

    @Operation(summary = "Criar unidade", description = "Cria uma nova unidade da rede. Apenas ADMIN.")
    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public org.springframework.http.ResponseEntity<UnidadeResponse> criar(
            @jakarta.validation.Valid @org.springframework.web.bind.annotation.RequestBody
            br.com.raizesnordeste.api.application.dto.request.CriarUnidadeRequest request
    ) {
        return org.springframework.http.ResponseEntity
                .status(org.springframework.http.HttpStatus.CREATED)
                .body(unidadeService.criar(request));
    }
}