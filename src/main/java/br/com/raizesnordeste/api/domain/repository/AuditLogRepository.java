package br.com.raizesnordeste.api.domain.repository;

import br.com.raizesnordeste.api.domain.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId, Pageable pageable);

    Page<AuditLog> findByEntidadeAndEntidadeIdOrderByCriadoEmDesc(
            String entidade, Long entidadeId, Pageable pageable
    );
}