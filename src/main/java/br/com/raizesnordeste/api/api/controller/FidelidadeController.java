package br.com.raizesnordeste.api.api.controller;

import br.com.raizesnordeste.api.application.dto.response.FidelidadeResponse;
import br.com.raizesnordeste.api.application.service.FidelidadeService;
import br.com.raizesnordeste.api.domain.entity.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/fidelidade")
@RequiredArgsConstructor
@Tag(name = "Fidelidade", description = "Programa de pontos e fidelização")
@SecurityRequirement(name = "bearerAuth")
public class FidelidadeController {

    private final FidelidadeService fidelidadeService;

    @Operation(
            summary = "Meu saldo de pontos",
            description = "Retorna o saldo de pontos do cliente autenticado. " +
                    "Disponível apenas para clientes com consentimento LGPD."
    )
    @GetMapping("/meu-saldo")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<FidelidadeResponse> meuSaldo(
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(fidelidadeService.consultar(usuarioLogado.getId()));
    }

    @Operation(
            summary = "Resgatar pontos",
            description = "Converte pontos em benefício. Mínimo de 100 pontos por resgate."
    )
    @PostMapping("/resgatar/{pontos}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<FidelidadeResponse> resgatar(
            @PathVariable int pontos,
            @AuthenticationPrincipal Usuario usuarioLogado,
            HttpServletRequest httpRequest
    ) {
        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(
                fidelidadeService.resgatar(usuarioLogado.getId(), pontos, ip)
        );
    }

    @Operation(
            summary = "Ativar programa de fidelidade",
            description = "Cria o perfil de fidelidade. " +
                    "Requer consentimento LGPD ativo no cadastro do cliente."
    )
    @PostMapping("/ativar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<FidelidadeResponse> ativar(
            @AuthenticationPrincipal Usuario usuarioLogado,
            HttpServletRequest httpRequest
    ) {
        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(fidelidadeService.ativar(usuarioLogado.getId(), ip));
    }

    @Operation(
            summary = "Consultar pontos de um cliente (Admin/Gerente)",
            description = "Permite que ADMIN ou GERENTE consulte o saldo de qualquer cliente."
    )
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<FidelidadeResponse> consultarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(fidelidadeService.consultar(clienteId));
    }
}