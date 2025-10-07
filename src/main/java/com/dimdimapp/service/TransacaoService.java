package com.dimdimapp.service;

import com.dimdimapp.model.Transacao;
import com.dimdimapp.repository.TransacaoRepository;
import com.dimdimapp.repository.ClienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TransacaoService {
    
    private static final Logger logger = LoggerFactory.getLogger(TransacaoService.class);
    
    private final TransacaoRepository transacaoRepository;
    private final ClienteRepository clienteRepository;
    
    @Autowired
    public TransacaoService(TransacaoRepository transacaoRepository, ClienteRepository clienteRepository) {
        this.transacaoRepository = transacaoRepository;
        this.clienteRepository = clienteRepository;
    }
    
    /**
     * Lista todas as transações
     */
    @Transactional(readOnly = true)
    public List<Transacao> listarTodas() {
        logger.info("Listando todas as transações");
        return transacaoRepository.findAll();
    }
    
    /**
     * Busca transação por ID
     */
    @Transactional(readOnly = true)
    public Optional<Transacao> buscarPorId(Long id) {
        logger.info("Buscando transação com ID: {}", id);
        return transacaoRepository.findById(id);
    }
    
    /**
     * Busca transações por cliente
     */
    @Transactional(readOnly = true)
    public List<Transacao> buscarPorCliente(Long clienteId) {
        logger.info("Buscando transações do cliente ID: {}", clienteId);
        return transacaoRepository.findByClienteId(clienteId);
    }
    
    /**
     * Busca transações por data
     */
    @Transactional(readOnly = true)
    public List<Transacao> buscarPorData(LocalDate data) {
        logger.info("Buscando transações da data: {}", data);
        return transacaoRepository.findByData(data);
    }
    
    /**
     * Busca transações por período
     */
    @Transactional(readOnly = true)
    public List<Transacao> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        logger.info("Buscando transações do período: {} a {}", dataInicio, dataFim);
        return transacaoRepository.findByDataBetween(dataInicio, dataFim);
    }
    
    /**
     * Busca transações por cliente e período
     */
    @Transactional(readOnly = true)
    public List<Transacao> buscarPorClienteEPeriodo(Long clienteId, LocalDate dataInicio, LocalDate dataFim) {
        logger.info("Buscando transações do cliente {} no período: {} a {}", clienteId, dataInicio, dataFim);
        return transacaoRepository.findByClienteIdAndDataBetween(clienteId, dataInicio, dataFim);
    }
    
    /**
     * Salva uma nova transação
     */
    public Transacao salvar(Transacao transacao) {
        logger.info("Salvando nova transação de valor: {} para cliente ID: {}", 
                   transacao.getValor(), transacao.getCliente().getId());
        
        // Verifica se o cliente existe
        if (!clienteRepository.existsById(transacao.getCliente().getId())) {
            logger.warn("Tentativa de salvar transação para cliente inexistente. Cliente ID: {}", 
                       transacao.getCliente().getId());
            throw new RuntimeException("Cliente não encontrado");
        }
        
        // Se a data não foi informada, usa a data atual
        if (transacao.getData() == null) {
            transacao.setData(LocalDate.now());
            logger.info("Data não informada, usando data atual: {}", transacao.getData());
        }
        
        Transacao transacaoSalva = transacaoRepository.save(transacao);
        logger.info("Transação salva com sucesso. ID: {}", transacaoSalva.getId());
        return transacaoSalva;
    }
    
    /**
     * Atualiza uma transação existente
     */
    public Transacao atualizar(Long id, Transacao transacaoAtualizada) {
        logger.info("Atualizando transação com ID: {}", id);
        
        Transacao transacaoExistente = transacaoRepository.findById(id)
            .orElseThrow(() -> {
                logger.warn("Transação não encontrada para atualização. ID: {}", id);
                return new RuntimeException("Transação não encontrada");
            });
        
        // Verifica se o cliente existe (se foi alterado)
        if (!transacaoExistente.getCliente().getId().equals(transacaoAtualizada.getCliente().getId()) &&
            !clienteRepository.existsById(transacaoAtualizada.getCliente().getId())) {
            logger.warn("Tentativa de atualizar transação para cliente inexistente. Cliente ID: {}", 
                       transacaoAtualizada.getCliente().getId());
            throw new RuntimeException("Cliente não encontrado");
        }
        
        // Atualiza os campos
        transacaoExistente.setValor(transacaoAtualizada.getValor());
        transacaoExistente.setData(transacaoAtualizada.getData());
        transacaoExistente.setCliente(transacaoAtualizada.getCliente());
        
        Transacao transacaoAtualizadaSalva = transacaoRepository.save(transacaoExistente);
        logger.info("Transação atualizada com sucesso. ID: {}", transacaoAtualizadaSalva.getId());
        return transacaoAtualizadaSalva;
    }
    
    /**
     * Deleta uma transação
     */
    public void deletar(Long id) {
        logger.info("Deletando transação com ID: {}", id);
        
        if (!transacaoRepository.existsById(id)) {
            logger.warn("Tentativa de deletar transação inexistente. ID: {}", id);
            throw new RuntimeException("Transação não encontrada");
        }
        
        transacaoRepository.deleteById(id);
        logger.info("Transação deletada com sucesso. ID: {}", id);
    }
    
    /**
     * Calcula total das transações de um cliente
     */
    @Transactional(readOnly = true)
    public Double calcularTotalPorCliente(Long clienteId) {
        logger.info("Calculando total das transações do cliente ID: {}", clienteId);
        return transacaoRepository.calcularTotalPorCliente(clienteId);
    }
    
    /**
     * Conta número de transações de um cliente
     */
    @Transactional(readOnly = true)
    public Long contarPorCliente(Long clienteId) {
        logger.info("Contando transações do cliente ID: {}", clienteId);
        return transacaoRepository.countByClienteId(clienteId);
    }
    
    /**
     * Verifica se transação existe
     */
    @Transactional(readOnly = true)
    public boolean existe(Long id) {
        return transacaoRepository.existsById(id);
    }
    
    /**
     * Conta total de transações
     */
    @Transactional(readOnly = true)
    public long contar() {
        return transacaoRepository.count();
    }
}
