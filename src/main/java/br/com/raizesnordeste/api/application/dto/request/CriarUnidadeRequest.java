package br.com.raizesnordeste.api.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CriarUnidadeRequest(

        @NotBlank(message = "Nome da unidade é obrigatório")
        String nome,

        @NotBlank(message = "Cidade é obrigatória")
        String cidade,

        @NotBlank(message = "Estado é obrigatório")
        String estado,

        String endereco,

        String telefone
) {}