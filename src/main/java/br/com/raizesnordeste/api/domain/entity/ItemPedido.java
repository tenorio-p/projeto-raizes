package br.com.raizesnordeste.api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    /**
     * Snapshot do preço no momento do pedido.
     * IMPORTANTE: não usar produto.getPreco() depois da criação,
     * pois o preço pode ter sido alterado.
     */
    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    /** Subtotal calculado: quantidade * precoUnitario */
    public BigDecimal getSubtotal() {
        return this.precoUnitario.multiply(BigDecimal.valueOf(this.quantidade));
    }
}