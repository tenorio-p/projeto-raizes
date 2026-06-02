package br.com.raizesnordeste.api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String acao;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "entidade")
    private String entidade;

    @Column(name = "entidade_id")
    private Long entidadeId;

    @Column(columnDefinition = "TEXT")
    private String detalhes;

    @Column(name = "ip_origem")
    private String ipOrigem;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
    }
}