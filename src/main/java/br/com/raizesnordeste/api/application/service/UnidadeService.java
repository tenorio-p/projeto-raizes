package br.com.raizesnordeste.api.application.service;

import br.com.raizesnordeste.api.application.dto.response.UnidadeResponse;
import br.com.raizesnordeste.api.domain.exception.RecursoNaoEncontradoException;
import br.com.raizesnordeste.api.domain.repository.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnidadeService {

    private final UnidadeRepository unidadeRepository;

    @Transactional(readOnly = true)
    public List<UnidadeResponse> listarAtivas() {
        return unidadeRepository.findByAtivaTrue()
                .stream().map(UnidadeResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public UnidadeResponse buscarPorId(Long id) {
        return unidadeRepository.findById(id)
                .map(UnidadeResponse::from)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade", id));
    }


    @org.springframework.transaction.annotation.Transactional
    public UnidadeResponse criar(br.com.raizesnordeste.api.application.dto.request.CriarUnidadeRequest request) {
        var unidade = br.com.raizesnordeste.api.domain.entity.Unidade.builder()
                .nome(request.nome())
                .cidade(request.cidade())
                .estado(request.estado())
                .endereco(request.endereco())
                .telefone(request.telefone())
                .ativa(true)
                .build();

        unidade = unidadeRepository.save(unidade);
        return UnidadeResponse.from(unidade);
    }
}