package br.com.raizesnordeste.api.application.dto.response;

import br.com.raizesnordeste.api.domain.entity.Estoque;
import br.com.raizesnordeste.api.domain.entity.Produto;

import java.math.BigDecimal;

public record CardapioItemResponse(
        Long produtoId,
        String nome,
        String descricao,
        BigDecimal preco,
        String categoria,
        String urlImagem,
        Integer quantidadeDisponivel
) {
    public static CardapioItemResponse from(Estoque estoque) {
        Produto p = estoque.getProduto();
        return new CardapioItemResponse(
                p.getId(), p.getNome(), p.getDescricao(),
                p.getPreco(), p.getCategoria(), p.getUrlImagem(),
                estoque.getQuantidade()
        );
    }
}