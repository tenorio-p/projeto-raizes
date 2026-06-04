package br.com.raizesnordeste.api.application.dto.request;

import br.com.raizesnordeste.api.domain.enums.StatusPedido;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusRequest(

        @NotNull(message = "Novo status é obrigatório")
        StatusPedido novoStatus
) {}