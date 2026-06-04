package br.com.raizesnordeste.api.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Raízes do Nordeste — API Back-End")
                        .description("""
                    API REST do sistema de pedidos da rede de lanchonetes nordestinas.
                    
                    **Autenticação:** Use POST /auth/login para obter o token JWT.
                    Clique em "Authorize" e informe: Bearer {seu_token}
                    
                    **Usuários de teste (senha: Senha@123):**
                    - admin@raizes.com (ADMIN)
                    - maria@email.com (CLIENTE)
                    - joao@raizes.com (ATENDENTE)
                    - cozinha@raizes.com (COZINHA)
                    - ana@raizes.com (GERENTE)
                    """)
                        .version("1.0.0")
                )
                // Registra o esquema de segurança JWT
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Informe o token JWT obtido no endpoint /auth/login")
                        )
                )
                // Aplica o esquema de segurança globalmente
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}