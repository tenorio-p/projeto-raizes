package br.com.raizesnordeste.api.application.dto.response;

import br.com.raizesnordeste.api.domain.entity.ItemPedido;
import br.com.raizesnordeste.api.domain.entity.Pedido;
import br.com.raizesnordeste.api.domain.enums.CanalPedido;
import br.com.raizesnordeste.api.domain.enums.FormaPagamento;
import br.com.raizesnordeste.api.domain.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long pedidoId,
        CanalPedido canalPedido,
        StatusPedido status,
        Long unidadeId,
        String nomeUnidade,
        Long clienteId,
        String nomeCliente,
        List<ItemPedidoResponse> itens,
        BigDecimal total,
        FormaPagamento formaPagamento,
        String observacao,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
    public record ItemPedidoResponse(
            Long produtoId,
            String nomeProduto,
            Integer quantidade,
            BigDecimal precoUnitario,
            BigDecimal subtotal
    ) {
        public static ItemPedidoResponse from(ItemPedido item) {
            return new ItemPedidoResponse(
                    item.getProduto().getId(),
                    item.getProduto().getNome(),
                    item.getQuantidade(),
                    item.getPrecoUnitario(),
                    item.getSubtotal()
            );
        }
    }

    public static PedidoResponse from(Pedido pedido) {
        return new PedidoResponse(
                pedido.getId(),
                pedido.getCanalPedido(),
                pedido.getStatus(),
                pedido.getUnidade().getId(),
                pedido.getUnidade().getNome(),
                pedido.getCliente().getId(),
                pedido.getCliente().getNome(),
                pedido.getItens().stream()
                        .map(ItemPedidoResponse::from)
                        .toList(),
                pedido.getTotal(),
                pedido.getFormaPagamento(),
                pedido.getObservacao(),
                pedido.getCriadoEm(),
                pedido.getAtualizadoEm()
        );
    }
}