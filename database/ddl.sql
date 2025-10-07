-- =============================================
-- DDL - DimDimApp Database
-- Tabelas: Cliente (Master) e Transacao (Detail)
-- =============================================

-- Tabela Cliente (Master)
CREATE TABLE clientes (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefone VARCHAR(255) NOT NULL
);

-- Tabela Transacao (Detail)
CREATE TABLE transacoes (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    valor FLOAT NOT NULL,
    data DATE NOT NULL,
    cliente_id BIGINT NOT NULL,
    CONSTRAINT FK_transacoes_cliente 
        FOREIGN KEY (cliente_id) 
        REFERENCES clientes(id)
        ON DELETE CASCADE
);

-- Índices para performance
CREATE INDEX IX_transacoes_cliente_id ON transacoes(cliente_id);
CREATE INDEX IX_clientes_email ON clientes(email);

-- Comentários das tabelas
EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'Tabela master de clientes da DimDim', 
    @level0type = N'SCHEMA', @level0name = N'dbo', 
    @level1type = N'TABLE', @level1name = N'clientes';

EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'Tabela detail de transações dos clientes', 
    @level0type = N'SCHEMA', @level0name = N'dbo', 
    @level1type = N'TABLE', @level1name = N'transacoes';
