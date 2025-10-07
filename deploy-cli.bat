@echo off
REM Script de Deploy CLI Simplificado - DimDimApp
echo 🚀 Deploy CLI - DimDimApp

REM Variáveis
set RESOURCE_GROUP=dimdim-rg-cli
set LOCATION=eastus2
set SQL_SERVER_NAME=dimdim-sql-cli
set SQL_DATABASE_NAME=dimdim-db
set SQL_ADMIN_USER=fiapuser
set SQL_ADMIN_PASSWORD=Fiap@12345
set APP_SERVICE_PLAN=dimdim-plan-cli
set WEB_APP_NAME=dimdimapp-cli
set APP_INSIGHTS_NAME=dimdim-insights-cli

echo [INFO] Configurações:
echo   Resource Group: %RESOURCE_GROUP%
echo   Location: %LOCATION%
echo   SQL Server: %SQL_SERVER_NAME%
echo   Web App: %WEB_APP_NAME%

REM 1. Build da aplicação
echo [INFO] Fazendo build da aplicação...
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Falha no build
    exit /b 1
)

REM 2. Criar Resource Group
echo [INFO] Criando Resource Group...
az group create --name %RESOURCE_GROUP% --location %LOCATION%

REM 3. Criar SQL Server
echo [INFO] Criando SQL Server...
az sql server create --name %SQL_SERVER_NAME% --resource-group %RESOURCE_GROUP% --location %LOCATION% --admin-user %SQL_ADMIN_USER% --admin-password %SQL_ADMIN_PASSWORD%

REM 4. Configurar Firewall
echo [INFO] Configurando firewall...
az sql server firewall-rule create --resource-group %RESOURCE_GROUP% --server %SQL_SERVER_NAME% --name "AllowAzureServices" --start-ip-address 0.0.0.0 --end-ip-address 0.0.0.0

REM 5. Criar Database
echo [INFO] Criando Database...
az sql db create --resource-group %RESOURCE_GROUP% --server %SQL_SERVER_NAME% --name %SQL_DATABASE_NAME% --service-objective S0

REM 6. Criar Application Insights
echo [INFO] Criando Application Insights...
az monitor app-insights component create --app %APP_INSIGHTS_NAME% --location %LOCATION% --resource-group %RESOURCE_GROUP%

REM 7. Obter Connection String do Application Insights
echo [INFO] Obtendo Connection String...
for /f "tokens=*" %%i in ('az monitor app-insights component show --app %APP_INSIGHTS_NAME% --resource-group %RESOURCE_GROUP% --query connectionString -o tsv') do set APP_INSIGHTS_CONNECTION_STRING=%%i

REM 8. Criar App Service Plan
echo [INFO] Criando App Service Plan...
az appservice plan create --name %APP_SERVICE_PLAN% --resource-group %RESOURCE_GROUP% --sku F1 --is-linux --location %LOCATION%

REM 9. Criar Web App
echo [INFO] Criando Web App...
az webapp create --resource-group %RESOURCE_GROUP% --plan %APP_SERVICE_PLAN% --name %WEB_APP_NAME% --runtime "JAVA:21-java21"

REM 10. Configurar Application Settings
echo [INFO] Configurando variáveis de ambiente...
az webapp config appsettings set --resource-group %RESOURCE_GROUP% --name %WEB_APP_NAME% --settings "SPRING_PROFILES_ACTIVE=prod" "SPRING_DATASOURCE_URL=jdbc:sqlserver://%SQL_SERVER_NAME%.database.windows.net:1433;database=%SQL_DATABASE_NAME%;encrypt=true;trustServerCertificate=false;loginTimeout=30;" "SPRING_DATASOURCE_USERNAME=%SQL_ADMIN_USER%" "SPRING_DATASOURCE_PASSWORD=%SQL_ADMIN_PASSWORD%" "SPRING_JPA_HIBERNATE_DDL_AUTO=update" "SPRING_JPA_SHOW_SQL=false" "LOGGING_LEVEL_COM_DIMDIMAPP=INFO" "APPLICATIONINSIGHTS_CONNECTION_STRING=%APP_INSIGHTS_CONNECTION_STRING%"

REM 11. Deploy da aplicação
echo [INFO] Fazendo deploy da aplicação...
az webapp deploy --resource-group %RESOURCE_GROUP% --name %WEB_APP_NAME% --src-path target/dimdim-app-0.0.1-SNAPSHOT.jar --type jar

echo [SUCCESS] Deploy concluído!
echo.
echo 🌐 URLs:
echo   Aplicação: https://%WEB_APP_NAME%.azurewebsites.net
echo   Swagger: https://%WEB_APP_NAME%.azurewebsites.net/swagger-ui.html
echo   Health: https://%WEB_APP_NAME%.azurewebsites.net/health
echo.
echo [INFO] Deploy finalizado! 🎉



