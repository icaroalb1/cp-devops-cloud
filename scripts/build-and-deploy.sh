#!/bin/bash

# Script para Build e Deploy do DimDimApp
# Este script compila o projeto e faz o deploy para Azure

set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

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

# Verifica se o Maven está instalado
if ! command -v mvn &> /dev/null; then
    log_error "Maven não está instalado. Instale o Maven para continuar."
    exit 1
fi

# Verifica se o Azure CLI está instalado
if ! command -v az &> /dev/null; then
    log_error "Azure CLI não está instalado. Instale em: https://docs.microsoft.com/en-us/cli/azure/install-azure-cli"
    exit 1
fi

# Variáveis
RESOURCE_GROUP="dimdim-rg"
WEB_APP_NAME="dimdimappweb"
JAR_FILE="target/dimdim-app-0.0.1-SNAPSHOT.jar"

log_info "Iniciando build e deploy do DimDimApp..."

# 1. Limpar e compilar o projeto
log_info "Limpando e compilando o projeto..."
mvn clean compile

if [ $? -ne 0 ]; then
    log_error "Falha na compilação do projeto"
    exit 1
fi

log_success "Compilação concluída"

# 2. Executar testes
log_info "Executando testes..."
mvn test

if [ $? -ne 0 ]; then
    log_warning "Alguns testes falharam, mas continuando com o build..."
fi

# 3. Gerar JAR
log_info "Gerando JAR executável..."
mvn package -DskipTests

if [ $? -ne 0 ]; then
    log_error "Falha na geração do JAR"
    exit 1
fi

log_success "JAR gerado com sucesso: $JAR_FILE"

# 4. Verificar se o JAR foi criado
if [ ! -f "$JAR_FILE" ]; then
    log_error "JAR não encontrado: $JAR_FILE"
    exit 1
fi

# 5. Verificar se está logado no Azure
if ! az account show &> /dev/null; then
    log_info "Fazendo login no Azure..."
    az login
fi

# 6. Verificar se a Web App existe
if ! az webapp show --name $WEB_APP_NAME --resource-group $RESOURCE_GROUP &> /dev/null; then
    log_error "Web App $WEB_APP_NAME não encontrada no Resource Group $RESOURCE_GROUP"
    log_info "Execute primeiro o script deploy.sh para criar a infraestrutura"
    exit 1
fi

# 7. Fazer deploy do JAR
log_info "Fazendo deploy do JAR para Azure Web App..."
az webapp deployment source config-zip \
    --resource-group $RESOURCE_GROUP \
    --name $WEB_APP_NAME \
    --src $JAR_FILE

if [ $? -ne 0 ]; then
    log_error "Falha no deploy do JAR"
    exit 1
fi

log_success "Deploy concluído com sucesso!"

# 8. Obter informações da aplicação
WEB_APP_URL="https://$WEB_APP_NAME.azurewebsites.net"
log_info "Aguardando aplicação inicializar..."
sleep 30

# 9. Testar health check
log_info "Testando health check..."
if curl -f -s "$WEB_APP_URL/health" > /dev/null; then
    log_success "Aplicação está funcionando corretamente!"
    echo ""
    echo "🌐 URLs da aplicação:"
    echo "  Aplicação: $WEB_APP_URL"
    echo "  Swagger UI: $WEB_APP_URL/swagger-ui.html"
    echo "  Health Check: $WEB_APP_URL/health"
    echo "  API Docs: $WEB_APP_URL/api-docs"
    echo ""
    echo "📊 Para monitorar:"
    echo "  Azure Portal: https://portal.azure.com"
    echo "  Resource Group: $RESOURCE_GROUP"
else
    log_warning "Aplicação pode não estar funcionando corretamente. Verifique os logs no Azure Portal."
fi

log_info "Build e deploy finalizados! 🎉"
