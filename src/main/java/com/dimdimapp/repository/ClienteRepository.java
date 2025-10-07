package com.dimdimapp.repository;

import com.dimdimapp.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    /**
     * Busca cliente por email
     */
    Optional<Cliente> findByEmail(String email);
    
    /**
     * Busca clientes por nome (case insensitive)
     */
    List<Cliente> findByNomeContainingIgnoreCase(String nome);
    
    /**
     * Busca clientes que possuem transações
     */
    @Query("SELECT DISTINCT c FROM Cliente c JOIN c.transacoes t")
    List<Cliente> findClientesComTransacoes();
    
    /**
     * Busca cliente por telefone
     */
    Optional<Cliente> findByTelefone(String telefone);
    
    /**
     * Verifica se existe cliente com email (excluindo o próprio ID)
     */
    @Query("SELECT COUNT(c) > 0 FROM Cliente c WHERE c.email = :email AND c.id != :id")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") Long id);
}
