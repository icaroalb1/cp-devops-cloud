package com.dimdimapp.service;

import com.dimdimapp.model.Cliente;
import com.dimdimapp.repository.ClienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService {
    
    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);
    
    private final ClienteRepository clienteRepository;
    
    @Autowired
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }
    
    /**
     * Lista todos os clientes
     */
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        logger.info("Listando todos os clientes");
        return clienteRepository.findAll();
    }
    
    /**
     * Busca cliente por ID
     */
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Long id) {
        logger.info("Buscando cliente com ID: {}", id);
        return clienteRepository.findById(id);
    }
    
    /**
     * Busca cliente por email
     */
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorEmail(String email) {
        logger.info("Buscando cliente com email: {}", email);
        return clienteRepository.findByEmail(email);
    }
    
    /**
     * Busca clientes por nome
     */
    @Transactional(readOnly = true)
    public List<Cliente> buscarPorNome(String nome) {
        logger.info("Buscando clientes com nome contendo: {}", nome);
        return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }
    
    /**
     * Salva um novo cliente
     */
    public Cliente salvar(Cliente cliente) {
        logger.info("Salvando novo cliente: {}", cliente.getNome());
        
        // Verifica se já existe cliente com o mesmo email
        if (clienteRepository.findByEmail(cliente.getEmail()).isPresent()) {
            logger.warn("Tentativa de salvar cliente com email já existente: {}", cliente.getEmail());
            throw new RuntimeException("Já existe um cliente cadastrado com este email");
        }
        
        Cliente clienteSalvo = clienteRepository.save(cliente);
        logger.info("Cliente salvo com sucesso. ID: {}", clienteSalvo.getId());
        return clienteSalvo;
    }
    
    /**
     * Atualiza um cliente existente
     */
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        logger.info("Atualizando cliente com ID: {}", id);
        
        Cliente clienteExistente = clienteRepository.findById(id)
            .orElseThrow(() -> {
                logger.warn("Cliente não encontrado para atualização. ID: {}", id);
                return new RuntimeException("Cliente não encontrado");
            });
        
        // Verifica se o email não está sendo usado por outro cliente
        if (!clienteExistente.getEmail().equals(clienteAtualizado.getEmail()) &&
            clienteRepository.existsByEmailAndIdNot(clienteAtualizado.getEmail(), id)) {
            logger.warn("Tentativa de atualizar cliente com email já existente: {}", clienteAtualizado.getEmail());
            throw new RuntimeException("Já existe outro cliente cadastrado com este email");
        }
        
        // Atualiza os campos
        clienteExistente.setNome(clienteAtualizado.getNome());
        clienteExistente.setEmail(clienteAtualizado.getEmail());
        clienteExistente.setTelefone(clienteAtualizado.getTelefone());
        
        Cliente clienteAtualizadoSalvo = clienteRepository.save(clienteExistente);
        logger.info("Cliente atualizado com sucesso. ID: {}", clienteAtualizadoSalvo.getId());
        return clienteAtualizadoSalvo;
    }
    
    /**
     * Deleta um cliente
     */
    public void deletar(Long id) {
        logger.info("Deletando cliente com ID: {}", id);
        
        if (!clienteRepository.existsById(id)) {
            logger.warn("Tentativa de deletar cliente inexistente. ID: {}", id);
            throw new RuntimeException("Cliente não encontrado");
        }
        
        clienteRepository.deleteById(id);
        logger.info("Cliente deletado com sucesso. ID: {}", id);
    }
    
    /**
     * Verifica se cliente existe
     */
    @Transactional(readOnly = true)
    public boolean existe(Long id) {
        return clienteRepository.existsById(id);
    }
    
    /**
     * Conta total de clientes
     */
    @Transactional(readOnly = true)
    public long contar() {
        return clienteRepository.count();
    }
}
