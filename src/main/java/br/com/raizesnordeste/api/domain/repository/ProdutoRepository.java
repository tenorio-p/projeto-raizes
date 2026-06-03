package br.com.raizesnordeste.api.domain.repository;

import br.com.raizesnordeste.api.domain.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByAtivoTrue();

    List<Produto> findByCategoriaAndAtivoTrue(String categoria);
}