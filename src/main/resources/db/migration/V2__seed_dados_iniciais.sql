-- Unidades da rede
INSERT INTO unidades (nome, cidade, estado, endereco, telefone)
VALUES ('Raízes do Nordeste - Recife Centro', 'Recife', 'PE', 'Rua da Aurora, 100', '(81) 99999-0001'),
       ('Raízes do Nordeste - Fortaleza Aldeota', 'Fortaleza', 'CE', 'Av. Santos Dumont, 500', '(85) 99999-0002'),
       ('Raízes do Nordeste - Salvador Pelourinho', 'Salvador', 'BA', 'Largo do Pelourinho, 15', '(71) 99999-0003');

-- Usuários de teste (senha: Senha@123 — hash bcrypt)
INSERT INTO usuarios (nome, email, senha, perfil, consentimento_lgpd, data_consentimento, ativo)
VALUES ('Admin Sistema', 'admin@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh3y', 'ADMIN',
        TRUE, NOW(), TRUE),
       ('Maria Cliente', 'maria@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh3y', 'CLIENTE',
        TRUE, NOW(), TRUE),
       ('João Atendente', 'joao@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh3y',
        'ATENDENTE', FALSE, NULL, TRUE),
       ('Ana Gerente', 'ana@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh3y', 'GERENTE',
        FALSE, NULL, TRUE),
       ('Chef Cozinha', 'cozinha@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh3y', 'COZINHA',
        FALSE, NULL, TRUE),
       ('Pedro Cliente', 'pedro@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh3y', 'CLIENTE',
        FALSE, NULL, TRUE);

-- Produtos do cardápio
INSERT INTO produtos (nome, descricao, preco, categoria)
VALUES ('Tapioca Simples', 'Tapioca com manteiga de garrafa', 8.90, 'Tapioca'),
       ('Tapioca Completa', 'Tapioca com queijo coalho, carne e tomate', 18.90, 'Tapioca'),
       ('Cuscuz com Ovo', 'Cuscuz nordestino com ovo mexido', 12.50, 'Cuscuz'),
       ('Cuscuz Recheado', 'Cuscuz com calabresa, queijo e pimentão', 16.90, 'Cuscuz'),
       ('Bolo de Macaxeira', 'Bolo artesanal de macaxeira', 10.00, 'Bolos'),
       ('Café do Sertão', 'Café passado na hora com rapadura', 6.00, 'Bebidas'),
       ('Suco de Umbu', 'Suco natural de umbu', 9.50, 'Bebidas'),
       ('Café da Manhã Completo', 'Cuscuz + ovo + tapioca + café', 28.90, 'Combos');

-- Estoque inicial (unidade 1 — Recife)
INSERT INTO estoques (unidade_id, produto_id, quantidade, quantidade_minima)
VALUES (1, 1, 50, 10),
       (1, 2, 30, 5),
       (1, 3, 40, 8),
       (1, 4, 25, 5),
       (1, 5, 20, 5),
       (1, 6, 100, 20),
       (1, 7, 30, 10),
       (1, 8, 20, 5);

-- Estoque inicial (unidade 2 — Fortaleza)
INSERT INTO estoques (unidade_id, produto_id, quantidade, quantidade_minima)
VALUES (2, 1, 40, 8),
       (2, 2, 20, 5),
       (2, 3, 35, 8),
       (2, 4, 15, 5),
       (2, 6, 80, 20),
       (2, 7, 25, 10),
       (2, 8, 15, 5);

-- Fidelidade para Maria (consentiu com LGPD)
INSERT INTO fidelidade (cliente_id, pontos_acumulados)
VALUES (2, 150);