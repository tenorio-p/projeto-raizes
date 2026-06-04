package br.com.raizesnordeste.api.application.service;

import br.com.raizesnordeste.api.application.dto.request.MovimentarEstoqueRequest;
import br.com.raizesnordeste.api.application.dto.response.CardapioItemResponse;
import br.com.raizesnordeste.api.application.dto.response.EstoqueResponse;
import br.com.raizesnordeste.api.domain.entity.Estoque;
import br.com.raizesnordeste.api.domain.entity.Produto;
import br.com.raizesnordeste.api.domain.entity.Unidade;
import br.com.raizesnordeste.api.domain.exception.RecursoNaoEncontradoException;
import br.com.raizesnordeste.api.domain.repository.EstoqueRepository;
import br.com.raizesnordeste.api.domain.repository.ProdutoRepository;
import br.com.raizesnordeste.api.domain.repository.UnidadeRepository;
import br.com.raizesnordeste.api.infrastructure.audit.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final UnidadeRepository unidadeRepository;
    private final ProdutoRepository produtoRepository;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public List<CardapioItemResponse> listarCardapio(Long unidadeId) {
        validarUnidade(unidadeId);
        return estoqueRepository.findCardapioDisponivel(unidadeId)
                .stream()
                .map(CardapioItemResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EstoqueResponse> listarEstoque(Long unidadeId) {
        validarUnidade(unidadeId);
        return estoqueRepository.findByUnidadeId(unidadeId)
                .stream()
                .map(EstoqueResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EstoqueResponse> listarAbaixoDoMinimo(Long unidadeId) {
        validarUnidade(unidadeId);
        return estoqueRepository.findAbaixoDoMinimo(unidadeId)
                .stream()
                .map(EstoqueResponse::from)
                .toList();
    }

    @Transactional
    public EstoqueResponse movimentar(
            Long unidadeId,
            MovimentarEstoqueRequest request,
            Long usuarioId,
            String ip
    ) {
        Unidade unidade = validarUnidade(unidadeId);
        Produto produto = produtoRepository.findById(request.produtoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", request.produtoId()));

        Estoque estoque = estoqueRepository
                .findByUnidadeIdAndProdutoId(unidadeId, request.produtoId())
                .orElseGet(() -> Estoque.builder()
                        .unidade(unidade)
                        .produto(produto)
                        .quantidade(0)
                        .build());

        String acao;
        if (request.tipo() == MovimentarEstoqueRequest.TipoMovimentacao.ENTRADA) {
            estoque.repor(request.quantidade());
            acao = "ESTOQUE_ENTRADA";
        } else {
            estoque.baixar(request.quantidade()); // lança EstoqueInsuficienteException se necessário
            acao = "ESTOQUE_SAIDA";
        }

        estoque = estoqueRepository.save(estoque);

        auditService.registrar(acao, usuarioId, "ESTOQUE", estoque.getId(),
                "Produto: " + produto.getNome() + " | Qtd: " + request.quantidade()
                        + " | Motivo: " + request.motivo(), ip);

        return EstoqueResponse.from(estoque);
    }

    @Transactional
    public void baixarEstoqueParaPedido(Long unidadeId, List<br.com.raizesnordeste.api.application.dto.request.CriarPedidoRequest.ItemPedidoRequest> itens) {
        for (var item : itens) {
            Estoque estoque = estoqueRepository
                    .findByUnidadeIdAndProdutoId(unidadeId, item.produtoId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Produto " + item.produtoId() + " não disponível nesta unidade."
                    ));
            estoque.baixar(item.quantidade()); // lança EstoqueInsuficienteException se insuficiente
            estoqueRepository.save(estoque);
        }
    }

    private Unidade validarUnidade(Long unidadeId) {
        return unidadeRepository.findById(unidadeId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade", unidadeId));
    }
}