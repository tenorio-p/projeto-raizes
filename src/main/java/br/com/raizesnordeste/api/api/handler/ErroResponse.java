package br.com.raizesnordeste.api.api.handler;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErroResponse(
        String error,
        String message,
        List<DetalheErro> details,
        LocalDateTime timestamp,
        String path
) {
    public record DetalheErro(String field, String issue) {}

    public static ErroResponse of(String error, String message, String path) {
        return new ErroResponse(error, message, null, LocalDateTime.now(), path);
    }

    public static ErroResponse ofValidation(String message, List<DetalheErro> details, String path) {
        return new ErroResponse("ERRO_VALIDACAO", message, details, LocalDateTime.now(), path);
    }
}