package br.com.raizesnordeste.api.application.dto.response;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UsuarioResumoResponse user
) {
    public static LoginResponse of(String token, long expiresIn, UsuarioResumoResponse user) {
        return new LoginResponse(token, "Bearer", expiresIn, user);
    }
}