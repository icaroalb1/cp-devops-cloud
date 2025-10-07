package com.dimdimapp.controller;

import com.dimdimapp.model.Cliente;
import com.dimdimapp.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clientes")
@Tag(name = "Clientes", description = "API para gerenciamento de clientes")
public class ClienteController {
    
    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);
    
    private final ClienteService clienteService;
    
    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }
    
    @GetMapping
    @Operation(summary = "Lista todos os clientes", description = "Retorna uma lista com todos os clientes cadastrados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<Cliente>> listarTodos() {
        logger.info("Endpoint GET /clientes chamado");
        try {
            List<Cliente> clientes = clienteService.listarTodos();
            logger.info("Retornando {} clientes", clientes.size());
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            logger.error("Erro ao listar clientes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Busca cliente por ID", description = "Retorna um cliente específico pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Cliente> buscarPorId(
            @Parameter(description = "ID do cliente") @PathVariable Long id) {
        logger.info("Endpoint GET /clientes/{} chamado", id);
        try {
            Optional<Cliente> cliente = clienteService.buscarPorId(id);
            if (cliente.isPresent()) {
                logger.info("Cliente encontrado: {}", cliente.get().getNome());
                return ResponseEntity.ok(cliente.get());
            } else {
                logger.warn("Cliente não encontrado com ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar cliente com ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/buscar")
    @Operation(summary = "Busca clientes por nome", description = "Retorna clientes que contenham o nome informado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Clientes encontrados"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<Cliente>> buscarPorNome(
            @Parameter(description = "Nome para busca") @RequestParam String nome) {
        logger.info("Endpoint GET /clientes/buscar?nome={} chamado", nome);
        try {
            List<Cliente> clientes = clienteService.buscarPorNome(nome);
            logger.info("Encontrados {} clientes com nome contendo: {}", clientes.size(), nome);
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            logger.error("Erro ao buscar clientes por nome: {}", nome, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    @Operation(summary = "Cria novo cliente", description = "Cadastra um novo cliente no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou email já existente"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Cliente> criar(@Valid @RequestBody Cliente cliente) {
        logger.info("Endpoint POST /clientes chamado para cliente: {}", cliente.getNome());
        try {
            Cliente clienteSalvo = clienteService.salvar(cliente);
            logger.info("Cliente criado com sucesso. ID: {}", clienteSalvo.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
        } catch (RuntimeException e) {
            logger.warn("Erro de validação ao criar cliente: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Erro ao criar cliente", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza cliente", description = "Atualiza os dados de um cliente existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou email já existente"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Cliente> atualizar(
            @Parameter(description = "ID do cliente") @PathVariable Long id,
            @Valid @RequestBody Cliente cliente) {
        logger.info("Endpoint PUT /clientes/{} chamado", id);
        try {
            Cliente clienteAtualizado = clienteService.atualizar(id, cliente);
            logger.info("Cliente atualizado com sucesso. ID: {}", id);
            return ResponseEntity.ok(clienteAtualizado);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado")) {
                logger.warn("Cliente não encontrado para atualização. ID: {}", id);
                return ResponseEntity.notFound().build();
            } else {
                logger.warn("Erro de validação ao atualizar cliente: {}", e.getMessage());
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao atualizar cliente com ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta cliente", description = "Remove um cliente do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do cliente") @PathVariable Long id) {
        logger.info("Endpoint DELETE /clientes/{} chamado", id);
        try {
            clienteService.deletar(id);
            logger.info("Cliente deletado com sucesso. ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.warn("Cliente não encontrado para deleção. ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Erro ao deletar cliente com ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/contar")
    @Operation(summary = "Conta clientes", description = "Retorna o número total de clientes cadastrados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contagem realizada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Long> contar() {
        logger.info("Endpoint GET /clientes/contar chamado");
        try {
            long total = clienteService.contar();
            logger.info("Total de clientes: {}", total);
            return ResponseEntity.ok(total);
        } catch (Exception e) {
            logger.error("Erro ao contar clientes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
