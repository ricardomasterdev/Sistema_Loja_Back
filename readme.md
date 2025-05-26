# 📱 Sistema de Controle de Vendas e Estoque (Loja SIGEP)

---

## 📋 Sumário

- [🚀 Visão Geral](#-visão-geral)
- [🏛️ Arquitetura](#️-arquitetura)
    - [Frontend](#frontend)
    - [Backend](#backend)
    - [Banco de Dados](#banco-de-dados)
- [🔧 Tecnologias & Frameworks](#-tecnologias--frameworks)
- [📊 Modelagem de Dados](#-modelagem-de-dados)
- [🛡️ Segurança & Autenticação](#️-segurança--autenticação)
- [✅ Boas Práticas](#-boas-práticas)
- [⚙️ Instalação & Execução](#️-instalação--execução)
- [🤝 Contribuição](#-contribuição)
- [📄 Licença](#-licença)

---

## 🚀 Visão Geral

Este sistema foi projetado para uma loja de celulares que precisa de:
- Cadastro de produtos
- Gerenciamento de estoque
- Registro de vendas com múltiplos itens
- Atualização automática de estoque

OBS: Adicionado outros campos alem os que foram relatados no pedido no documento.

*Preço unitario para registrar o preço do momento do produto , 
caso o mesmo seja editado o preço os que ja foram vendidos 
continuam com preço registrado no momento.

*Data da Venda


---

## 🏛️ Arquitetura

### Frontend

- **Framework**: React 18+
- **Navegação**: React Router (SPA)
- **UI**: React Bootstrap
- **Comunicação**: Axios para chamadas REST
- **Estrutura**:
    - **Pages** (e.g. `Login.jsx`, `Vendas.jsx`)
    - **Components** (e.g. `PaginationControl`)
    - **Services** (e.g. `saleService.js`)

### Backend

- **Language & Runtime**: Java 17
- **Framework**: Spring Boot 2.x
- **Organização de pacotes**:
    - `controller` — endpoints REST
    - `service` — regras de negócio
    - `repository` — JDBC Template
    - `model` — entidades de domínio
- **Documentação**: Springdoc OpenAPI (Swagger UI)

ENDERECO PARA ACESSAR I (Swagger UI) APOS APLICAÇÃO ESTA RODANDO.

http://app1.cdxsistemas.com.br:8080/swagger-ui/index.html#


### Banco de Dados

- **SGDB**: Microsoft SQL Server 2019+
- **Tabelas principais**:
    - `Produtos`
    - `Vendas`
    - `ItensVenda`
- **Integridade**: chaves estrangeiras, restrições de quantidade (≥ 0)


---

## 🔧 Tecnologias & Frameworks

| Camada    | Tecnologia / Versão          |
|-----------|------------------------------|
| Frontend  | Node.js v22.14.0 / npm 10.9.2 |
|           | React 18+ / React Bootstrap  |
| Backend   | Java 11+ / Spring Boot 2.x   |
|           | Spring Security / JWT        |
|           | Spring JDBC Template         |
| Banco     | SQL Server 2019+             |
| Ferramentas | Maven 3.6+ / Git            |

---

## 📊 Modelagem de Dados

### Produto

| Campo                | Tipo     | Restrição      |
|----------------------|----------|----------------|
| `id`                 | Integer  | PK, obrigatório|
| `nome`               | String   | obrigatório    |
| `descricao`          | String   | opcional       |
| `quantidade_disponivel` | Integer  | ≥ 0, obrigatório|
| `valor_unitario`     | Decimal  | obrigatório    |

### Venda

| Campo         | Tipo    | Restrição       |
|---------------|---------|-----------------|
| `id`          | Integer | PK, obrigatório |
| `cliente`     | String  | obrigatório     |
| `valor_total` | Decimal | calculado       |
| `datavenda`   | date    | obrigatorio     |

### ItemVenda

| Campo        | Tipo     | Restrição                              |
|--------------|----------|----------------------------------------|
| `venda_id`   | Integer  | FK → `Vendas.id`                       |
| `produto_id` | Integer  | FK → `Produtos.id`                     |
| `quantidade` | Integer  | ≤ `quantidade_disponivel` do produto   |

---

## 🛡️ Segurança & Autenticação

1. **Spring Security + JWT**
2. **Endpoint de login**
    - `POST /api/auth/login`
    - Payload:
      ```json
      {
        "username": "seu_usuario",
        "password": "sua_senha"
      }
      ```  
    - Resposta: token JWT
3. **Envio de token**
    - Header `Authorization: Bearer <token>`
4. **Proteção de rotas**
    - Configuração de filtros e roles

---

## ✅ Boas Práticas

- **Camadas bem definidas**: Controller → Service → Repository
- **Validação**: JSR-380 (Bean Validation)
- **Transações**: `@Transactional` no Service
- **Tratamento global de exceções**: `@ControllerAdvice`
- **Profiles de configuração**: `application.properties`
- **Documentação API**: Swagger UI
- **Logging estruturado**: SLF4J + Logback

---

## ⚙️ Instalação & Execução

1. **Pré-requisitos**
    - Java 17
    - Maven 3.6+
    - Node.js 22.14.0, npm 10.9.2
    - SQL Server EXPRESS

2. **Banco de Dados**

acesso: 

jdbc:sqlserver://;serverName=177.53.148.179;databaseName=lojaDB

serverName=177.53.148.179
databaseName=lojaDB

Usuario: sysloja
senha=Ric@7901


3. **Backend**
   ```bash
   cd backend
   mvn clean package
   mvn spring-boot:run
