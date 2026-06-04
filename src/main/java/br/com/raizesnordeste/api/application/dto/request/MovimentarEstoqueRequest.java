package br.com.raizesnordeste.api.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MovimentarEstoqueRequest(

        @NotNull(message = "Produto é obrigatório")
        Long produtoId,

        @NotNull(message = "Quantidade é obrigatória")
        @Positive(message = "Quantidade deve ser positiva")
        Integer quantidade,

        @NotNull(message = "Tipo é obrigatório: ENTRADA ou SAIDA")
        TipoMovimentacao tipo,

        String motivo

) {
    public enum TipoMovimentacao { ENTRADA, SAIDA }
}