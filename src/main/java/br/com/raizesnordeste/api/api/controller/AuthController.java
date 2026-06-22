package br.com.raizesnordeste.api.api.controller;

import br.com.raizesnordeste.api.application.dto.request.LoginRequest;
import br.com.raizesnordeste.api.application.dto.request.RegistroRequest;
import br.com.raizesnordeste.api.application.dto.response.LoginResponse;
import br.com.raizesnordeste.api.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login e cadastro de usuários")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Login",
            description = "Autentica o usuário com email e senha e retorna um token JWT Bearer."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        String ip = obterIpOrigem(httpRequest);
        return ResponseEntity.ok(authService.login(request, ip));
    }

    @Operation(
            summary = "Registro",
            description = "Cadastra um novo usuário. Perfil padrão é CLIENTE. " +
                    "Se consentimentoLgpd=true, cria automaticamente o perfil de fidelização."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Email já cadastrado"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos")
    })
    @PostMapping("/registro")
    public ResponseEntity<LoginResponse> registro(
            @Valid @RequestBody RegistroRequest request,
            HttpServletRequest httpRequest
    ) {
        String ip = obterIpOrigem(httpRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.registro(request, ip));
    }

    private String obterIpOrigem(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}