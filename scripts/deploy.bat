@echo off
REM Script de Deploy para Azure - DimDimApp (Windows)
REM Projeto para Checkpoint DevOps Tools & Cloud Computing

echo 🚀 Iniciando deploy do DimDimApp para Azure...

REM Verifica se o Azure CLI está instalado
where az >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Azure CLI não está instalado. Instale em: https://docs.microsoft.com/en-us/cli/azure/install-azure-cli
    exit /b 1
)

REM Verifica se está logado no Azure
az account show >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [INFO] Fazendo login no Azure...
    az login
)

REM Variáveis de configuração
set RESOURCE_GROUP=dimdim-rg
set LOCATION=eastus
set SQL_SERVER_NAME=dimdim-sql-server
set SQL_DATABASE_NAME=dimdim-db
set SQL_ADMIN_USER=fiapuser
set SQL_ADMIN_PASSWORD=Fiap@12345
set APP_SERVICE_PLAN=dimdim-plan
set WEB_APP_NAME=dimdimappweb
set JAVA_VERSION=21-java
set APP_INSIGHTS_NAME=dimdim-app-insights

echo [INFO] Configurações do deploy:
echo   Resource Group: %RESOURCE_GROUP%
echo   Location: %LOCATION%
echo   SQL Server: %SQL_SERVER_NAME%
echo   Database: %SQL_DATABASE_NAME%
echo   Web App: %WEB_APP_NAME%
echo   Java Version: %JAVA_VERSION%

REM 1. Criar Resource Group
echo [INFO] Criando Resource Group: %RESOURCE_GROUP%
az group create --name %RESOURCE_GROUP% --location %LOCATION%

REM 2. Criar SQL Server
echo [INFO] Criando SQL Server: %SQL_SERVER_NAME%
az sql server create --name %SQL_SERVER_NAME% --resource-group %RESOURCE_GROUP% --location %LOCATION% --admin-user %SQL_ADMIN_USER% --admin-password %SQL_ADMIN_PASSWORD%

REM 3. Configurar Firewall do SQL Server
echo [INFO] Configurando firewall do SQL Server
az sql server firewall-rule create --resource-group %RESOURCE_GROUP% --server %SQL_SERVER_NAME% --name "AllowAzureServices" --start-ip-address 0.0.0.0 --end-ip-address 0.0.0.0

REM 4. Criar Database
echo [INFO] Criando Database: %SQL_DATABASE_NAME%
az sql db create --resource-group %RESOURCE_GROUP% --server %SQL_SERVER_NAME% --name %SQL_DATABASE_NAME% --service-objective S0

REM 5. Criar Application Insights
echo [INFO] Criando Application Insights: %APP_INSIGHTS_NAME%
az monitor app-insights component create --app %APP_INSIGHTS_NAME% --location %LOCATION% --resource-group %RESOURCE_GROUP%

REM 6. Obter Connection String do Application Insights
echo [INFO] Obtendo Connection String do Application Insights
for /f "tokens=*" %%i in ('az monitor app-insights component show --app %APP_INSIGHTS_NAME% --resource-group %RESOURCE_GROUP% --query connectionString -o tsv') do set APP_INSIGHTS_CONNECTION_STRING=%%i

REM 7. Criar App Service Plan
echo [INFO] Criando App Service Plan: %APP_SERVICE_PLAN%
az appservice plan create --name %APP_SERVICE_PLAN% --resource-group %RESOURCE_GROUP% --sku B1 --is-linux

REM 8. Criar Web App
echo [INFO] Criando Web App: %WEB_APP_NAME%
az webapp create --resource-group %RESOURCE_GROUP% --plan %APP_SERVICE_PLAN% --name %WEB_APP_NAME% --runtime "%JAVA_VERSION%"

REM 9. Configurar Connection String
echo [INFO] Configurando Connection String
set CONNECTION_STRING=jdbc:sqlserver://%SQL_SERVER_NAME%.database.windows.net:1433;database=%SQL_DATABASE_NAME%;user=%SQL_ADMIN_USER%;password=%SQL_ADMIN_PASSWORD%;encrypt=true;trustServerCertificate=false;loginTimeout=30;
az webapp config connection-string set --resource-group %RESOURCE_GROUP% --name %WEB_APP_NAME% --settings "DefaultConnection=%CONNECTION_STRING%" --connection-string-type SQLAzure

REM 10. Configurar Application Settings
echo [INFO] Configurando Application Settings
az webapp config appsettings set --resource-group %RESOURCE_GROUP% --name %WEB_APP_NAME% --settings "SPRING_PROFILES_ACTIVE=prod" "SPRING_DATASOURCE_URL=jdbc:sqlserver://%SQL_SERVER_NAME%.database.windows.net:1433;database=%SQL_DATABASE_NAME%;encrypt=true;trustServerCertificate=false;loginTimeout=30;" "SPRING_DATASOURCE_USERNAME=%SQL_ADMIN_USER%" "SPRING_DATASOURCE_PASSWORD=%SQL_ADMIN_PASSWORD%" "SPRING_JPA_HIBERNATE_DDL_AUTO=update" "SPRING_JPA_SHOW_SQL=false" "LOGGING_LEVEL_COM_DIMDIMAPP=INFO" "APPLICATIONINSIGHTS_CONNECTION_STRING=%APP_INSIGHTS_CONNECTION_STRING%"

REM 11. Configurar Logs
echo [INFO] Configurando Application Logging
az webapp log config --resource-group %RESOURCE_GROUP% --name %WEB_APP_NAME% --application-logging filesystem --level information

echo [SUCCESS] Deploy concluído com sucesso!
echo.
echo 🌐 URLs importantes:
echo   Aplicação: https://%WEB_APP_NAME%.azurewebsites.net
echo   Swagger UI: https://%WEB_APP_NAME%.azurewebsites.net/swagger-ui.html
echo   Health Check: https://%WEB_APP_NAME%.azurewebsites.net/health
echo   API Docs: https://%WEB_APP_NAME%.azurewebsites.net/api-docs
echo.
echo 📊 Para monitorar a aplicação:
echo   Azure Portal: https://portal.azure.com
echo   Resource Group: %RESOURCE_GROUP%
echo.
echo [INFO] Deploy finalizado! 🎉

pause
