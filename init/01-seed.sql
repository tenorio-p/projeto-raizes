-- =============================================================
-- RAÍZES DO NORDESTE — SEED INICIAL
-- Executado automaticamente pelo Docker na primeira inicialização
-- =============================================================

INSERT INTO unidades (nome, cidade, estado, endereco, telefone, ativa, criado_em, atualizado_em) VALUES
  ('Raizes do Nordeste - Recife Centro',       'Recife',    'PE', 'Rua da Aurora, 100',        '(81) 99999-0001', true, NOW(), NOW()),
  ('Raizes do Nordeste - Fortaleza Aldeota',   'Fortaleza', 'CE', 'Av. Santos Dumont, 500',    '(85) 99999-0002', true, NOW(), NOW()),
  ('Raizes do Nordeste - Salvador Pelourinho', 'Salvador',  'BA', 'Largo do Pelourinho, 15',   '(71) 99999-0003', true, NOW(), NOW());

-- Senha padrao: Senha@123 (hash BCrypt gerado pela propria aplicacao)
INSERT INTO usuarios (nome, email, senha, perfil, consentimento_lgpd, data_consentimento, ativo, criado_em, atualizado_em) VALUES
  ('Admin Sistema',  'admin@raizes.com',   '$2a$10$S6rY2WZ6zpfwiQeEGqFMDuOPvzOlDUsOJT9oFSewQUIMNZg7A2bhi', 'ADMIN',     true,  NOW(), true, NOW(), NOW()),
  ('Maria Cliente',  'maria@email.com',    '$2a$10$S6rY2WZ6zpfwiQeEGqFMDuOPvzOlDUsOJT9oFSewQUIMNZg7A2bhi', 'CLIENTE',   true,  NOW(), true, NOW(), NOW()),
  ('Joao Atendente', 'joao@raizes.com',    '$2a$10$S6rY2WZ6zpfwiQeEGqFMDuOPvzOlDUsOJT9oFSewQUIMNZg7A2bhi', 'ATENDENTE', false, NULL,  true, NOW(), NOW()),
  ('Ana Gerente',    'ana@raizes.com',     '$2a$10$S6rY2WZ6zpfwiQeEGqFMDuOPvzOlDUsOJT9oFSewQUIMNZg7A2bhi', 'GERENTE',   false, NULL,  true, NOW(), NOW()),
  ('Chef Cozinha',   'cozinha@raizes.com', '$2a$10$S6rY2WZ6zpfwiQeEGqFMDuOPvzOlDUsOJT9oFSewQUIMNZg7A2bhi', 'COZINHA',   false, NULL,  true, NOW(), NOW()),
  ('Pedro Cliente',  'pedro@email.com',    '$2a$10$S6rY2WZ6zpfwiQeEGqFMDuOPvzOlDUsOJT9oFSewQUIMNZg7A2bhi', 'CLIENTE',   false, NULL,  true, NOW(), NOW());

INSERT INTO produtos (nome, descricao, preco, categoria, ativo, criado_em, atualizado_em) VALUES
  ('Tapioca Simples',        'Tapioca com manteiga de garrafa',            8.90,  'Tapioca', true, NOW(), NOW()),
  ('Tapioca Completa',       'Tapioca com queijo coalho, carne e tomate', 18.90,  'Tapioca', true, NOW(), NOW()),
  ('Cuscuz com Ovo',         'Cuscuz nordestino com ovo mexido',           12.50, 'Cuscuz',  true, NOW(), NOW()),
  ('Cuscuz Recheado',        'Cuscuz com calabresa, queijo e pimentao',   16.90, 'Cuscuz',  true, NOW(), NOW()),
  ('Bolo de Macaxeira',      'Bolo artesanal de macaxeira',               10.00, 'Bolos',   true, NOW(), NOW()),
  ('Cafe do Sertao',         'Cafe passado na hora com rapadura',          6.00,  'Bebidas', true, NOW(), NOW()),
  ('Suco de Umbu',           'Suco natural de umbu',                       9.50,  'Bebidas', true, NOW(), NOW()),
  ('Cafe da Manha Completo', 'Cuscuz mais ovo mais tapioca mais cafe',    28.90, 'Combos',  true, NOW(), NOW());

INSERT INTO estoques (unidade_id, produto_id, quantidade, quantidade_minima, atualizado_em) VALUES
  (1,1,50,10,NOW()),(1,2,30,5,NOW()),(1,3,40,8,NOW()),(1,4,25,5,NOW()),
  (1,5,20,5,NOW()),(1,6,100,20,NOW()),(1,7,30,10,NOW()),(1,8,20,5,NOW()),
  (2,1,40,8,NOW()),(2,2,20,5,NOW()),(2,3,35,8,NOW()),(2,4,15,5,NOW()),
  (2,6,80,20,NOW()),(2,7,25,10,NOW()),(2,8,15,5,NOW());

INSERT INTO fidelidade (cliente_id, pontos_acumulados, pontos_resgatados, atualizado_em)
VALUES (2, 0, 0, NOW());
