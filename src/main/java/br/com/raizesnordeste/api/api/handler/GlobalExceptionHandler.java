package br.com.raizesnordeste.api.api.handler;

import br.com.raizesnordeste.api.domain.exception.EstoqueInsuficienteException;
import br.com.raizesnordeste.api.domain.exception.RecursoNaoEncontradoException;
import br.com.raizesnordeste.api.domain.exception.RegraDeNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---- 404 Not Found ----

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handleNaoEncontrado(
            RecursoNaoEncontradoException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErroResponse.of("RECURSO_NAO_ENCONTRADO", ex.getMessage(), request.getRequestURI()));
    }

    // ---- 409 Conflict (regras de negócio) ----

    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ResponseEntity<ErroResponse> handleEstoqueInsuficiente(
            EstoqueInsuficienteException ex,
            HttpServletRequest request
    ) {
        ErroResponse erro = new ErroResponse(
                "ESTOQUE_INSUFICIENTE",
                ex.getMessage(),
                List.of(new ErroResponse.DetalheErro(
                        "quantidade",
                        "Disponível: " + ex.getQtdDisponivel() + ", Solicitado: " + ex.getQtdSolicitada()
                )),
                java.time.LocalDateTime.now(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroResponse> handleRegraDeNegocio(
            RegraDeNegocioException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErroResponse.of("REGRA_DE_NEGOCIO", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErroResponse> handleIllegalState(
            IllegalStateException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErroResponse.of("TRANSICAO_INVALIDA", ex.getMessage(), request.getRequestURI()));
    }

    // ---- 422 Unprocessable Entity (validação de campos) ----

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidacao(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<ErroResponse.DetalheErro> detalhes = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String campo = error instanceof FieldError fe ? fe.getField() : error.getObjectName();
                    String mensagem = error.getDefaultMessage();
                    return new ErroResponse.DetalheErro(campo, mensagem);
                })
                .toList();

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ErroResponse.ofValidation("Erro de validação nos campos enviados.", detalhes, request.getRequestURI()));
    }

    // ---- 401 Unauthorized ----

    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ErroResponse> handleNaoAutenticado(
            Exception ex,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErroResponse.of("NAO_AUTENTICADO", "Autenticação necessária ou credenciais inválidas.", request.getRequestURI()));
    }

    // ---- 403 Forbidden ----

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponse> handleSemPermissao(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErroResponse.of("SEM_PERMISSAO", "Você não tem permissão para acessar este recurso.", request.getRequestURI()));
    }

    // ---- 500 Internal Server Error ----

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleErroGenerico(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("[ERRO INTERNO] {} - {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErroResponse.of("ERRO_INTERNO", "Erro interno no servidor. Tente novamente.", request.getRequestURI()));
    }
}