package com.example.ejb;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.OptimisticLockException;
import java.math.BigDecimal;

@Stateless
public class BeneficioEjbService {

    @PersistenceContext
    private EntityManager em;

    /**
     * Transfers amount from one Beneficio to another with proper validation and locking.
     * 
     * @param fromId Source beneficio ID
     * @param toId Destination beneficio ID
     * @param amount Amount to transfer
     * @throws IllegalArgumentException if validation fails
     * @throws OptimisticLockException if concurrent modification detected
     */
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        
        // Validate input parameters
        if (fromId == null || toId == null) {
            throw new IllegalArgumentException("IDs cannot be null");
        }
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Cannot transfer to the same beneficio");
        }

        // Use pessimistic locking to prevent concurrent modifications
        Beneficio from = em.find(Beneficio.class, fromId, LockModeType.PESSIMISTIC_WRITE);
        Beneficio to = em.find(Beneficio.class, toId, LockModeType.PESSIMISTIC_WRITE);

        // Validate entities exist
        if (from == null) {
            throw new IllegalArgumentException("Source beneficio not found: " + fromId);
        }
        
        if (to == null) {
            throw new IllegalArgumentException("Destination beneficio not found: " + toId);
        }

        // Validate both beneficios are active
        if (!from.getAtivo()) {
            throw new IllegalArgumentException("Source beneficio is not active");
        }
        
        if (!to.getAtivo()) {
            throw new IllegalArgumentException("Destination beneficio is not active");
        }

        // Validate sufficient balance
        if (from.getValor().compareTo(amount) < 0) {
            throw new IllegalArgumentException(
                String.format("Insufficient balance. Available: %s, Required: %s", 
                    from.getValor(), amount)
            );
        }

        // Perform the transfer
        from.setValor(from.getValor().subtract(amount));
        to.setValor(to.getValor().add(amount));

        // EntityManager will automatically merge due to managed entities
        // Transaction will rollback automatically if any exception occurs
    }
}
