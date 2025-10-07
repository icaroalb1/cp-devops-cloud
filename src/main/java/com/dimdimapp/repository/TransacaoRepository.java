package com.dimdimapp.repository;

import com.dimdimapp.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    
    /**
     * Busca transações por cliente
     */
    List<Transacao> findByClienteId(Long clienteId);
    
    /**
     * Busca transações por data
     */
    List<Transacao> findByData(LocalDate data);
    
    /**
     * Busca transações por período
     */
    List<Transacao> findByDataBetween(LocalDate dataInicio, LocalDate dataFim);
    
    /**
     * Busca transações por valor mínimo
     */
    List<Transacao> findByValorGreaterThanEqual(Double valorMinimo);
    
    /**
     * Busca transações por valor máximo
     */
    List<Transacao> findByValorLessThanEqual(Double valorMaximo);
    
    /**
     * Busca transações por cliente e período
     */
    @Query("SELECT t FROM Transacao t WHERE t.cliente.id = :clienteId AND t.data BETWEEN :dataInicio AND :dataFim")
    List<Transacao> findByClienteIdAndDataBetween(@Param("clienteId") Long clienteId, 
                                                  @Param("dataInicio") LocalDate dataInicio, 
                                                  @Param("dataFim") LocalDate dataFim);
    
    /**
     * Calcula soma total das transações de um cliente
     */
    @Query("SELECT COALESCE(SUM(t.valor), 0) FROM Transacao t WHERE t.cliente.id = :clienteId")
    Double calcularTotalPorCliente(@Param("clienteId") Long clienteId);
    
    /**
     * Conta número de transações de um cliente
     */
    Long countByClienteId(Long clienteId);
}
