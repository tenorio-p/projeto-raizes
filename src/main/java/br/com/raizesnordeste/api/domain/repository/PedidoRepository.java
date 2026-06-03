package br.com.raizesnordeste.api.domain.repository;

import br.com.raizesnordeste.api.domain.entity.Pedido;
import br.com.raizesnordeste.api.domain.enums.CanalPedido;
import br.com.raizesnordeste.api.domain.enums.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("""
        SELECT p FROM Pedido p
        WHERE (:canalPedido IS NULL OR p.canalPedido = :canalPedido)
          AND (:status IS NULL OR p.status = :status)
          AND (:unidadeId IS NULL OR p.unidade.id = :unidadeId)
        ORDER BY p.criadoEm DESC
    """)
    Page<Pedido> buscarComFiltros(
            @Param("canalPedido") CanalPedido canalPedido,
            @Param("status") StatusPedido status,
            @Param("unidadeId") Long unidadeId,
            Pageable pageable
    );

    Page<Pedido> findByClienteIdOrderByCriadoEmDesc(Long clienteId, Pageable pageable);

    @Query("""
        SELECT DISTINCT p FROM Pedido p
        LEFT JOIN FETCH p.itens i
        LEFT JOIN FETCH i.produto
        WHERE p.id = :id
    """)
    Optional<Pedido> findByIdComItens(@Param("id") Long id);
}