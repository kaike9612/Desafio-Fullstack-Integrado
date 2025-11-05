package com.example.ejb;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeneficioEjbServiceTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private BeneficioEjbService service;

    private Beneficio beneficio1;
    private Beneficio beneficio2;

    @BeforeEach
    void setUp() {
        beneficio1 = new Beneficio("Beneficio A", "Descrição A", new BigDecimal("1000.00"), true);
        beneficio1.setId(1L);

        beneficio2 = new Beneficio("Beneficio B", "Descrição B", new BigDecimal("500.00"), true);
        beneficio2.setId(2L);
    }

    @Test
    void testTransferSuccess() {
        when(entityManager.find(eq(Beneficio.class), eq(1L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(beneficio1);
        when(entityManager.find(eq(Beneficio.class), eq(2L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(beneficio2);

        service.transfer(1L, 2L, new BigDecimal("200.00"));

        assertEquals(new BigDecimal("800.00"), beneficio1.getValor());
        assertEquals(new BigDecimal("700.00"), beneficio2.getValor());
    }

    @Test
    void testTransferInsufficientBalance() {
        when(entityManager.find(eq(Beneficio.class), eq(1L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(beneficio1);
        when(entityManager.find(eq(Beneficio.class), eq(2L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(beneficio2);

        assertThrows(IllegalArgumentException.class, 
            () -> service.transfer(1L, 2L, new BigDecimal("2000.00")));
    }

    @Test
    void testTransferNullIds() {
        assertThrows(IllegalArgumentException.class, 
            () -> service.transfer(null, 2L, new BigDecimal("100.00")));
        
        assertThrows(IllegalArgumentException.class, 
            () -> service.transfer(1L, null, new BigDecimal("100.00")));
    }

    @Test
    void testTransferNegativeAmount() {
        assertThrows(IllegalArgumentException.class, 
            () -> service.transfer(1L, 2L, new BigDecimal("-100.00")));
    }

    @Test
    void testTransferSameBeneficio() {
        assertThrows(IllegalArgumentException.class, 
            () -> service.transfer(1L, 1L, new BigDecimal("100.00")));
    }

    @Test
    void testTransferBeneficioNotFound() {
        when(entityManager.find(eq(Beneficio.class), eq(999L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(null);

        assertThrows(IllegalArgumentException.class, 
            () -> service.transfer(999L, 2L, new BigDecimal("100.00")));
    }

    @Test
    void testTransferInactiveBeneficio() {
        beneficio1.setAtivo(false);
        
        when(entityManager.find(eq(Beneficio.class), eq(1L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(beneficio1);
        when(entityManager.find(eq(Beneficio.class), eq(2L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(beneficio2);

        assertThrows(IllegalArgumentException.class, 
            () -> service.transfer(1L, 2L, new BigDecimal("100.00")));
    }
}
