package com.dimdimapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// Application Insights - Comentado temporariamente (dependência não disponível)
// import com.microsoft.applicationinsights.TelemetryClient;

/**
 * Configuração do Application Insights para monitoramento da aplicação
 */
@Configuration
public class ApplicationInsightsConfig {

    /**
     * Bean do TelemetryClient para enviar telemetrias para o Application Insights
     * Comentado temporariamente (dependência não disponível)
     */
    /*
    @Bean
    public TelemetryClient telemetryClient() {
        return new TelemetryClient();
    }
    */

    /**
     * Envia uma telemetria de evento personalizada
     * Comentado temporariamente (dependência não disponível)
     */
    /*
    public static void trackCustomEvent(TelemetryClient telemetryClient, String eventName, String message) {
        if (telemetryClient != null) {
            telemetryClient.trackEvent(eventName);
            telemetryClient.trackTrace(message);
        }
    }
    */

    /**
     * Envia uma telemetria de exceção
     * Comentado temporariamente (dependência não disponível)
     */
    /*
    public static void trackException(TelemetryClient telemetryClient, Exception exception, String message) {
        if (telemetryClient != null) {
            telemetryClient.trackException(exception);
            telemetryClient.trackTrace(message);
        }
    }
    */

    /**
     * Envia uma telemetria de métrica personalizada
     * Comentado temporariamente (dependência não disponível)
     */
    /*
    public static void trackCustomMetric(TelemetryClient telemetryClient, String metricName, double value) {
        if (telemetryClient != null) {
            telemetryClient.trackMetric(metricName, value);
        }
    }
    */

    /**
     * Envia uma telemetria de dependência
     * Comentado temporariamente (dependência não disponível)
     */
    /*
    public static void trackDependency(TelemetryClient telemetryClient, String dependencyName, String commandName, 
                                     long duration, boolean success) {
        if (telemetryClient != null) {
            telemetryClient.trackDependency(dependencyName, commandName, duration, success);
        }
    }
    */
}