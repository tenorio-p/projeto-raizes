package br.com.raizesnordeste.api.application.dto.response;

import br.com.raizesnordeste.api.domain.entity.Fidelidade;

import java.time.LocalDateTime;

public record FidelidadeResponse(
        Long id,
        Long clienteId,
        String nomeCliente,
        Integer pontosAcumulados,
        Integer pontosResgatados,
        Integer saldoPontos,
        LocalDateTime atualizadoEm
) {
    public static FidelidadeResponse from(Fidelidade f) {
        return new FidelidadeResponse(
                f.getId(),
                f.getCliente().getId(),
                f.getCliente().getNome(),
                f.getPontosAcumulados(),
                f.getPontosResgatados(),
                f.getSaldoPontos(),
                f.getAtualizadoEm()
        );
    }
}