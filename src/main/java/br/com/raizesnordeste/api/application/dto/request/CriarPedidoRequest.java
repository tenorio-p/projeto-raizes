package br.com.raizesnordeste.api.application.dto.request;

import br.com.raizesnordeste.api.domain.enums.CanalPedido;
import br.com.raizesnordeste.api.domain.enums.FormaPagamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CriarPedidoRequest(

        @NotNull(message = "Canal do pedido é obrigatório (APP, TOTEM, BALCAO, PICKUP, WEB)")
        CanalPedido canalPedido,

        @NotNull(message = "Unidade é obrigatória")
        Long unidadeId,

        @NotEmpty(message = "O pedido deve conter ao menos um item")
        @Valid
        List<ItemPedidoRequest> itens,

        @NotNull(message = "Forma de pagamento é obrigatória")
        FormaPagamento formaPagamento,

        String observacao
) {
    // DTO aninhado para cada item do pedido
    public record ItemPedidoRequest(

            @NotNull(message = "Produto é obrigatório")
            Long produtoId,

            @NotNull(message = "Quantidade é obrigatória")
            @Positive(message = "Quantidade deve ser maior que zero")
            Integer quantidade
    ) {}
}