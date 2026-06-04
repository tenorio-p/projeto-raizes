package br.com.raizesnordeste.api.api.controller;

import br.com.raizesnordeste.api.application.dto.request.ProcessarPagamentoRequest;
import br.com.raizesnordeste.api.application.dto.response.PagamentoResponse;
import br.com.raizesnordeste.api.application.service.PagamentoService;
import br.com.raizesnordeste.api.domain.entity.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
@Tag(name = "Pagamentos", description = "Processamento e consulta de pagamentos (mock)")
@SecurityRequirement(name = "bearerAuth")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @Operation(
            summary = "Processar pagamento (mock)",
            description = "Simula o envio ao gateway externo e retorna o resultado. " +
                    "Use **simularFalha=true** para testar o cenário de recusa. " +
                    "Se aprovado, o estoque é baixado e o pedido avança para EM_PREPARO."
    )
    @PostMapping("/{pedidoId}/processar")
    public ResponseEntity<PagamentoResponse> processar(
            @PathVariable Long pedidoId,
            @RequestBody(required = false) ProcessarPagamentoRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado,
            HttpServletRequest httpRequest
    ) {
        // request é opcional, se não enviado, usa o padrão
        if (request == null) request = new ProcessarPagamentoRequest();

        String ip = obterIp(httpRequest);
        return ResponseEntity.ok(
                pagamentoService.processar(pedidoId, request, usuarioLogado.getId(), ip)
        );
    }

    @Operation(summary = "Consultar pagamento de um pedido")
    @GetMapping("/{pedidoId}")
    public ResponseEntity<PagamentoResponse> consultar(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pagamentoService.buscarPorPedido(pedidoId));
    }

    private String obterIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        return (xForwardedFor != null) ? xForwardedFor.split(",")[0].trim() : request.getRemoteAddr();
    }
}