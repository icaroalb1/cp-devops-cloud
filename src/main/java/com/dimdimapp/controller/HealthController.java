package com.dimdimapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@Tag(name = "Health Check", description = "Endpoints para verificação de saúde da aplicação")
public class HealthController {
    
    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);
    
    @Autowired
    private DataSource dataSource;
    
    @GetMapping
    @Operation(summary = "Health Check", description = "Verifica se a aplicação está funcionando corretamente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Aplicação funcionando corretamente"),
        @ApiResponse(responseCode = "503", description = "Aplicação com problemas")
    })
    public ResponseEntity<Map<String, Object>> healthCheck() {
        logger.info("Health check solicitado");
        
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("application", "DimDimApp");
        health.put("version", "1.0.0");
        
        // Verifica conexão com banco de dados
        try (Connection connection = dataSource.getConnection()) {
            health.put("database", "UP");
            health.put("database_url", connection.getMetaData().getURL());
            logger.info("Health check - Banco de dados: OK");
        } catch (Exception e) {
            health.put("database", "DOWN");
            health.put("database_error", e.getMessage());
            logger.error("Health check - Erro na conexão com banco de dados", e);
        }
        
        boolean isHealthy = "UP".equals(health.get("status")) && "UP".equals(health.get("database"));
        
        if (isHealthy) {
            logger.info("Health check - Aplicação saudável");
            return ResponseEntity.ok(health);
        } else {
            logger.warn("Health check - Aplicação com problemas");
            return ResponseEntity.status(503).body(health);
        }
    }
    
    @GetMapping("/ready")
    @Operation(summary = "Readiness Check", description = "Verifica se a aplicação está pronta para receber requisições")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Aplicação pronta"),
        @ApiResponse(responseCode = "503", description = "Aplicação não está pronta")
    })
    public ResponseEntity<Map<String, Object>> readinessCheck() {
        logger.info("Readiness check solicitado");
        
        Map<String, Object> readiness = new HashMap<>();
        readiness.put("status", "READY");
        readiness.put("timestamp", LocalDateTime.now());
        
        // Verifica se consegue conectar no banco
        try (Connection connection = dataSource.getConnection()) {
            readiness.put("database", "READY");
            logger.info("Readiness check - Banco de dados: READY");
            return ResponseEntity.ok(readiness);
        } catch (Exception e) {
            readiness.put("status", "NOT_READY");
            readiness.put("database", "NOT_READY");
            readiness.put("error", e.getMessage());
            logger.error("Readiness check - Erro na conexão com banco de dados", e);
            return ResponseEntity.status(503).body(readiness);
        }
    }
    
    @GetMapping("/live")
    @Operation(summary = "Liveness Check", description = "Verifica se a aplicação está viva")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Aplicação viva")
    })
    public ResponseEntity<Map<String, Object>> livenessCheck() {
        logger.info("Liveness check solicitado");
        
        Map<String, Object> liveness = new HashMap<>();
        liveness.put("status", "ALIVE");
        liveness.put("timestamp", LocalDateTime.now());
        liveness.put("uptime", System.currentTimeMillis() - Long.parseLong(System.getProperty("java.vm.startTime", "0")));
        
        logger.info("Liveness check - Aplicação viva");
        return ResponseEntity.ok(liveness);
    }
}
