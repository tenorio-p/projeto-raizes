package br.com.raizesnordeste.api.api.config;

import br.com.raizesnordeste.api.infrastructure.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.raizesnordeste.api.infrastructure.security.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // Política stateless — nenhuma sessão HTTP é criada
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Regras de autorização por rota
                .authorizeHttpRequests(auth -> auth

                        // ---- ROTAS PÚBLICAS ----
                        .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/registro").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/actuator/health").permitAll()

                        // ---- UNIDADES ----
                        // Qualquer autenticado pode listar unidades e ver cardápio
                        .requestMatchers(HttpMethod.GET, "/unidades/**").authenticated()
                        // Apenas ADMIN cria/edita unidades
                        .requestMatchers(HttpMethod.POST, "/unidades/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/unidades/**").hasRole("ADMIN")

                        // ---- PRODUTOS ----
                        .requestMatchers(HttpMethod.GET, "/produtos/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/produtos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/produtos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/produtos/**").hasRole("ADMIN")

                        // ---- ESTOQUE ----
                        // GERENTE e ADMIN podem movimentar estoque
                        .requestMatchers("/estoque/**").hasAnyRole("ADMIN", "GERENTE")

                        // ---- PEDIDOS ----
                        // Qualquer autenticado pode criar pedidos e listar os seus
                        .requestMatchers(HttpMethod.POST, "/pedidos").authenticated()
                        .requestMatchers(HttpMethod.GET, "/pedidos/**").authenticated()
                        // Atualizar status: COZINHA, ATENDENTE, GERENTE ou ADMIN
                        .requestMatchers(HttpMethod.PATCH, "/pedidos/**").hasAnyRole("ADMIN", "GERENTE", "ATENDENTE", "COZINHA")

                        // ---- PAGAMENTOS ----
                        .requestMatchers("/pagamentos/**").authenticated()

                        // ---- FIDELIDADE ----
                        .requestMatchers("/fidelidade/**").authenticated()

                        // ---- ADMIN ----
                        .requestMatchers("/usuarios/**").hasRole("ADMIN")

                        // Qualquer outra rota exige autenticação
                        .anyRequest().authenticated()
                )

                // Registra o filtro JWT ANTES do filtro padrão de autenticação
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // Configura o provedor de autenticação customizado
                .authenticationProvider(authenticationProvider());

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider((UserDetailsService) passwordEncoder());
        provider.setUserDetailsPasswordService((UserDetailsPasswordService) userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}