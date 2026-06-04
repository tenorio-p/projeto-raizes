package br.com.raizesnordeste.api.application.dto.request;

import br.com.raizesnordeste.api.domain.enums.PerfilUsuario;
import jakarta.validation.constraints.*;

public record RegistroRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
        String nome,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Formato de email inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        String senha,

        String telefone,

        PerfilUsuario perfil,

        boolean consentimentoLgpd
) {}