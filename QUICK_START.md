# 🚀 DimDimApp - Guia de Início Rápido

## ⚡ Execução Local (Desenvolvimento)

### 1. Pré-requisitos
- Java 21+
- Maven 3.6+
- SQL Server (local ou Azure)

### 2. Banco de Dados
As credenciais já estão configuradas no `application.properties`:
- **Servidor**: localhost:1433
- **Database**: dimdim_db  
- **Usuário**: sa
- **Senha**: DimDim@123

### 3. Executar
```bash
mvn spring-boot:run
```

### 4. Testar
- **API**: http://localhost:8080
- **Swagger**: http://localhost:8080/swagger-ui.html
- **Health**: http://localhost:8080/health

---

## ☁️ Deploy no Azure (Produção)

### 1. Criar Infraestrutura
```bash
# Linux/Mac
./scripts/deploy.sh

# Windows
scripts\deploy.bat
```

### 2. Build e Deploy
```bash
# Linux/Mac
./scripts/build-and-deploy.sh

# Windows
scripts\build-and-deploy.bat
```

### 3. Acessar Aplicação
- **URL**: https://dimdimappweb.azurewebsites.net
- **Swagger**: https://dimdimappweb.azurewebsites.net/swagger-ui.html

---

## 🧪 Testes Rápidos

### Criar Cliente
```bash
curl -X POST http://localhost:8080/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@email.com",
    "telefone": "(11) 99999-9999"
  }'
```

### Criar Transação
```bash
curl -X POST http://localhost:8080/transacoes \
  -H "Content-Type: application/json" \
  -d '{
    "valor": 150.50,
    "data": "2024-01-15",
    "cliente": {"id": 1}
  }'
```

### Health Check
```bash
curl http://localhost:8080/health
```

---

## 📊 Monitoramento

### Azure Portal
- Acesse: https://portal.azure.com
- Resource Group: `dimdim-rg`
- Web App: `dimdimappweb`
- SQL Server: `dimdim-sql-server`

### Application Insights
1. Crie recurso Application Insights no Azure
2. Copie a Connection String
3. Configure na Web App:
   ```
   APPLICATIONINSIGHTS_CONNECTION_STRING=InstrumentationKey=xxx;IngestionEndpoint=https://xxx.in.applicationinsights.azure.com/
   ```

---

## 🔧 Troubleshooting

### Erro de Conexão com Banco
- Verifique as credenciais no `application.properties`
- Confirme se o SQL Server está rodando
- Verifique se o firewall permite conexões

### Erro no Deploy
- Execute `az login` antes do deploy
- Verifique se tem permissões no Azure
- Confirme se o Resource Group existe

### Aplicação não Inicia
- Verifique os logs no Azure Portal
- Confirme se as variáveis de ambiente estão corretas
- Teste localmente primeiro

---

## 📚 Documentação Completa

Para mais detalhes, consulte o [README.md](README.md) principal.

---

**🎉 Projeto DimDimApp - DevOps Tools & Cloud Computing**
