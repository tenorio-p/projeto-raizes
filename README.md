# 🌵 Raízes do Nordeste — API Back-End :)

> Projeto Multidisciplinar — Trilha Back-End | UNINTER - 2026

Sistema de pedidos para rede de lanchonetes nordestinas em expansão.  
API REST com autenticação JWT, controle de estoque por unidade, pagamento mock, programa de fidelização e conformidade com LGPD.

---

## 📋 Índice

- [Tecnologias](#tecnologias)
- [Pré-requisitos](#pré-requisitos)
- [Configuração do ambiente](#configuração-do-ambiente)
- [Como executar](#como-executar)
- [Documentação Swagger](#documentação-swagger)
- [Usuários de teste](#usuários-de-teste)
- [Fluxo principal](#fluxo-principal)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Testes com Postman](#testes-com-postman)

---

## 🛠 Tecnologias

| Tecnologia | Versão | Finalidade |
|---|--------|---|
| Java | 17     | Linguagem principal |
| Spring Boot | 4.0.6  | Framework back-end |
| Spring Security | 6.x    | Autenticação e autorização |
| JWT (JJWT) | 0.11.5 | Tokens de acesso |
| Spring Data JPA | 3.x    | Persistência e ORM |
| PostgreSQL | 15+    | Banco de dados |
| Flyway | 9.x    | Migrations versionadas |
| Lombok | latest | Redução de boilerplate |
| SpringDoc OpenAPI | 2.3.0  | Documentação Swagger |
| Maven | 3.8+   | Gerenciamento de dependências |

---

## ✅ Pré-requisitos

Antes de executar, certifique-se de ter instalado:

- **Java 17+** → `java -version`
- **Maven 3.8+** → `mvn -version`
- **PostgreSQL 15+** rodando localmente
- **Docker + Docker Compose** para banco

---

## ⚙️ Configuração do ambiente

### 1. Clone o repositório

```bash
git clone https://github.com/SEU_USUARIO/raizes-nordeste-api.git
cd raizes-nordeste-api
```

### 2. Crie o banco de dados

Acesse o PostgreSQL e execute:

```sql
CREATE DATABASE raizes_nordeste;
```"""

-----> Suba o banco com Docker <-----

```bash
docker-compose up -d
```

Isso cria automaticamente o banco `raizes_nordeste` com:

- Usuário: `postgres`
- Senha: `postgres`
- Porta: `5432`

Verifique se o container está em execução:

```bash
docker ps
```

Você deverá ver um container PostgreSQL rodando.

Para testar a conexão:

```bash
docker exec postgres-raizes psql -U postgres -c "\l"
```

O banco `raizes_nordeste` deverá aparecer na lista.


### 3. Configure as variáveis de ambiente

Copie o arquivo de exemplo e edite com suas credenciais:

```bash
cp .env.example .env
```

Edite o `.env`:

```env
DB_URL=jdbc:postgresql://localhost:5432/raizes_nordeste
DB_USERNAME=postgres
DB_PASSWORD=sua_senha_aqui
JWT_SECRET=raizes-nordeste-chave-secreta-super-segura-2026
JWT_EXPIRATION=86400000
PORT=8080
```

### 4. Configure o application.properties

O arquivo `src/main/resources/application.properties` já lê as variáveis do `.env` automaticamente via `${VAR:valor_padrao}`.

Se preferir editar diretamente (sem `.env`), altere os valores em:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/raizes_nordeste
spring.datasource.username=postgres
spring.datasource.password=sua_senha
```

---

## ▶️ Como executar

### Opção 1 — Maven (recomendado)

```bash
# Instalar dependências e compilar
mvn clean install -DskipTests

# Iniciar a aplicação
mvn spring-boot:run
```

### Opção 2 — JAR gerado

```bash
mvn clean package -DskipTests
java -jar target/api-1.0.0.jar
```

### Migrations

O **Flyway** executa as migrations automaticamente ao iniciar a aplicação.  
Os scripts estão em `src/main/resources/db/migration/`:

| Arquivo | Conteúdo |
|---|---|
| `V1__criar_tabelas_iniciais.sql` | Criação de todas as tabelas |
| `V2__seed_dados_iniciais.sql` | Dados de teste (usuários, produtos, estoque) |

> Não é necessário rodar nenhum script manualmente — basta iniciar a aplicação com o banco criado.

---

## 📖 Documentação Swagger

Com a aplicação rodando, acesse:

```
http://localhost:8080/swagger-ui.html
```

Para testar endpoints protegidos:

1. Execute `POST /auth/login` com as credenciais abaixo
2. Copie o `accessToken` retornado
3. Clique em **Authorize** (cadeado no topo da página)
4. Informe: `Bearer {seu_token}`
5. Confirme — todos os endpoints protegidos ficarão disponíveis

---

## 👤 Usuários de teste

Todos os usuários abaixo estão no seed (`V2`) com a senha padrão: **`Senha@123`**

| Email | Perfil | Acesso |
|---|---|---|
| `admin@raizes.com` | ADMIN | Total — todos os endpoints |
| `ana@raizes.com` | GERENTE | Estoque, pedidos da unidade |
| `joao@raizes.com` | ATENDENTE | Criar pedidos, atualizar status |
| `cozinha@raizes.com` | COZINHA | Atualizar status (preparo → pronto) |
| `maria@email.com` | CLIENTE | Pedidos próprios, fidelidade |
| `pedro@email.com` | CLIENTE | Pedidos próprios (sem fidelidade) |

---

## 🔄 Fluxo principal (Fluxo A — MVP obrigatório)

Execute os passos na ordem abaixo para demonstrar o fluxo completo:

### Passo 1 — Autenticar
```http
POST /auth/login
{
  "email": "maria@email.com",
  "senha": "Senha@123"
}
```
→ Copie o `accessToken` e use nos próximos passos.

### Passo 2 — Ver cardápio da unidade
```http
GET /unidades/1/cardapio
Authorization: Bearer {token}
```

### Passo 3 — Criar pedido
```http
POST /pedidos
Authorization: Bearer {token}
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
→ Anote o `pedidoId` retornado.

### Passo 4 — Processar pagamento (aprovado)
```http
POST /pagamentos/{pedidoId}/processar
Authorization: Bearer {token}
{
  "simularFalha": false
}
```
→ Pedido avança para `EM_PREPARO` e estoque é baixado.

### Passo 5 — Simular pagamento recusado
```http
POST /pagamentos/{pedidoId}/processar
{
  "simularFalha": true
}
```
→ Pedido permanece `AGUARDANDO_PAGAMENTO`.

### Passo 6 — Cozinha atualiza status
```http
# Autentique com cozinha@raizes.com primeiro
PATCH /pedidos/{pedidoId}/status
{
  "novoStatus": "PRONTO"
}
```

### Passo 7 — Filtrar pedidos por canal
```http
GET /pedidos?canalPedido=APP&status=EM_PREPARO&page=0&size=10
```

---

## 🗂 Estrutura do projeto

```
src/main/java/com/raizesnordeste/api/
│
├── domain/                    ← Camada de Domínio
│   ├── entity/                # Entidades JPA (Pedido, Usuario, Produto...)
│   ├── enums/                 # CanalPedido, StatusPedido, PerfilUsuario...
│   ├── exception/             # Exceções de negócio
│   └── repository/            # Interfaces Spring Data JPA
│
├── application/               ← Camada de Aplicação
│   ├── service/               # Casos de uso (PedidoService, AuthService...)
│   └── dto/                   # DTOs de request e response
│
├── infrastructure/            ← Camada de Infraestrutura
│   ├── security/              # JWT, filtros, UserDetailsService
│   ├── mock/                  # Serviço mock de pagamento
│   └── audit/                 # Serviço de auditoria
│
└── api/                       ← Camada de Interface
    ├── controller/            # Controllers REST
    ├── handler/               # GlobalExceptionHandler
    └── config/                # SecurityConfig, SwaggerConfig, AsyncConfig
```

---

## 🧪 Testes com Postman

A coleção de testes está disponível no repositório:

```
/postman/Raizes_do_Nordeste.postman_collection.json
```

### Como importar

1. Abra o Postman
2. Clique em **Import**
3. Selecione o arquivo `Raizes_do_Nordeste.postman_collection.json`
4. Configure a variável de ambiente `baseUrl` = `http://localhost:8080`

### Ordem sugerida de execução

```
Auth/
  ├── T01 - Login válido (CLIENTE)
  ├── T02 - Login sem token (401)
  ├── T03 - Login credenciais inválidas
  └── T04 - Registro novo usuário

Pedidos/
  ├── T05 - Criar pedido válido (APP)
  ├── T06 - Criar pedido sem canalPedido (422)
  ├── T07 - Criar pedido estoque insuficiente (409)
  └── T08 - Listar pedidos filtro canalPedido

Pagamentos/
  ├── T09 - Pagamento mock aprovado
  └── T10 - Pagamento mock recusado

Status/
  ├── T11 - Atualizar para PRONTO (cozinha)
  └── T12 - Transição inválida (409)

Estoque/
  └── T13 - Consultar estoque (GERENTE)
```

---

## 🔒 Segurança e LGPD

- Senhas armazenadas com **BCrypt** (strength=10) — nunca em texto puro
- Autenticação via **JWT Bearer Token** com expiração configurável
- Autorização por **perfil/role** em todos os endpoints protegidos
- Dados pessoais do usuário **nunca expostos** nas respostas (sem campo `senha`)
- Programa de fidelização **só ativo com consentimento LGPD explícito**
- **Auditoria** de ações sensíveis gravada na tabela `audit_logs`:
    - Login (sucesso e falha)
    - Criação e cancelamento de pedidos
    - Mudanças de status
    - Movimentações de estoque
    - Consentimento LGPD

---

## 📬 Contato

Projeto desenvolvido para a disciplina de Projeto Multidisciplinar — UNINTER 2026.