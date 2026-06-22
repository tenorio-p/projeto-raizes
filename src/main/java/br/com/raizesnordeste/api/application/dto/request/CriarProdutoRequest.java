package br.com.raizesnordeste.api.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CriarProdutoRequest(

        @NotBlank(message = "Nome do produto é obrigatório")
        String nome,

        String descricao,

        @NotNull(message = "Preço é obrigatório")
        @Positive(message = "Preço deve ser maior que zero")
        BigDecimal preco,

        @NotBlank(message = "Categoria é obrigatória")
        String categoria,

        String urlImagem
) {}