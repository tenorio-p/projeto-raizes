package br.com.raizesnordeste.api.application.service;

import br.com.raizesnordeste.api.application.dto.response.FidelidadeResponse;
import br.com.raizesnordeste.api.domain.entity.Fidelidade;
import br.com.raizesnordeste.api.domain.entity.Usuario;
import br.com.raizesnordeste.api.domain.exception.RecursoNaoEncontradoException;
import br.com.raizesnordeste.api.domain.exception.RegraDeNegocioException;
import br.com.raizesnordeste.api.domain.repository.FidelidadeRepository;
import br.com.raizesnordeste.api.domain.repository.UsuarioRepository;
import br.com.raizesnordeste.api.infrastructure.audit.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FidelidadeService {

    private final FidelidadeRepository fidelidadeRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public FidelidadeResponse consultar(Long clienteId) {
        return fidelidadeRepository.findByClienteId(clienteId)
                .map(FidelidadeResponse::from)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Programa de fidelidade não encontrado para este cliente. " +
                                "É necessário consentir com o uso de dados (LGPD) no cadastro."
                ));
    }

    @Transactional
    public void acumularPontos(Long clienteId, java.math.BigDecimal valorPedido, String ip) {
        fidelidadeRepository.findByClienteId(clienteId).ifPresent(fidelidade -> {
            int pontos = valorPedido.intValue(); // 1 ponto por R$1,00
            fidelidade.acumularPontos(pontos);
            fidelidadeRepository.save(fidelidade);

            auditService.registrar("PONTOS_ACUMULADOS", clienteId, "FIDELIDADE",
                    fidelidade.getId(), "+" + pontos + " pontos | Valor: R$" + valorPedido, ip);
        });
    }


    @Transactional
    public FidelidadeResponse resgatar(Long clienteId, int pontos, String ip) {
        if (pontos < 100) {
            throw new RegraDeNegocioException(
                    "Resgate mínimo é de 100 pontos. Solicitado: " + pontos
            );
        }

        Fidelidade fidelidade = fidelidadeRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Programa de fidelidade não encontrado para este cliente."
                ));

        fidelidade.resgatarPontos(pontos); // lança IllegalStateException se saldo insuficiente
        fidelidadeRepository.save(fidelidade);

        auditService.registrar("PONTOS_RESGATADOS", clienteId, "FIDELIDADE",
                fidelidade.getId(), "-" + pontos + " pontos resgatados", ip);

        return FidelidadeResponse.from(fidelidade);
    }


    @Transactional
    public FidelidadeResponse ativar(Long clienteId, String ip) {
        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", clienteId));

        if (!cliente.isConsentimentoLgpd()) {
            throw new RegraDeNegocioException(
                    "É necessário aceitar os termos de uso de dados (LGPD) para participar."
            );
        }

        if (fidelidadeRepository.existsByClienteId(clienteId)) {
            throw new RegraDeNegocioException("Cliente já possui perfil de fidelidade ativo.");
        }

        Fidelidade fidelidade = Fidelidade.builder()
                .cliente(cliente)
                .pontosAcumulados(0)
                .pontosResgatados(0)
                .build();

        fidelidade = fidelidadeRepository.save(fidelidade);

        auditService.registrar("FIDELIDADE_ATIVADA", clienteId, "FIDELIDADE",
                fidelidade.getId(), "Perfil criado com consentimento LGPD", ip);

        return FidelidadeResponse.from(fidelidade);
    }
}