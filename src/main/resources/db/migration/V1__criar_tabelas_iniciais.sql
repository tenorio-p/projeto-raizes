-- Tabela de unidades
CREATE TABLE unidades
(
    id            BIGSERIAL PRIMARY KEY,
    nome          VARCHAR(100) NOT NULL,
    cidade        VARCHAR(100) NOT NULL,
    estado        CHAR(2)      NOT NULL,
    endereco      VARCHAR(255),
    telefone      VARCHAR(20),
    ativa         BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em     TIMESTAMP    NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Tabela de usuários
CREATE TABLE usuarios
(
    id                 BIGSERIAL PRIMARY KEY,
    nome               VARCHAR(100) NOT NULL,
    email              VARCHAR(150) NOT NULL UNIQUE,
    senha              VARCHAR(255) NOT NULL,
    telefone           VARCHAR(20),
    data_nascimento    DATE,
    perfil             VARCHAR(20)  NOT NULL,
    consentimento_lgpd BOOLEAN      NOT NULL DEFAULT FALSE,
    data_consentimento TIMESTAMP,
    criado_em          TIMESTAMP    NOT NULL DEFAULT NOW(),
    atualizado_em      TIMESTAMP    NOT NULL DEFAULT NOW(),
    ativo              BOOLEAN      NOT NULL DEFAULT TRUE
);

-- Tabela de produtos
CREATE TABLE produtos
(
    id            BIGSERIAL PRIMARY KEY,
    nome          VARCHAR(150)   NOT NULL,
    descricao     TEXT,
    preco         NUMERIC(10, 2) NOT NULL,
    categoria     VARCHAR(50),
    url_imagem    VARCHAR(500),
    ativo         BOOLEAN        NOT NULL DEFAULT TRUE,
    criado_em     TIMESTAMP      NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- Tabela de estoque
CREATE TABLE estoques
(
    id                BIGSERIAL PRIMARY KEY,
    unidade_id        BIGINT    NOT NULL REFERENCES unidades (id),
    produto_id        BIGINT    NOT NULL REFERENCES produtos (id),
    quantidade        INTEGER   NOT NULL DEFAULT 0,
    quantidade_minima INTEGER   NOT NULL DEFAULT 5,
    atualizado_em     TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_estoque_unidade_produto UNIQUE (unidade_id, produto_id),
    CONSTRAINT chk_quantidade_positiva CHECK (quantidade >= 0)
);

-- Tabela de pedidos
CREATE TABLE pedidos
(
    id              BIGSERIAL PRIMARY KEY,
    canal_pedido    VARCHAR(20)    NOT NULL,
    status          VARCHAR(30)    NOT NULL DEFAULT 'AGUARDANDO_PAGAMENTO',
    cliente_id      BIGINT         NOT NULL REFERENCES usuarios (id),
    unidade_id      BIGINT         NOT NULL REFERENCES unidades (id),
    total           NUMERIC(10, 2) NOT NULL DEFAULT 0,
    forma_pagamento VARCHAR(20)    NOT NULL,
    observacao      TEXT,
    criado_em       TIMESTAMP      NOT NULL DEFAULT NOW(),
    atualizado_em   TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- Índice para filtrar pedidos por canal
CREATE INDEX idx_pedidos_canal ON pedidos (canal_pedido);
CREATE INDEX idx_pedidos_status ON pedidos (status);
CREATE INDEX idx_pedidos_cliente ON pedidos (cliente_id);
CREATE INDEX idx_pedidos_unidade ON pedidos (unidade_id);

-- Tabela de itens do pedido
CREATE TABLE itens_pedido
(
    id             BIGSERIAL PRIMARY KEY,
    pedido_id      BIGINT         NOT NULL REFERENCES pedidos (id),
    produto_id     BIGINT         NOT NULL REFERENCES produtos (id),
    quantidade     INTEGER        NOT NULL,
    preco_unitario NUMERIC(10, 2) NOT NULL,
    CONSTRAINT chk_quantidade_item CHECK (quantidade > 0)
);

-- Tabela de pagamentos
CREATE TABLE pagamentos
(
    id               BIGSERIAL PRIMARY KEY,
    pedido_id        BIGINT         NOT NULL UNIQUE REFERENCES pedidos (id),
    forma_pagamento  VARCHAR(20)    NOT NULL,
    valor            NUMERIC(10, 2) NOT NULL,
    status_pagamento VARCHAR(20)    NOT NULL DEFAULT 'PENDENTE',
    transacao_id     VARCHAR(100),
    mensagem_gateway VARCHAR(255),
    payload_gateway  TEXT,
    criado_em        TIMESTAMP      NOT NULL DEFAULT NOW(),
    processado_em    TIMESTAMP
);

-- Tabela de fidelidade
CREATE TABLE fidelidade
(
    id                BIGSERIAL PRIMARY KEY,
    cliente_id        BIGINT    NOT NULL UNIQUE REFERENCES usuarios (id),
    pontos_acumulados INTEGER   NOT NULL DEFAULT 0,
    pontos_resgatados INTEGER   NOT NULL DEFAULT 0,
    atualizado_em     TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_pontos_acumulados CHECK (pontos_acumulados >= 0),
    CONSTRAINT chk_pontos_resgatados CHECK (pontos_resgatados >= 0)
);

-- Tabela de auditoria
CREATE TABLE audit_logs
(
    id          BIGSERIAL PRIMARY KEY,
    acao        VARCHAR(100) NOT NULL,
    usuario_id  BIGINT,
    entidade    VARCHAR(50),
    entidade_id BIGINT,
    detalhes    TEXT,
    ip_origem   VARCHAR(45),
    criado_em   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_acao ON audit_logs (acao);
CREATE INDEX idx_audit_usuario ON audit_logs (usuario_id);