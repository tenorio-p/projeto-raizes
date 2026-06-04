package br.com.raizesnordeste.api.infrastructure.mock;

import br.com.raizesnordeste.api.domain.enums.FormaPagamento;
import br.com.raizesnordeste.api.domain.enums.StatusPagamento;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
public class PagamentoMockService {

    public ResultadoPagamento processar(
            Long pedidoId,
            BigDecimal valor,
            FormaPagamento formaPagamento,
            boolean simularFalha
    ) {
        log.info("[MOCK GATEWAY] Processando pagamento — pedido={}, valor={}, forma={}",
                pedidoId, valor, formaPagamento);

        // Pagamento em dinheiro é sempre aprovado
        if (formaPagamento == FormaPagamento.DINHEIRO) {
            return aprovado("Pagamento em dinheiro confirmado pelo atendente.");
        }

        // Simula latência de rede do gateway (50ms)
        simularLatencia();

        if (simularFalha) {
            log.warn("[MOCK GATEWAY] Pagamento RECUSADO simulado — pedido={}", pedidoId);
            return recusado("Pagamento recusado pelo emissor. Verifique os dados do cartão.");
        }

        log.info("[MOCK GATEWAY] Pagamento APROVADO — pedido={}", pedidoId);
        return aprovado("Pagamento aprovado com sucesso.");
    }

    // ---- Helpers privados ----

    private ResultadoPagamento aprovado(String mensagem) {
        String transacaoId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String payload = """
            {"status":"APPROVED","transactionId":"%s","code":"00","message":"%s"}
            """.formatted(transacaoId, mensagem).trim();

        return new ResultadoPagamento(StatusPagamento.APROVADO, transacaoId, mensagem, payload);
    }

    private ResultadoPagamento recusado(String mensagem) {
        String transacaoId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String payload = """
            {"status":"DECLINED","transactionId":"%s","code":"51","message":"%s"}
            """.formatted(transacaoId, mensagem).trim();

        return new ResultadoPagamento(StatusPagamento.RECUSADO, transacaoId, mensagem, payload);
    }

    private void simularLatencia() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public record ResultadoPagamento(
            StatusPagamento status,
            String transacaoId,
            String mensagemGateway,
            String payloadGateway
    ) {}
}