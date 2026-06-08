package br.com.raizesnordeste.api.application.service;

import br.com.raizesnordeste.api.application.dto.request.ProcessarPagamentoRequest;
import br.com.raizesnordeste.api.application.dto.response.PagamentoResponse;
import br.com.raizesnordeste.api.domain.entity.Pagamento;
import br.com.raizesnordeste.api.domain.entity.Pedido;
import br.com.raizesnordeste.api.domain.enums.StatusPagamento;
import br.com.raizesnordeste.api.domain.enums.StatusPedido;
import br.com.raizesnordeste.api.domain.exception.RecursoNaoEncontradoException;
import br.com.raizesnordeste.api.domain.exception.RegraDeNegocioException;
import br.com.raizesnordeste.api.domain.repository.PagamentoRepository;
import br.com.raizesnordeste.api.domain.repository.PedidoRepository;
import br.com.raizesnordeste.api.infrastructure.audit.AuditService;
import br.com.raizesnordeste.api.infrastructure.mock.PagamentoMockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final PedidoRepository pedidoRepository;
    private final PagamentoRepository pagamentoRepository;
    private final PagamentoMockService mockService;
    private final PedidoService pedidoService;
    private final FidelidadeService fidelidadeService;
    private final AuditService auditService;

    @Transactional
    public PagamentoResponse processar(
            Long pedidoId,
            ProcessarPagamentoRequest request,
            Long usuarioId,
            String ip
    ) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido", pedidoId));

        // Regra: só processa pedidos aguardando pagamento
        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new RegraDeNegocioException(
                    "Pedido não está aguardando pagamento. Status atual: " + pedido.getStatus()
            );
        }

        // Idempotência: verifica se já existe pagamento aprovado para este pedido
        pagamentoRepository.findByPedidoId(pedidoId).ifPresent(p -> {
            if (p.getStatusPagamento() == StatusPagamento.APROVADO) {
                throw new RegraDeNegocioException("Este pedido já possui um pagamento aprovado.");
            }
        });

        // Envia para o gateway mock e recebe o resultado
        var resultado = mockService.processar(
                pedidoId,
                pedido.getTotal(),
                pedido.getFormaPagamento(),
                request.simularFalha()
        );

        // Registra o pagamento no banco
        Pagamento pagamento = Pagamento.builder()
                .pedido(pedido)
                .formaPagamento(pedido.getFormaPagamento())
                .valor(pedido.getTotal())
                .statusPagamento(resultado.status())
                .transacaoId(resultado.transacaoId())
                .mensagemGateway(resultado.mensagemGateway())
                .payloadGateway(resultado.payloadGateway())
                .processadoEm(LocalDateTime.now())
                .build();

        pagamentoRepository.save(pagamento);

        String statusPedidoFinal;

        if (resultado.status() == StatusPagamento.APROVADO) {
            // Baixa estoque + avança para EM_PREPARO
            pedidoService.confirmarPagamento(pedidoId, usuarioId, ip);
            statusPedidoFinal = StatusPedido.EM_PREPARO.name();

            // Acumula pontos de fidelidade (se cliente tiver consentimento LGPD)
            fidelidadeService.acumularPontos(pedido.getCliente().getId(), pedido.getTotal(), ip);

            auditService.registrar("PAGAMENTO_APROVADO", usuarioId, "PAGAMENTO", pagamento.getId(),
                    "Pedido " + pedidoId + " | TXN: " + resultado.transacaoId(), ip);
        } else {
            // Pagamento recusado — pedido continua AGUARDANDO_PAGAMENTO
            statusPedidoFinal = StatusPedido.AGUARDANDO_PAGAMENTO.name();

            auditService.registrar("PAGAMENTO_RECUSADO", usuarioId, "PAGAMENTO", pagamento.getId(),
                    "Pedido " + pedidoId + " | Motivo: " + resultado.mensagemGateway(), ip);
        }

        return PagamentoResponse.from(pagamento, statusPedidoFinal);
    }

    /** Consulta o pagamento de um pedido */
    @Transactional(readOnly = true)
    public PagamentoResponse buscarPorPedido(Long pedidoId) {
        var pagamento = pagamentoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Nenhum pagamento encontrado para o pedido " + pedidoId
                ));
        return PagamentoResponse.from(pagamento, pagamento.getPedido().getStatus().name());
    }
}