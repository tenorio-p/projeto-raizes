package br.com.raizesnordeste.api.api.controller;

import br.com.raizesnordeste.api.application.dto.request.MovimentarEstoqueRequest;
import br.com.raizesnordeste.api.application.dto.response.EstoqueResponse;
import br.com.raizesnordeste.api.application.service.EstoqueService;
import br.com.raizesnordeste.api.domain.entity.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estoque")
@RequiredArgsConstructor
@Tag(name = "Estoque", description = "Controle de estoque por unidade")
@SecurityRequirement(name = "bearerAuth")
public class EstoqueController {

    private final EstoqueService estoqueService;

    @Operation(summary = "Consultar estoque de uma unidade")
    @GetMapping("/unidade/{unidadeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<EstoqueResponse>> listar(@PathVariable Long unidadeId) {
        return ResponseEntity.ok(estoqueService.listarEstoque(unidadeId));
    }

    @Operation(summary = "Itens abaixo do estoque mínimo")
    @GetMapping("/unidade/{unidadeId}/alertas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<EstoqueResponse>> alertas(@PathVariable Long unidadeId) {
        return ResponseEntity.ok(estoqueService.listarAbaixoDoMinimo(unidadeId));
    }

    @Operation(
            summary = "Movimentar estoque",
            description = "Realiza entrada ou saída manual de estoque. Requer perfil ADMIN ou GERENTE."
    )
    @PostMapping("/unidade/{unidadeId}/movimentar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<EstoqueResponse> movimentar(
            @PathVariable Long unidadeId,
            @Valid @RequestBody MovimentarEstoqueRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado,
            HttpServletRequest httpRequest
    ) {
        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(
                estoqueService.movimentar(unidadeId, request, usuarioLogado.getId(), ip)
        );
    }
}