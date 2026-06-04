package br.com.raizesnordeste.api.application.dto.response;

import br.com.raizesnordeste.api.domain.entity.Estoque;
import java.time.LocalDateTime;

public record EstoqueResponse(
        Long estoqueId,
        Long unidadeId,
        String nomeUnidade,
        Long produtoId,
        String nomeProduto,
        Integer quantidade,
        Integer quantidadeMinima,
        boolean abaixoDoMinimo,
        LocalDateTime atualizadoEm
) {
    public static EstoqueResponse from(Estoque e) {
        return new EstoqueResponse(
                e.getId(),
                e.getUnidade().getId(),
                e.getUnidade().getNome(),
                e.getProduto().getId(),
                e.getProduto().getNome(),
                e.getQuantidade(),
                e.getQuantidadeMinima(),
                e.getQuantidade() <= e.getQuantidadeMinima(),
                e.getAtualizadoEm()
        );
    }
}