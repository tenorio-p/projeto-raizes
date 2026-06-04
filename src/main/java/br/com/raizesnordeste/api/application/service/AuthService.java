package br.com.raizesnordeste.api.application.service;

import br.com.raizesnordeste.api.application.dto.request.LoginRequest;
import br.com.raizesnordeste.api.application.dto.request.RegistroRequest;
import br.com.raizesnordeste.api.application.dto.response.LoginResponse;
import br.com.raizesnordeste.api.application.dto.response.UsuarioResumoResponse;
import br.com.raizesnordeste.api.domain.entity.Fidelidade;
import br.com.raizesnordeste.api.domain.entity.Usuario;
import br.com.raizesnordeste.api.domain.enums.PerfilUsuario;
import br.com.raizesnordeste.api.domain.exception.RegraDeNegocioException;
import br.com.raizesnordeste.api.domain.repository.FidelidadeRepository;
import br.com.raizesnordeste.api.domain.repository.UsuarioRepository;
import br.com.raizesnordeste.api.infrastructure.audit.AuditService;
import br.com.raizesnordeste.api.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final FidelidadeRepository fidelidadeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuditService auditService;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public LoginResponse login(LoginRequest request, String ipOrigem) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.senha())
            );
        } catch (BadCredentialsException e) {
            auditService.registrar(
                    "LOGIN_FALHO",
                    null,
                    "Tentativa com email: " + request.email(),
                    ipOrigem
            );
            throw new RegraDeNegocioException("Email ou senha inválidos.");
        }

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado."));

        String token = jwtService.gerarToken(usuario);

        auditService.registrar("LOGIN_SUCESSO", usuario.getId(), "USUARIO", usuario.getId(),
                "Login via " + ipOrigem, ipOrigem);

        return LoginResponse.of(token, jwtExpiration, UsuarioResumoResponse.from(usuario));
    }

    @Transactional
    public LoginResponse registro(RegistroRequest request, String ipOrigem) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new RegraDeNegocioException(
                    "Já existe um usuário cadastrado com o email: " + request.email()
            );
        }

        PerfilUsuario perfil = request.perfil() != null ? request.perfil() : PerfilUsuario.CLIENTE;

        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(passwordEncoder.encode(request.senha())) // NUNCA salva senha em texto puro
                .telefone(request.telefone())
                .perfil(perfil)
                .consentimentoLgpd(request.consentimentoLgpd())
                .dataConsentimento(request.consentimentoLgpd() ? LocalDateTime.now() : null)
                .ativo(true)
                .build();

        usuario = usuarioRepository.save(usuario);

        if (request.consentimentoLgpd()) {
            Fidelidade fidelidade = Fidelidade.builder()
                    .cliente(usuario)
                    .pontosAcumulados(0)
                    .pontosResgatados(0)
                    .build();
            fidelidadeRepository.save(fidelidade);
        }

        auditService.registrar("USUARIO_CRIADO", usuario.getId(), "USUARIO", usuario.getId(),
                "Perfil: " + perfil + ", LGPD: " + request.consentimentoLgpd(), ipOrigem);

        String token = jwtService.gerarToken(usuario);
        return LoginResponse.of(token, jwtExpiration, UsuarioResumoResponse.from(usuario));
    }
}