# DimDimApp 🏦

Projeto Java completo para o Checkpoint da disciplina "DevOps Tools & Cloud Computing" (Tema: DimDim).

## 📋 Sobre o Projeto

O DimDimApp é uma API RESTful desenvolvida em Java 21 com Spring Boot 3.3+ para gerenciamento de clientes e transações financeiras. O projeto demonstra práticas de DevOps e Cloud Computing com deploy automatizado no Azure.

## 🏗️ Arquitetura e Tecnologias

- **Java 21** - Linguagem de programação
- **Spring Boot 3.3+** - Framework principal
- **Spring Data JPA + Hibernate** - ORM para persistência
- **SQL Server (Azure PaaS)** - Banco de dados
- **Swagger/OpenAPI** - Documentação da API
- **Application Insights** - Monitoramento
- **Azure Web App** - Plataforma de deploy
- **Maven** - Gerenciamento de dependências

## 📦 Estrutura do Projeto

```
com.dimdimapp
├── controller/          # Controladores REST
│   ├── ClienteController.java
│   ├── TransacaoController.java
│   └── HealthController.java
├── service/            # Camada de serviços
│   ├── ClienteService.java
│   └── TransacaoService.java
├── repository/         # Repositórios JPA
│   ├── ClienteRepository.java
│   └── TransacaoRepository.java
├── model/             # Entidades JPA
│   ├── Cliente.java
│   └── Transacao.java
└── config/            # Configurações
    ├── SwaggerConfig.java
    └── ApplicationInsightsConfig.java
```

## 🗄️ Modelo de Dados

### Entidade Cliente
- `id` (Long) - Chave primária
- `nome` (String) - Nome do cliente
- `email` (String) - Email único
- `telefone` (String) - Telefone no formato (XX) XXXXX-XXXX
- `transacoes` (List<Transacao>) - Lista de transações

### Entidade Transacao
- `id` (Long) - Chave primária
- `valor` (Double) - Valor da transação
- `data` (LocalDate) - Data da transação
- `cliente` (Cliente) - Cliente associado

## 🌐 Endpoints da API

### Clientes
- `GET /clientes` - Lista todos os clientes
- `GET /clientes/{id}` - Busca cliente por ID
- `GET /clientes/buscar?nome={nome}` - Busca clientes por nome
- `POST /clientes` - Cria novo cliente
- `PUT /clientes/{id}` - Atualiza cliente
- `DELETE /clientes/{id}` - Deleta cliente
- `GET /clientes/contar` - Conta total de clientes

### Transações
- `GET /transacoes` - Lista todas as transações
- `GET /transacoes/{id}` - Busca transação por ID
- `GET /transacoes/cliente/{clienteId}` - Busca transações por cliente
- `GET /transacoes/data?data={data}` - Busca transações por data
- `GET /transacoes/periodo?dataInicio={inicio}&dataFim={fim}` - Busca por período
- `POST /transacoes` - Cria nova transação
- `PUT /transacoes/{id}` - Atualiza transação
- `DELETE /transacoes/{id}` - Deleta transação
- `GET /transacoes/cliente/{clienteId}/total` - Calcula total por cliente
- `GET /transacoes/contar` - Conta total de transações

### Health Check
- `GET /health` - Status geral da aplicação
- `GET /health/ready` - Verifica se está pronta para receber requisições
- `GET /health/live` - Verifica se a aplicação está viva

## 🚀 Como Executar Localmente

### Pré-requisitos
- Java 21+
- Maven 3.6+
- SQL Server (local ou Azure)

### 1. Clone o repositório
```bash
git clone <url-do-repositorio>
cd cp-devops-cloud
```

### 2. Configure o banco de dados
A aplicação está configurada para usar SQL Server. As credenciais já estão configuradas:

**Para desenvolvimento local:**
- **Servidor**: localhost:1433
- **Database**: dimdim_db
- **Usuário**: sa
- **Senha**: DimDim@123

**Para produção (Azure):**
- As credenciais são configuradas automaticamente via variáveis de ambiente

### 3. Execute a aplicação
```bash
mvn spring-boot:run
```

### 4. Acesse a aplicação
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health Check: http://localhost:8080/health

## ☁️ Deploy no Azure

### 1. Criar infraestrutura
```bash
chmod +x scripts/deploy.sh
./scripts/deploy.sh
```

### 2. Build e deploy da aplicação
```bash
chmod +x scripts/build-and-deploy.sh
./scripts/build-and-deploy.sh
```

### 3. Configurar Application Insights
1. Acesse o Azure Portal
2. Crie um recurso Application Insights
3. Copie a Connection String
4. Configure como variável de ambiente na Web App:
   ```
   APPLICATIONINSIGHTS_CONNECTION_STRING=InstrumentationKey=xxx;IngestionEndpoint=https://xxx.in.applicationinsights.azure.com/
   ```

## 📊 Monitoramento

### Application Insights
- Métricas de performance
- Logs de aplicação
- Rastreamento de dependências
- Alertas customizados

### Health Checks
- Verificação de saúde da aplicação
- Status do banco de dados
- Métricas de uptime

## 📚 Documentação da API

A documentação completa da API está disponível via Swagger UI:
- **Local**: http://localhost:8080/swagger-ui.html
- **Azure**: https://dimdimappweb.azurewebsites.net/swagger-ui.html

## 🧪 Testando a API

### Exemplo de criação de cliente
```bash
curl -X POST http://localhost:8080/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@email.com",
    "telefone": "(11) 99999-9999"
  }'
```

### Exemplo de criação de transação
```bash
curl -X POST http://localhost:8080/transacoes \
  -H "Content-Type: application/json" \
  -d '{
    "valor": 150.50,
    "data": "2024-01-15",
    "cliente": {
      "id": 1
    }
  }'
```

## 🔧 Configurações

### Variáveis de Ambiente (Azure)
- `SPRING_DATASOURCE_URL` - URL do banco SQL Server
- `SPRING_DATASOURCE_USERNAME` - Usuário do banco
- `SPRING_DATASOURCE_PASSWORD` - Senha do banco
- `APPLICATIONINSIGHTS_CONNECTION_STRING` - Connection string do Application Insights

### Configurações do Banco
- Dialeto: SQL Server
- DDL: update (cria/atualiza tabelas automaticamente)
- Pool de conexões: HikariCP
- Timeout: 30 segundos

## 📈 Funcionalidades Implementadas

✅ **CRUD completo** para Clientes e Transações  
✅ **Validação de dados** com Bean Validation  
✅ **Relacionamento JPA** (OneToMany/ManyToOne)  
✅ **Documentação Swagger** com OpenAPI 3  
✅ **Health checks** para monitoramento  
✅ **Logging estruturado** com SLF4J  
✅ **Application Insights** totalmente integrado para telemetria  
✅ **Deploy automatizado** para Azure com infraestrutura completa  
✅ **Scripts de infraestrutura** como código  
✅ **Tratamento de erros** com ResponseEntity  
✅ **Configuração de variáveis de ambiente** para produção  
✅ **Múltiplos perfis** (test, prod)  
✅ **Credenciais pré-configuradas** para execução local  

## 🎯 Próximos Passos

- [ ] Implementar autenticação/autorização
- [ ] Adicionar testes de integração
- [ ] Configurar CI/CD com GitHub Actions
- [ ] Implementar cache com Redis
- [ ] Adicionar métricas customizadas
- [ ] Configurar alertas no Application Insights

## 👥 Equipe

Projeto desenvolvido para o 2º Checkpoint da disciplina "DevOps Tools & Cloud Computing" - Tema DimDim.

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

**🎉 Projeto DimDimApp - DevOps Tools & Cloud Computing**
