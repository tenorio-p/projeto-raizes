package br.com.raizesnordeste.api.api.controller;

import br.com.raizesnordeste.api.application.dto.request.AtualizarStatusRequest;
import br.com.raizesnordeste.api.application.dto.request.CriarPedidoRequest;
import br.com.raizesnordeste.api.application.dto.response.PedidoResponse;
import br.com.raizesnordeste.api.application.service.PedidoService;
import br.com.raizesnordeste.api.domain.entity.Usuario;
import br.com.raizesnordeste.api.domain.enums.CanalPedido;
import br.com.raizesnordeste.api.domain.enums.StatusPedido;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Criação e gerenciamento de pedidos")
@SecurityRequirement(name = "bearerAuth")
public class PedidoController {

    private final PedidoService pedidoService;

    @Operation(
            summary = "Criar pedido",
            description = "Cria um novo pedido. O campo **canalPedido** é obrigatório " +
                    "(APP, TOTEM, BALCAO, PICKUP, WEB). " +
                    "Retorna 409 se algum item estiver sem estoque."
    )
    @PostMapping
    public ResponseEntity<PedidoResponse> criar(
            @Valid @RequestBody CriarPedidoRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado,
            HttpServletRequest httpRequest
    ) {
        String ip = obterIp(httpRequest);
        PedidoResponse response = pedidoService.criar(request, usuarioLogado.getId(), ip);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Listar pedidos",
            description = "Lista pedidos com filtros opcionais. " +
                    "Exemplo: GET /pedidos?canalPedido=APP&status=AGUARDANDO_PAGAMENTO&page=0&size=10"
    )
    @GetMapping
    public ResponseEntity<Page<PedidoResponse>> listar(
            @Parameter(description = "Filtrar por canal (APP, TOTEM, BALCAO, PICKUP, WEB)")
            @RequestParam(required = false) CanalPedido canalPedido,

            @Parameter(description = "Filtrar por status do pedido")
            @RequestParam(required = false) StatusPedido status,

            @Parameter(description = "Filtrar por unidade")
            @RequestParam(required = false) Long unidadeId,

            @PageableDefault(size = 10, sort = "criadoEm") Pageable pageable
    ) {
        return ResponseEntity.ok(pedidoService.listar(canalPedido, status, unidadeId, pageable));
    }

    @Operation(summary = "Buscar pedido por ID")
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @Operation(
            summary = "Atualizar status do pedido",
            description = "Fluxo: AGUARDANDO_PAGAMENTO → EM_PREPARO → PRONTO → ENTREGUE | CANCELADO. " +
                    "Retorna 409 se a transição for inválida."
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoResponse> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarStatusRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado,
            HttpServletRequest httpRequest
    ) {
        String ip = obterIp(httpRequest);
        return ResponseEntity.ok(
                pedidoService.atualizarStatus(id, request, usuarioLogado.getId(), ip)
        );
    }

    @Operation(summary = "Meus pedidos", description = "Lista os pedidos do cliente autenticado.")
    @GetMapping("/meus")
    public ResponseEntity<Page<PedidoResponse>> meusPedidos(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(
                pedidoService.listarPorCliente(usuarioLogado.getId(), pageable)
        );
    }

    private String obterIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        return (xForwardedFor != null) ? xForwardedFor.split(",")[0].trim() : request.getRemoteAddr();
    }
}