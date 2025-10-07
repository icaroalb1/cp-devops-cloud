# 🚀 Configuração CLI - DimDimApp

## 📋 Pré-requisitos
- SQL Server Express instalado
- Java 21+
- Maven 3.9+

## 🔧 1. Habilitar Autenticação Mista

**Execute como Administrador:**
```powershell
powershell -ExecutionPolicy Bypass -File .\habilitar-autenticacao-simples.ps1
```

**OU manualmente:**
1. Pressione `Win + R`
2. Digite: `services.msc`
3. Encontre "SQL Server (SQLEXPRESS)"
4. Clique com botão direito → "Restart"

## 🗄️ 3. Configurar Login SA e Banco

### Via sqlcmd:
```cmd
"C:\Program Files\Microsoft SQL Server\Client SDK\ODBC\170\Tools\Binn\SQLCMD.EXE" -S localhost -E -Q "ALTER LOGIN sa WITH PASSWORD = 'DimDim@123'"
"C:\Program Files\Microsoft SQL Server\Client SDK\ODBC\170\Tools\Binn\SQLCMD.EXE" -S localhost -E -Q "CREATE DATABASE dimdim_db"
```

### Testar conexão:
```cmd
"C:\Program Files\Microsoft SQL Server\Client SDK\ODBC\170\Tools\Binn\SQLCMD.EXE" -S localhost -U sa -P "DimDim@123" -Q "SELECT DB_NAME()"
```

## 🚀 3. Executar Aplicação

```cmd
mvn spring-boot:run
```

## 🧪 4. Testar API

### Health Check:
```cmd
curl http://localhost:8080/health
```

### Swagger UI:
```
http://localhost:8080/swagger-ui.html
```

## 📊 5. Verificar Status

### Portas SQL Server:
```cmd
netstat -an | findstr "1433\|1434"
```

### Processos Java:
```cmd
jps -l
```

---

**✅ Projeto limpo e pronto para execução via CLI!**