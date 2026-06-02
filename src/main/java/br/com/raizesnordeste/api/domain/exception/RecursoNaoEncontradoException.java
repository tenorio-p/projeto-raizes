package br.com.raizesnordeste.api.domain.exception;

public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(String.format("%s com id %d não encontrado.", recurso, id));
    }
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}