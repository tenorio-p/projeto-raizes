package br.com.raizesnordeste.api.application.dto.response;

import br.com.raizesnordeste.api.domain.entity.Pagamento;
import br.com.raizesnordeste.api.domain.enums.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagamentoResponse(
        Long pagamentoId,
        Long pedidoId,
        StatusPagamento statusPagamento,
        String transacaoId,
        String mensagemGateway,
        BigDecimal valor,
        LocalDateTime processadoEm,
        String statusPedidoAtualizado
) {
    public static PagamentoResponse from(Pagamento pagamento, String statusPedido) {
        return new PagamentoResponse(
                pagamento.getId(),
                pagamento.getPedido().getId(),
                pagamento.getStatusPagamento(),
                pagamento.getTransacaoId(),
                pagamento.getMensagemGateway(),
                pagamento.getValor(),
                pagamento.getProcessadoEm(),
                statusPedido
        );
    }
}