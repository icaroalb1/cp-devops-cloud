@echo off
REM Script para Build e Deploy do DimDimApp (Windows)
REM Este script compila o projeto e faz o deploy para Azure

echo 🚀 Iniciando build e deploy do DimDimApp...

REM Verifica se o Maven está instalado
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Maven não está instalado. Instale o Maven para continuar.
    pause
    exit /b 1
)

REM Verifica se o Azure CLI está instalado
where az >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Azure CLI não está instalado. Instale em: https://docs.microsoft.com/en-us/cli/azure/install-azure-cli
    pause
    exit /b 1
)

REM Variáveis
set RESOURCE_GROUP=dimdim-rg
set WEB_APP_NAME=dimdimappweb
set JAR_FILE=target\dimdim-app-0.0.1-SNAPSHOT.jar

echo [INFO] Iniciando build e deploy do DimDimApp...

REM 1. Limpar e compilar o projeto
echo [INFO] Limpando e compilando o projeto...
call mvn clean compile
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Falha na compilação do projeto
    pause
    exit /b 1
)
echo [SUCCESS] Compilação concluída

REM 2. Executar testes
echo [INFO] Executando testes...
call mvn test
if %ERRORLEVEL% NEQ 0 (
    echo [WARNING] Alguns testes falharam, mas continuando com o build...
)

REM 3. Gerar JAR
echo [INFO] Gerando JAR executável...
call mvn package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Falha na geração do JAR
    pause
    exit /b 1
)
echo [SUCCESS] JAR gerado com sucesso: %JAR_FILE%

REM 4. Verificar se o JAR foi criado
if not exist "%JAR_FILE%" (
    echo [ERROR] JAR não encontrado: %JAR_FILE%
    pause
    exit /b 1
)

REM 5. Verificar se está logado no Azure
az account show >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [INFO] Fazendo login no Azure...
    az login
)

REM 6. Verificar se a Web App existe
az webapp show --name %WEB_APP_NAME% --resource-group %RESOURCE_GROUP% >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Web App %WEB_APP_NAME% não encontrada no Resource Group %RESOURCE_GROUP%
    echo [INFO] Execute primeiro o script deploy.bat para criar a infraestrutura
    pause
    exit /b 1
)

REM 7. Fazer deploy do JAR
echo [INFO] Fazendo deploy do JAR para Azure Web App...
az webapp deployment source config-zip --resource-group %RESOURCE_GROUP% --name %WEB_APP_NAME% --src %JAR_FILE%
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Falha no deploy do JAR
    pause
    exit /b 1
)
echo [SUCCESS] Deploy concluído com sucesso!

REM 8. Obter informações da aplicação
set WEB_APP_URL=https://%WEB_APP_NAME%.azurewebsites.net
echo [INFO] Aguardando aplicação inicializar...
timeout /t 30 /nobreak >nul

REM 9. Testar health check
echo [INFO] Testando health check...
curl -f -s "%WEB_APP_URL%/health" >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Aplicação está funcionando corretamente!
    echo.
    echo 🌐 URLs da aplicação:
    echo   Aplicação: %WEB_APP_URL%
    echo   Swagger UI: %WEB_APP_URL%/swagger-ui.html
    echo   Health Check: %WEB_APP_URL%/health
    echo   API Docs: %WEB_APP_URL%/api-docs
    echo.
    echo 📊 Para monitorar:
    echo   Azure Portal: https://portal.azure.com
    echo   Resource Group: %RESOURCE_GROUP%
) else (
    echo [WARNING] Aplicação pode não estar funcionando corretamente. Verifique os logs no Azure Portal.
)

echo [INFO] Build e deploy finalizados! 🎉
pause
