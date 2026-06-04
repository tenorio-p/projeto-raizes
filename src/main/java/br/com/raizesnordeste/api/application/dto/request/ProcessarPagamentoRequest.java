package br.com.raizesnordeste.api.application.dto.request;

public record ProcessarPagamentoRequest(
        boolean simularFalha
) {
    public ProcessarPagamentoRequest() {
        this(false);
    }
}