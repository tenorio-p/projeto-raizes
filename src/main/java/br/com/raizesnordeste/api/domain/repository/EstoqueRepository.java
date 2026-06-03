package br.com.raizesnordeste.api.domain.repository;

import br.com.raizesnordeste.api.domain.entity.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    Optional<Estoque> findByUnidadeIdAndProdutoId(Long unidadeId, Long produtoId);

    List<Estoque> findByUnidadeId(Long unidadeId);

    @Query("""
        SELECT e FROM Estoque e
        JOIN FETCH e.produto p
        WHERE e.unidade.id = :unidadeId
          AND e.quantidade > 0
          AND p.ativo = true
        ORDER BY p.categoria, p.nome
    """)
    List<Estoque> findCardapioDisponivel(@Param("unidadeId") Long unidadeId);

    @Query("""
        SELECT e FROM Estoque e
        WHERE e.unidade.id = :unidadeId
          AND e.quantidade <= e.quantidadeMinima
    """)
    List<Estoque> findAbaixoDoMinimo(@Param("unidadeId") Long unidadeId);
}