package br.com.raizesnordeste.api.domain.entity;

import br.com.raizesnordeste.api.domain.exception.EstoqueInsuficienteException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "estoques",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_estoque_unidade_produto",
                columnNames = {"unidade_id", "produto_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id", nullable = false)
    private Unidade unidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantidade = 0;

    /** Quantidade mínima para alerta de reposição */
    @Column(name = "quantidade_minima", nullable = false)
    @Builder.Default
    private Integer quantidadeMinima = 5;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    public void baixar(int qtd) {
        if (this.quantidade < qtd) {
            throw new EstoqueInsuficienteException(
                    this.produto.getNome(),
                    qtd,
                    this.quantidade
            );
        }
        this.quantidade -= qtd;
        this.atualizadoEm = LocalDateTime.now();
    }

    public void repor(int qtd) {
        if (qtd <= 0) {
            throw new IllegalArgumentException("Quantidade de reposição deve ser positiva.");
        }
        this.quantidade += qtd;
        this.atualizadoEm = LocalDateTime.now();
    }
    public boolean estaDisponivel(int qtdSolicitada) {
        return this.quantidade >= qtdSolicitada;
    }

    @PrePersist
    protected void onCreate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}