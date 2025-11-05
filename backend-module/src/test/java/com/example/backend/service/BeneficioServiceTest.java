package com.example.backend.service;

import com.example.backend.dto.BeneficioDTO;
import com.example.backend.dto.TransferRequestDTO;
import com.example.backend.entity.Beneficio;
import com.example.backend.repository.BeneficioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeneficioServiceTest {

    @Mock
    private BeneficioRepository repository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private BeneficioService service;

    private Beneficio beneficio1;
    private Beneficio beneficio2;

    @BeforeEach
    void setUp() {
        beneficio1 = new Beneficio("Beneficio A", "Descrição A", new BigDecimal("1000.00"), true);
        beneficio1.setId(1L);
        beneficio1.setVersion(0L);

        beneficio2 = new Beneficio("Beneficio B", "Descrição B", new BigDecimal("500.00"), true);
        beneficio2.setId(2L);
        beneficio2.setVersion(0L);
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(Arrays.asList(beneficio1, beneficio2));

        List<BeneficioDTO> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals("Beneficio A", result.get(0).getNome());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        when(repository.findById(1L)).thenReturn(Optional.of(beneficio1));

        BeneficioDTO result = service.findById(1L);

        assertNotNull(result);
        assertEquals("Beneficio A", result.getNome());
        assertEquals(new BigDecimal("1000.00"), result.getValor());
    }

    @Test
    void testFindByIdNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.findById(999L));
    }

    @Test
    void testCreate() {
        BeneficioDTO dto = new BeneficioDTO(null, "Novo Beneficio", "Nova Descrição", 
                                            new BigDecimal("750.00"), true, null);
        
        Beneficio savedBeneficio = new Beneficio("Novo Beneficio", "Nova Descrição", 
                                                 new BigDecimal("750.00"), true);
        savedBeneficio.setId(3L);

        when(repository.save(any(Beneficio.class))).thenReturn(savedBeneficio);

        BeneficioDTO result = service.create(dto);

        assertNotNull(result);
        assertEquals("Novo Beneficio", result.getNome());
        verify(repository, times(1)).save(any(Beneficio.class));
    }

    @Test
    void testCreateWithInvalidData() {
        BeneficioDTO dto = new BeneficioDTO(null, "", "Descrição", 
                                            new BigDecimal("100.00"), true, null);

        assertThrows(IllegalArgumentException.class, () -> service.create(dto));
    }

    @Test
    void testUpdate() {
        BeneficioDTO dto = new BeneficioDTO(1L, "Beneficio Atualizado", "Nova Descrição", 
                                            new BigDecimal("1500.00"), true, 0L);

        when(repository.findById(1L)).thenReturn(Optional.of(beneficio1));
        when(repository.save(any(Beneficio.class))).thenReturn(beneficio1);

        BeneficioDTO result = service.update(1L, dto);

        assertNotNull(result);
        verify(repository, times(1)).save(any(Beneficio.class));
    }

    @Test
    void testDelete() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void testTransferSuccess() {
        TransferRequestDTO request = new TransferRequestDTO(1L, 2L, new BigDecimal("200.00"));

        when(entityManager.find(eq(Beneficio.class), eq(1L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(beneficio1);
        when(entityManager.find(eq(Beneficio.class), eq(2L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(beneficio2);

        service.transfer(request);

        assertEquals(new BigDecimal("800.00"), beneficio1.getValor());
        assertEquals(new BigDecimal("700.00"), beneficio2.getValor());
        verify(repository, times(2)).save(any(Beneficio.class));
    }

    @Test
    void testTransferInsufficientBalance() {
        TransferRequestDTO request = new TransferRequestDTO(1L, 2L, new BigDecimal("2000.00"));

        when(entityManager.find(eq(Beneficio.class), eq(1L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(beneficio1);
        when(entityManager.find(eq(Beneficio.class), eq(2L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(beneficio2);

        assertThrows(IllegalArgumentException.class, () -> service.transfer(request));
    }

    @Test
    void testTransferSameBeneficio() {
        TransferRequestDTO request = new TransferRequestDTO(1L, 1L, new BigDecimal("100.00"));

        assertThrows(IllegalArgumentException.class, () -> service.transfer(request));
    }

    @Test
    void testTransferNegativeAmount() {
        TransferRequestDTO request = new TransferRequestDTO(1L, 2L, new BigDecimal("-100.00"));

        assertThrows(IllegalArgumentException.class, () -> service.transfer(request));
    }

    @Test
    void testTransferInactiveBeneficio() {
        beneficio1.setAtivo(false);
        TransferRequestDTO request = new TransferRequestDTO(1L, 2L, new BigDecimal("100.00"));

        when(entityManager.find(eq(Beneficio.class), eq(1L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(beneficio1);
        when(entityManager.find(eq(Beneficio.class), eq(2L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(beneficio2);

        assertThrows(IllegalArgumentException.class, () -> service.transfer(request));
    }
}
