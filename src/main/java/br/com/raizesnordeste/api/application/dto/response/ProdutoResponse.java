package br.com.raizesnordeste.api.application.dto.response;

import br.com.raizesnordeste.api.domain.entity.Produto;
import java.math.BigDecimal;

public record ProdutoResponse(
        Long id,
        String nome,
        String descricao,
        BigDecimal preco,
        String categoria,
        String urlImagem,
        boolean ativo
) {
    public static ProdutoResponse from(Produto p) {
        return new ProdutoResponse(
                p.getId(), p.getNome(), p.getDescricao(),
                p.getPreco(), p.getCategoria(), p.getUrlImagem(), p.isAtivo()
        );
    }
}