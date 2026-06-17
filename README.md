# Raízes do Nordeste — API Back-End

> Projeto Multidisciplinar — Trilha Back-End | UNINTER 2026

API REST para rede de lanchonetes nordestinas com autenticação JWT, controle de estoque por unidade, pagamento mock, programa de fidelização e conformidade com LGPD.

---

## Tecnologias

| Tecnologia | Versão |
|---|---|
| Java | 17 |
| Spring Boot | 4.0.6 |
| PostgreSQL | 15 |
| Docker + Docker Compose | - |
| Flyway | migrations |
| JJWT | 0.12.6 |
| SpringDoc OpenAPI | 2.8.5 |

---

## Pré-requisitos

- Java 17+
- Maven 3.8+
- Docker + Docker Compose

---

## Como executar (passo a passo)

### 1. Clone o repositório

```bash
git clone https://github.com/SEU_USUARIO/raizes-nordeste-api.git
cd raizes-nordeste-api
```

### 2. Suba o banco com Docker

```bash
docker-compose up -d
```

Isso cria automaticamente o banco `raizes_nordeste` **e insere todos os dados de teste** (usuários, produtos, estoque).

Verifique:
```bash
docker ps
# deve aparecer: raizes_nordeste_db
```

### 3. Configure as variáveis de ambiente

Copie o arquivo de exemplo:
```bash
cp .env.example .env
```

O `.env` padrão já funciona com o Docker sem alterações.

### 4. Rode a aplicação

```bash
mvn spring-boot:run
```

Aguarde a mensagem:
```
Started ApiApplication in X seconds
```

### 5. Acesse a documentação

```
http://localhost:8080/swagger-ui.html
```

---

## Usuários de teste

Todos com a senha: **`Senha@123`**

| Email | Perfil |
|---|---|
| `admin@raizes.com` | ADMIN |
| `maria@email.com` | CLIENTE (com fidelidade) |
| `joao@raizes.com` | ATENDENTE |
| `ana@raizes.com` | GERENTE |
| `cozinha@raizes.com` | COZINHA |
| `pedro@email.com` | CLIENTE |

---

## Fluxo crítico para teste (Fluxo A)

Execute na ordem:

### 1. Login
```
POST /auth/login
{ "email": "maria@email.com", "senha": "Senha@123" }
```
Copie o `accessToken` → clique **Authorize** no Swagger → `Bearer {token}`

### 2. Cardápio da unidade
```
GET /unidades/1/cardapio
```

### 3. Criar pedido
```
POST /pedidos
{
  "canalPedido": "APP",
  "unidadeId": 1,
  "itens": [
    { "produtoId": 1, "quantidade": 2 },
    { "produtoId": 6, "quantidade": 1 }
  ],
  "formaPagamento": "PIX"
}
```

### 4. Processar pagamento (aprovado)
```
POST /pagamentos/{pedidoId}/processar
{ "simularFalha": false }
```

### 5. Processar pagamento (recusado)
```
POST /pagamentos/{pedidoId}/processar
{ "simularFalha": true }
```

### 6. Atualizar status (login como cozinha primeiro)
```
POST /auth/login
{ "email": "cozinha@raizes.com", "senha": "Senha@123" }

PATCH /pedidos/{pedidoId}/status
{ "novoStatus": "PRONTO" }
```

### 7. Filtrar pedidos por canal
```
GET /pedidos?canalPedido=APP&page=0&size=10
```

---

## Testes com Postman

Importe a coleção:
```
postman/Raizes_do_Nordeste.postman_collection.json
```

Configure o ambiente:
- `baseUrl` = `http://localhost:8080`

Execute na ordem T01 → T18.

---

## Links

- Swagger: `http://localhost:8080/swagger-ui.html`
- Repositório da coleção Postman: `/postman/Raizes_do_Nordeste.postman_collection.json`

---

## Estrutura do projeto

```
src/main/java/br/com/raizesnordeste/api/
├── api/
│   ├── config/        # SecurityConfig, SwaggerConfig, AsyncConfig
│   ├── controller/    # AuthController, PedidoController, PagamentoController...
│   └── handler/       # GlobalExceptionHandler (padrão de erro JSON)
├── application/
│   ├── dto/           # DTOs de request e response
│   └── service/       # AuthService, PedidoService, PagamentoService...
├── domain/
│   ├── entity/        # Entidades JPA
│   ├── enums/         # CanalPedido, StatusPedido, PerfilUsuario...
│   ├── exception/     # Exceções de domínio
│   └── repository/    # Interfaces Spring Data JPA
└── infrastructure/
    ├── audit/         # AuditService (logs de ações sensíveis)
    ├── mock/          # PagamentoMockService (gateway externo simulado)
    └── security/      # JwtService, JwtAuthFilter, UserDetailsServiceImpl
```