package br.com.raizesnordeste.api.infrastructure.audit;

import br.com.raizesnordeste.api.domain.entity.AuditLog;
import br.com.raizesnordeste.api.domain.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Async
    public void registrar(
            String acao,
            Long usuarioId,
            String entidade,
            Long entidadeId,
            String detalhes,
            String ipOrigem
    ) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .acao(acao)
                    .usuarioId(usuarioId)
                    .entidade(entidade)
                    .entidadeId(entidadeId)
                    .detalhes(detalhes)
                    .ipOrigem(ipOrigem)
                    .build();

            auditLogRepository.save(auditLog);
            log.info("[AUDIT] acao={} usuario={} entidade={} id={}", acao, usuarioId, entidade, entidadeId);

        } catch (Exception e) {
            log.error("[AUDIT] Erro ao registrar auditoria: {}", e.getMessage());
        }
    }

    @Async
    public void registrar(String acao, Long usuarioId, String detalhes, String ipOrigem) {
        registrar(acao, usuarioId, null, null, detalhes, ipOrigem);
    }
}