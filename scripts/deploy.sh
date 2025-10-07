#!/bin/bash

# Script de Deploy para Azure - DimDimApp
# Projeto para Checkpoint DevOps Tools & Cloud Computing

set -e  # Para o script em caso de erro

echo "🚀 Iniciando deploy do DimDimApp para Azure..."

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para log colorido
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verifica se o Azure CLI está instalado
if ! command -v az &> /dev/null; then
    log_error "Azure CLI não está instalado. Instale em: https://docs.microsoft.com/en-us/cli/azure/install-azure-cli"
    exit 1
fi

# Verifica se está logado no Azure
if ! az account show &> /dev/null; then
    log_info "Fazendo login no Azure..."
    az login
fi

# Variáveis de configuração
RESOURCE_GROUP="dimdim-rg"
LOCATION="eastus"
SQL_SERVER_NAME="dimdim-sql-server"
SQL_DATABASE_NAME="dimdim-db"
SQL_ADMIN_USER="fiapuser"
SQL_ADMIN_PASSWORD="Fiap@12345"
APP_SERVICE_PLAN="dimdim-plan"
WEB_APP_NAME="dimdimappweb"
JAVA_VERSION="21-java"
APP_INSIGHTS_NAME="dimdim-app-insights"

log_info "Configurações do deploy:"
echo "  Resource Group: $RESOURCE_GROUP"
echo "  Location: $LOCATION"
echo "  SQL Server: $SQL_SERVER_NAME"
echo "  Database: $SQL_DATABASE_NAME"
echo "  Web App: $WEB_APP_NAME"
echo "  Java Version: $JAVA_VERSION"

# 1. Criar Resource Group
log_info "Criando Resource Group: $RESOURCE_GROUP"
if az group show --name $RESOURCE_GROUP &> /dev/null; then
    log_warning "Resource Group $RESOURCE_GROUP já existe"
else
    az group create --name $RESOURCE_GROUP --location $LOCATION
    log_success "Resource Group criado com sucesso"
fi

# 2. Criar SQL Server
log_info "Criando SQL Server: $SQL_SERVER_NAME"
if az sql server show --name $SQL_SERVER_NAME --resource-group $RESOURCE_GROUP &> /dev/null; then
    log_warning "SQL Server $SQL_SERVER_NAME já existe"
else
    az sql server create \
        --name $SQL_SERVER_NAME \
        --resource-group $RESOURCE_GROUP \
        --location $LOCATION \
        --admin-user $SQL_ADMIN_USER \
        --admin-password $SQL_ADMIN_PASSWORD
    log_success "SQL Server criado com sucesso"
fi

# 3. Configurar Firewall do SQL Server (permitir acesso do Azure)
log_info "Configurando firewall do SQL Server"
az sql server firewall-rule create \
    --resource-group $RESOURCE_GROUP \
    --server $SQL_SERVER_NAME \
    --name "AllowAzureServices" \
    --start-ip-address 0.0.0.0 \
    --end-ip-address 0.0.0.0

# 4. Criar Database
log_info "Criando Database: $SQL_DATABASE_NAME"
if az sql db show --name $SQL_DATABASE_NAME --resource-group $RESOURCE_GROUP --server $SQL_SERVER_NAME &> /dev/null; then
    log_warning "Database $SQL_DATABASE_NAME já existe"
else
    az sql db create \
        --resource-group $RESOURCE_GROUP \
        --server $SQL_SERVER_NAME \
        --name $SQL_DATABASE_NAME \
        --service-objective S0
    log_success "Database criado com sucesso"
fi

# 5. Criar Application Insights
log_info "Criando Application Insights: $APP_INSIGHTS_NAME"
if az monitor app-insights component show --app $APP_INSIGHTS_NAME --resource-group $RESOURCE_GROUP &> /dev/null; then
    log_warning "Application Insights $APP_INSIGHTS_NAME já existe"
else
    az monitor app-insights component create \
        --app $APP_INSIGHTS_NAME \
        --location $LOCATION \
        --resource-group $RESOURCE_GROUP
    log_success "Application Insights criado com sucesso"
fi

# 6. Obter Connection String do Application Insights
log_info "Obtendo Connection String do Application Insights"
APP_INSIGHTS_CONNECTION_STRING=$(az monitor app-insights component show \
    --app $APP_INSIGHTS_NAME \
    --resource-group $RESOURCE_GROUP \
    --query connectionString -o tsv)

# 7. Criar App Service Plan
log_info "Criando App Service Plan: $APP_SERVICE_PLAN"
if az appservice plan show --name $APP_SERVICE_PLAN --resource-group $RESOURCE_GROUP &> /dev/null; then
    log_warning "App Service Plan $APP_SERVICE_PLAN já existe"
else
    az appservice plan create \
        --name $APP_SERVICE_PLAN \
        --resource-group $RESOURCE_GROUP \
        --sku B1 \
        --is-linux
    log_success "App Service Plan criado com sucesso"
fi

# 8. Criar Web App
log_info "Criando Web App: $WEB_APP_NAME"
if az webapp show --name $WEB_APP_NAME --resource-group $RESOURCE_GROUP &> /dev/null; then
    log_warning "Web App $WEB_APP_NAME já existe"
else
    az webapp create \
        --resource-group $RESOURCE_GROUP \
        --plan $APP_SERVICE_PLAN \
        --name $WEB_APP_NAME \
        --runtime "$JAVA_VERSION"
    log_success "Web App criada com sucesso"
fi

# 9. Configurar Connection String
log_info "Configurando Connection String"
CONNECTION_STRING="jdbc:sqlserver://$SQL_SERVER_NAME.database.windows.net:1433;database=$SQL_DATABASE_NAME;user=$SQL_ADMIN_USER;password=$SQL_ADMIN_PASSWORD;encrypt=true;trustServerCertificate=false;loginTimeout=30;"

az webapp config connection-string set \
    --resource-group $RESOURCE_GROUP \
    --name $WEB_APP_NAME \
    --settings "DefaultConnection=$CONNECTION_STRING" \
    --connection-string-type SQLAzure

log_success "Connection String configurada"

# 10. Configurar Application Settings
log_info "Configurando Application Settings"
az webapp config appsettings set \
    --resource-group $RESOURCE_GROUP \
    --name $WEB_APP_NAME \
    --settings \
        "SPRING_PROFILES_ACTIVE=prod" \
        "SPRING_DATASOURCE_URL=jdbc:sqlserver://$SQL_SERVER_NAME.database.windows.net:1433;database=$SQL_DATABASE_NAME;encrypt=true;trustServerCertificate=false;loginTimeout=30;" \
        "SPRING_DATASOURCE_USERNAME=$SQL_ADMIN_USER" \
        "SPRING_DATASOURCE_PASSWORD=$SQL_ADMIN_PASSWORD" \
        "SPRING_JPA_HIBERNATE_DDL_AUTO=update" \
        "SPRING_JPA_SHOW_SQL=false" \
        "LOGGING_LEVEL_COM_DIMDIMAPP=INFO" \
        "APPLICATIONINSIGHTS_CONNECTION_STRING=$APP_INSIGHTS_CONNECTION_STRING"

log_success "Application Settings configuradas"

# 11. Configurar Logs
log_info "Configurando Application Logging"
az webapp log config \
    --resource-group $RESOURCE_GROUP \
    --name $WEB_APP_NAME \
    --application-logging filesystem \
    --level information

log_success "Logs configurados"

# 12. Obter URL da aplicação
WEB_APP_URL="https://$WEB_APP_NAME.azurewebsites.net"
log_success "Deploy concluído com sucesso!"
echo ""
echo "🌐 URLs importantes:"
echo "  Aplicação: $WEB_APP_URL"
echo "  Swagger UI: $WEB_APP_URL/swagger-ui.html"
echo "  Health Check: $WEB_APP_URL/health"
echo "  API Docs: $WEB_APP_URL/api-docs"
echo ""
echo "📊 Para monitorar a aplicação:"
echo "  Azure Portal: https://portal.azure.com"
echo "  Resource Group: $RESOURCE_GROUP"
echo ""
echo "🔧 Para fazer deploy do código:"
echo "  az webapp deployment source config-zip --resource-group $RESOURCE_GROUP --name $WEB_APP_NAME --src target/dimdim-app-0.0.1-SNAPSHOT.jar"
echo ""
log_info "Deploy finalizado! 🎉"
