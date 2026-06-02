package br.com.raizesnordeste.api.domain.exception;

public class EstoqueInsuficienteException extends RuntimeException {

    private final String nomeProduto;
    private final int qtdSolicitada;
    private final int qtdDisponivel;

    public EstoqueInsuficienteException(String nomeProduto, int qtdSolicitada, int qtdDisponivel) {
        super(String.format(
                "Estoque insuficiente para '%s'. Solicitado: %d, Disponível: %d",
                nomeProduto, qtdSolicitada, qtdDisponivel
        ));
        this.nomeProduto = nomeProduto;
        this.qtdSolicitada = qtdSolicitada;
        this.qtdDisponivel = qtdDisponivel;
    }

    public String getNomeProduto() { return nomeProduto; }
    public int getQtdSolicitada() { return qtdSolicitada; }
    public int getQtdDisponivel() { return qtdDisponivel; }
}