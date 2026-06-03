package br.com.raizesnordeste.api.domain.repository;

import br.com.raizesnordeste.api.domain.entity.Fidelidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FidelidadeRepository extends JpaRepository<Fidelidade, Long> {

    Optional<Fidelidade> findByClienteId(Long clienteId);

    boolean existsByClienteId(Long clienteId);
}