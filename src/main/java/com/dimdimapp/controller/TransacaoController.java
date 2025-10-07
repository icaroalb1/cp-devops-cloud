package com.dimdimapp.controller;

import com.dimdimapp.model.Transacao;
import com.dimdimapp.service.TransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transacoes")
@Tag(name = "Transações", description = "API para gerenciamento de transações")
public class TransacaoController {
    
    private static final Logger logger = LoggerFactory.getLogger(TransacaoController.class);
    
    private final TransacaoService transacaoService;
    
    @Autowired
    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }
    
    @GetMapping
    @Operation(summary = "Lista todas as transações", description = "Retorna uma lista com todas as transações cadastradas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de transações retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<Transacao>> listarTodas() {
        logger.info("Endpoint GET /transacoes chamado");
        try {
            List<Transacao> transacoes = transacaoService.listarTodas();
            logger.info("Retornando {} transações", transacoes.size());
            return ResponseEntity.ok(transacoes);
        } catch (Exception e) {
            logger.error("Erro ao listar transações", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Busca transação por ID", description = "Retorna uma transação específica pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transação encontrada"),
        @ApiResponse(responseCode = "404", description = "Transação não encontrada"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Transacao> buscarPorId(
            @Parameter(description = "ID da transação") @PathVariable Long id) {
        logger.info("Endpoint GET /transacoes/{} chamado", id);
        try {
            Optional<Transacao> transacao = transacaoService.buscarPorId(id);
            if (transacao.isPresent()) {
                logger.info("Transação encontrada: ID {} - Valor: {}", id, transacao.get().getValor());
                return ResponseEntity.ok(transacao.get());
            } else {
                logger.warn("Transação não encontrada com ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar transação com ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Busca transações por cliente", description = "Retorna todas as transações de um cliente específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transações encontradas"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<Transacao>> buscarPorCliente(
            @Parameter(description = "ID do cliente") @PathVariable Long clienteId) {
        logger.info("Endpoint GET /transacoes/cliente/{} chamado", clienteId);
        try {
            List<Transacao> transacoes = transacaoService.buscarPorCliente(clienteId);
            logger.info("Encontradas {} transações para o cliente ID: {}", transacoes.size(), clienteId);
            return ResponseEntity.ok(transacoes);
        } catch (Exception e) {
            logger.error("Erro ao buscar transações do cliente ID: {}", clienteId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/data")
    @Operation(summary = "Busca transações por data", description = "Retorna transações de uma data específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transações encontradas"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<Transacao>> buscarPorData(
            @Parameter(description = "Data para busca (formato: yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        logger.info("Endpoint GET /transacoes/data?data={} chamado", data);
        try {
            List<Transacao> transacoes = transacaoService.buscarPorData(data);
            logger.info("Encontradas {} transações na data: {}", transacoes.size(), data);
            return ResponseEntity.ok(transacoes);
        } catch (Exception e) {
            logger.error("Erro ao buscar transações da data: {}", data, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/periodo")
    @Operation(summary = "Busca transações por período", description = "Retorna transações de um período específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transações encontradas"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<Transacao>> buscarPorPeriodo(
            @Parameter(description = "Data de início (formato: yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Data de fim (formato: yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        logger.info("Endpoint GET /transacoes/periodo?dataInicio={}&dataFim={} chamado", dataInicio, dataFim);
        try {
            List<Transacao> transacoes = transacaoService.buscarPorPeriodo(dataInicio, dataFim);
            logger.info("Encontradas {} transações no período: {} a {}", transacoes.size(), dataInicio, dataFim);
            return ResponseEntity.ok(transacoes);
        } catch (Exception e) {
            logger.error("Erro ao buscar transações do período: {} a {}", dataInicio, dataFim, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    @Operation(summary = "Cria nova transação", description = "Cadastra uma nova transação no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Transação criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou cliente não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Transacao> criar(@Valid @RequestBody Transacao transacao) {
        logger.info("Endpoint POST /transacoes chamado para transação de valor: {}", transacao.getValor());
        try {
            Transacao transacaoSalva = transacaoService.salvar(transacao);
            logger.info("Transação criada com sucesso. ID: {}", transacaoSalva.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(transacaoSalva);
        } catch (RuntimeException e) {
            logger.warn("Erro de validação ao criar transação: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Erro ao criar transação", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza transação", description = "Atualiza os dados de uma transação existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transação atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou cliente não encontrado"),
        @ApiResponse(responseCode = "404", description = "Transação não encontrada"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Transacao> atualizar(
            @Parameter(description = "ID da transação") @PathVariable Long id,
            @Valid @RequestBody Transacao transacao) {
        logger.info("Endpoint PUT /transacoes/{} chamado", id);
        try {
            Transacao transacaoAtualizada = transacaoService.atualizar(id, transacao);
            logger.info("Transação atualizada com sucesso. ID: {}", id);
            return ResponseEntity.ok(transacaoAtualizada);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrada")) {
                logger.warn("Transação não encontrada para atualização. ID: {}", id);
                return ResponseEntity.notFound().build();
            } else {
                logger.warn("Erro de validação ao atualizar transação: {}", e.getMessage());
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao atualizar transação com ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta transação", description = "Remove uma transação do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Transação deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Transação não encontrada"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID da transação") @PathVariable Long id) {
        logger.info("Endpoint DELETE /transacoes/{} chamado", id);
        try {
            transacaoService.deletar(id);
            logger.info("Transação deletada com sucesso. ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.warn("Transação não encontrada para deleção. ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Erro ao deletar transação com ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/cliente/{clienteId}/total")
    @Operation(summary = "Calcula total por cliente", description = "Retorna o valor total das transações de um cliente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Total calculado com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Double> calcularTotalPorCliente(
            @Parameter(description = "ID do cliente") @PathVariable Long clienteId) {
        logger.info("Endpoint GET /transacoes/cliente/{}/total chamado", clienteId);
        try {
            Double total = transacaoService.calcularTotalPorCliente(clienteId);
            logger.info("Total calculado para cliente {}: {}", clienteId, total);
            return ResponseEntity.ok(total);
        } catch (Exception e) {
            logger.error("Erro ao calcular total do cliente ID: {}", clienteId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/contar")
    @Operation(summary = "Conta transações", description = "Retorna o número total de transações cadastradas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contagem realizada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Long> contar() {
        logger.info("Endpoint GET /transacoes/contar chamado");
        try {
            long total = transacaoService.contar();
            logger.info("Total de transações: {}", total);
            return ResponseEntity.ok(total);
        } catch (Exception e) {
            logger.error("Erro ao contar transações", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
