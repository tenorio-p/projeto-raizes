package br.com.raizesnordeste.api.domain.entity;

import br.com.raizesnordeste.api.domain.enums.FormaPagamento;
import br.com.raizesnordeste.api.domain.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false)
    private FormaPagamento formaPagamento;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pagamento", nullable = false)
    @Builder.Default
    private StatusPagamento statusPagamento = StatusPagamento.PENDENTE;

    @Column(name = "transacao_id")
    private String transacaoId;

    @Column(name = "mensagem_gateway")
    private String mensagemGateway;

    @Column(name = "payload_gateway", columnDefinition = "TEXT")
    private String payloadGateway;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "processado_em")
    private LocalDateTime processadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
    }
}