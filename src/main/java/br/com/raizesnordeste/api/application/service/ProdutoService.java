package br.com.raizesnordeste.api.application.service;

import br.com.raizesnordeste.api.application.dto.response.ProdutoResponse;
import br.com.raizesnordeste.api.domain.exception.RecursoNaoEncontradoException;
import br.com.raizesnordeste.api.domain.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listarAtivos() {
        return produtoRepository.findByAtivoTrue()
                .stream().map(ProdutoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .map(ProdutoResponse::from)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));
    }
}