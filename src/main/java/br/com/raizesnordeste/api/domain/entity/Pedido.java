package br.com.raizesnordeste.api.domain.entity;

import br.com.raizesnordeste.api.domain.enums.CanalPedido;
import br.com.raizesnordeste.api.domain.enums.FormaPagamento;
import br.com.raizesnordeste.api.domain.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "canal_pedido", nullable = false)
    private CanalPedido canalPedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusPedido status = StatusPedido.AGUARDANDO_PAGAMENTO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id", nullable = false)
    private Unidade unidade;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemPedido> itens = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false)
    private FormaPagamento formaPagamento;

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)
    private Pagamento pagamento;

    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    public void calcularTotal() {
        this.total = this.itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void iniciarPreparo() {
        if (this.status != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new IllegalStateException(
                    "Pedido deve estar AGUARDANDO_PAGAMENTO para iniciar preparo. Status atual: " + this.status
            );
        }
        this.status = StatusPedido.EM_PREPARO;
    }

    public void marcarPronto() {
        if (this.status != StatusPedido.EM_PREPARO) {
            throw new IllegalStateException(
                    "Pedido deve estar EM_PREPARO para ser marcado como PRONTO. Status atual: " + this.status
            );
        }
        this.status = StatusPedido.PRONTO;
    }

    public void marcarEntregue() {
        if (this.status != StatusPedido.PRONTO) {
            throw new IllegalStateException(
                    "Pedido deve estar PRONTO para ser marcado como ENTREGUE. Status atual: " + this.status
            );
        }
        this.status = StatusPedido.ENTREGUE;
    }

    public void cancelar() {
        if (this.status == StatusPedido.ENTREGUE) {
            throw new IllegalStateException("Não é possível cancelar um pedido já ENTREGUE.");
        }
        this.status = StatusPedido.CANCELADO;
    }

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}