package br.com.raizesnordeste.api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "fidelidade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fidelidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false, unique = true)
    private Usuario cliente;

    @Column(name = "pontos_acumulados", nullable = false)
    @Builder.Default
    private Integer pontosAcumulados = 0;

    @Column(name = "pontos_resgatados", nullable = false)
    @Builder.Default
    private Integer pontosResgatados = 0;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    public int getSaldoPontos() {
        return pontosAcumulados - pontosResgatados;
    }

    public void acumularPontos(int pontos) {
        this.pontosAcumulados += pontos;
        this.atualizadoEm = LocalDateTime.now();
    }

    public void resgatarPontos(int pontos) {
        if (pontos > getSaldoPontos()) {
            throw new IllegalStateException("Saldo de pontos insuficiente.");
        }
        this.pontosResgatados += pontos;
        this.atualizadoEm = LocalDateTime.now();
    }
}