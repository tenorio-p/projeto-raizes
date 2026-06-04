package br.com.raizesnordeste.api.application.dto.response;

import br.com.raizesnordeste.api.domain.entity.Unidade;

public record UnidadeResponse(
        Long id,
        String nome,
        String cidade,
        String estado,
        String endereco,
        String telefone,
        boolean ativa
) {
    public static UnidadeResponse from(Unidade u) {
        return new UnidadeResponse(
                u.getId(), u.getNome(), u.getCidade(),
                u.getEstado(), u.getEndereco(), u.getTelefone(), u.isAtiva()
        );
    }
}