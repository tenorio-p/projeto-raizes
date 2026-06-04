package br.com.raizesnordeste.api.application.service;

import br.com.raizesnordeste.api.application.dto.request.AtualizarStatusRequest;
import br.com.raizesnordeste.api.application.dto.request.CriarPedidoRequest;
import br.com.raizesnordeste.api.application.dto.response.PedidoResponse;
import br.com.raizesnordeste.api.domain.entity.ItemPedido;
import br.com.raizesnordeste.api.domain.entity.Pedido;
import br.com.raizesnordeste.api.domain.entity.Produto;
import br.com.raizesnordeste.api.domain.enums.CanalPedido;
import br.com.raizesnordeste.api.domain.enums.StatusPedido;
import br.com.raizesnordeste.api.domain.exception.RecursoNaoEncontradoException;
import br.com.raizesnordeste.api.domain.exception.RegraDeNegocioException;
import br.com.raizesnordeste.api.domain.repository.EstoqueRepository;
import br.com.raizesnordeste.api.domain.repository.PedidoRepository;
import br.com.raizesnordeste.api.domain.repository.ProdutoRepository;
import br.com.raizesnordeste.api.domain.repository.UnidadeRepository;
import br.com.raizesnordeste.api.domain.repository.UsuarioRepository;
import br.com.raizesnordeste.api.infrastructure.audit.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UnidadeRepository unidadeRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;
    private final AuditService auditService;

    @Transactional
    public PedidoResponse criar(CriarPedidoRequest request, Long clienteId, String ip) {

        var unidade = unidadeRepository.findById(request.unidadeId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade", request.unidadeId()));

        if (!unidade.isAtiva()) {
            throw new RegraDeNegocioException("A unidade selecionada não está ativa.");
        }

        var cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", clienteId));

        // Constrói o pedido sem itens ainda
        var pedido = Pedido.builder()
                .canalPedido(request.canalPedido())
                .cliente(cliente)
                .unidade(unidade)
                .formaPagamento(request.formaPagamento())
                .observacao(request.observacao())
                .build();

        // Valida e adiciona cada item
        for (var itemReq : request.itens()) {
            Produto produto = produtoRepository.findById(itemReq.produtoId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", itemReq.produtoId()));

            if (!produto.isAtivo()) {
                throw new RegraDeNegocioException(
                        "Produto '" + produto.getNome() + "' não está disponível."
                );
            }

            // Verifica disponibilidade em estoque (sem baixar ainda)
            var estoque = estoqueRepository
                    .findByUnidadeIdAndProdutoId(request.unidadeId(), itemReq.produtoId())
                    .orElseThrow(() -> new RegraDeNegocioException(
                            "Produto '" + produto.getNome() + "' não disponível nesta unidade."
                    ));

            if (!estoque.estaDisponivel(itemReq.quantidade())) {
                throw new br.com.raizesnordeste.api.domain.exception.EstoqueInsuficienteException(
                        produto.getNome(), itemReq.quantidade(), estoque.getQuantidade()
                );
            }

            var item = ItemPedido.builder()
                    .pedido(pedido)
                    .produto(produto)
                    .quantidade(itemReq.quantidade())
                    .precoUnitario(produto.getPreco()) // snapshot do preço atual
                    .build();

            pedido.getItens().add(item);
        }

        pedido.calcularTotal();
        var pedidoSalvo = pedidoRepository.save(pedido);

        auditService.registrar("PEDIDO_CRIADO", clienteId, "PEDIDO", pedidoSalvo.getId(),
                "Canal: " + request.canalPedido() + " | Total: R$" + pedidoSalvo.getTotal(), ip);

        return PedidoResponse.from(pedidoSalvo);
    }

    @Transactional
    public PedidoResponse atualizarStatus(
            Long pedidoId,
            AtualizarStatusRequest request,
            Long usuarioId,
            String ip
    ) {
        var pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido", pedidoId));

        StatusPedido statusAnterior = pedido.getStatus();

        // Delega a validação da transição para a entidade
        switch (request.novoStatus()) {
            case EM_PREPARO -> pedido.iniciarPreparo();
            case PRONTO     -> pedido.marcarPronto();
            case ENTREGUE   -> pedido.marcarEntregue();
            case CANCELADO  -> pedido.cancelar();
            default -> throw new RegraDeNegocioException(
                    "Transição de status não permitida manualmente: " + request.novoStatus()
            );
        }

        var pedidoAtualizado = pedidoRepository.save(pedido);

        auditService.registrar("STATUS_ATUALIZADO", usuarioId, "PEDIDO", pedidoId,
                statusAnterior + " → " + request.novoStatus(), ip);

        return PedidoResponse.from(pedidoAtualizado);
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long pedidoId) {
        var pedido = pedidoRepository.findByIdComItens(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido", pedidoId));
        return PedidoResponse.from(pedido);
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> listar(
            CanalPedido canalPedido,
            StatusPedido status,
            Long unidadeId,
            Pageable pageable
    ) {
        return pedidoRepository
                .buscarComFiltros(canalPedido, status, unidadeId, pageable)
                .map(PedidoResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> listarPorCliente(Long clienteId, Pageable pageable) {
        return pedidoRepository
                .findByClienteIdOrderByCriadoEmDesc(clienteId, pageable)
                .map(PedidoResponse::from);
    }


    @Transactional
    public void confirmarPagamento(Long pedidoId, Long usuarioId, String ip) {
        var pedido = pedidoRepository.findByIdComItens(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido", pedidoId));

        // Baixa o estoque de cada item
        for (var item : pedido.getItens()) {
            var estoque = estoqueRepository
                    .findByUnidadeIdAndProdutoId(pedido.getUnidade().getId(), item.getProduto().getId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Estoque não encontrado para o produto: " + item.getProduto().getNome()
                    ));
            estoque.baixar(item.getQuantidade());
            estoqueRepository.save(estoque);
        }

        // Avança o status para EM_PREPARO
        pedido.iniciarPreparo();
        pedidoRepository.save(pedido);

        auditService.registrar("PAGAMENTO_CONFIRMADO", usuarioId, "PEDIDO", pedidoId,
                "Estoque baixado e pedido em preparo.", ip);
    }
}