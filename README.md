# Raízes do Nordeste — API Back-End

> Projeto Multidisciplinar — Trilha Back-End | UNINTER 2026 :)

API REST para rede de lanchonetes nordestinas com autenticação JWT, controle de estoque por unidade, pagamento mock, programa de fidelização e conformidade com LGPD.

---

## Tecnologias

| Tecnologia | Versão |
|---|---|
| Java | 17 |
| Spring Boot | 4.0.6 |
| PostgreSQL | 15 |
| Docker + Docker Compose | - |
| Hibernate / JPA | criação automática do schema |
| JJWT | 0.11.5 |
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

Verifique:
```bash
docker ps
# deve aparecer: raizes_nordeste_db
```

### 3. Configure as variáveis de ambiente

```bash
cp .env.example .env
```

O `.env` padrão já funciona com o Docker sem alterações.

### 4. Rode a aplicação

```bash
mvn spring-boot:run
```

Aguarde:
```
Started ApiApplication in X seconds
```

O Hibernate cria automaticamente todas as tabelas a partir das entidades JPA (`ddl-auto=update`). Não é necessário rodar nenhum script SQL.

### 5. Acesse a documentação

```
http://localhost:8080/swagger-ui.html
```

---

## Setup inicial de dados (banco começa vazio)

Como o projeto não usa seed automático, é necessário cadastrar os dados iniciais via API. Siga esta ordem **exatamente**:

### Passo 1 — Criar o usuário administrador

```http
POST /auth/registro
{
  "nome": "Admin Sistema",
  "email": "admin@raizes.com",
  "senha": "Senha@123",
  "perfil": "ADMIN",
  "consentimentoLgpd": false
}
```

A resposta já traz o `accessToken`. Copie-o, clique em **Authorize** no Swagger e informe `Bearer {token}` — todos os passos seguintes usam esse token.

### Passo 2 — Criar uma unidade

```http
POST /unidades
{
  "nome": "Raízes do Nordeste - Recife Centro",
  "cidade": "Recife",
  "estado": "PE",
  "endereco": "Rua da Aurora, 100",
  "telefone": "(81) 99999-0001"
}
```

Anote o `id` retornado (normalmente `1`).

### Passo 3 — Criar produtos

```http
POST /produtos
{
  "nome": "Tapioca Simples",
  "descricao": "Tapioca com manteiga de garrafa",
  "preco": 8.90,
  "categoria": "Tapioca"
}
```

Repita para quantos produtos quiser testar. Anote os `id`s retornados.

### Passo 4 — Lançar estoque do produto na unidade

```http
POST /estoque/unidade/{unidadeId}/movimentar
{
  "produtoId": 1,
  "quantidade": 50,
  "tipo": "ENTRADA",
  "motivo": "Estoque inicial"
}
```

Repita para cada produto cadastrado.

### Passo 5 — Criar os demais usuários de teste

```http
POST /auth/registro
```

Repita trocando `perfil` e `email`:

| Email | Perfil | consentimentoLgpd |
|---|---|---|
| `maria@email.com` | CLIENTE | true |
| `joao@raizes.com` | ATENDENTE | false |
| `ana@raizes.com` | GERENTE | false |
| `cozinha@raizes.com` | COZINHA | false |

Todos com a senha `Senha@123`.

> A partir daqui, o ambiente está pronto para o fluxo completo de pedidos.

---

## Fluxo crítico (Fluxo A)

### 1. Login do cliente
```http
POST /auth/login
{ "email": "maria@email.com", "senha": "Senha@123" }
```

### 2. Ver cardápio da unidade
```http
GET /unidades/1/cardapio
```

### 3. Criar pedido
```http
POST /pedidos
{
  "canalPedido": "APP",
  "unidadeId": 1,
  "itens": [
    { "produtoId": 1, "quantidade": 2 }
  ],
  "formaPagamento": "PIX"
}
```

### 4. Processar pagamento (aprovado)
```http
POST /pagamentos/{pedidoId}/processar
{ "simularFalha": false }
```
→ Estoque é baixado e pedido avança para `EM_PREPARO`.

### 5. Processar pagamento (recusado) — cenário negativo
```http
POST /pagamentos/{outroPedidoId}/processar
{ "simularFalha": true }
```

### 6. Cozinha atualiza o status
Login como `cozinha@raizes.com`, depois:
```http
PATCH /pedidos/{pedidoId}/status
{ "novoStatus": "PRONTO" }
```

### 7. Filtrar pedidos por canal
```http
GET /pedidos?canalPedido=APP&page=0&size=10
```

---

## Testes com Postman

A coleção está em `postman/Raizes_do_Nordeste.postman_collection.json` e já inclui o **setup completo do zero** (criação de admin, unidade, produto, estoque, demais usuários) seguido dos 18 cenários de teste do plano de testes.

### Como importar

1. Abra o Postman → **Import** → selecione o arquivo da coleção
2. Crie um **Environment** com a variável `baseUrl` = `http://localhost:8080`
3. Selecione esse Environment
4. Execute as pastas **na ordem em que aparecem**, de cima para baixo

Todas as variáveis (`token`, `unidadeId`, `produtoId`, `pedidoId`, etc.) são capturadas automaticamente pelos scripts de cada requisição — não é necessário editar nada manualmente.

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

---

## Persistência de dados

O schema do banco é gerado automaticamente pelo Hibernate a partir das anotações `@Entity` das classes em `domain/entity/` (`spring.jpa.hibernate.ddl-auto=update`). Não há migrations versionadas neste projeto — opção feita para manter a configuração simples, adequada ao escopo do trabalho.

---

## Segurança e LGPD

- Senhas armazenadas com **BCrypt** — nunca em texto puro
- Autenticação via **JWT Bearer Token**
- Autorização por **perfil/role** em todos os endpoints protegidos
- Dados pessoais nunca expostos nas respostas (sem campo `senha`)
- Programa de fidelização só ativo com **consentimento LGPD explícito**
- **Auditoria** de ações sensíveis gravada na tabela `audit_logs`