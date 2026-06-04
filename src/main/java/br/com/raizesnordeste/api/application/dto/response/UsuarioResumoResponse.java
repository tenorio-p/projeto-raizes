package br.com.raizesnordeste.api.application.dto.response;

import br.com.raizesnordeste.api.domain.entity.Usuario;
import br.com.raizesnordeste.api.domain.enums.PerfilUsuario;

public record UsuarioResumoResponse(
        Long id,
        String nome,
        String email,
        PerfilUsuario perfil,
        boolean consentimentoLgpd
) {
    public static UsuarioResumoResponse from(Usuario usuario) {
        return new UsuarioResumoResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil(),
                usuario.isConsentimentoLgpd()
        );
    }
}