package com.example.backend.service;

import com.example.backend.dto.BeneficioDTO;
import com.example.backend.dto.TransferRequestDTO;
import com.example.backend.entity.Beneficio;
import com.example.backend.repository.BeneficioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BeneficioService {

    @Autowired
    private BeneficioRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<BeneficioDTO> findAll() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<BeneficioDTO> findAllActive() {
        return repository.findByAtivoTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public BeneficioDTO findById(Long id) {
        Beneficio beneficio = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Beneficio not found: " + id));
        return toDTO(beneficio);
    }

    @Transactional
    public BeneficioDTO create(BeneficioDTO dto) {
        validateBeneficioDTO(dto);
        Beneficio beneficio = toEntity(dto);
        beneficio = repository.save(beneficio);
        return toDTO(beneficio);
    }

    @Transactional
    public BeneficioDTO update(Long id, BeneficioDTO dto) {
        validateBeneficioDTO(dto);
        Beneficio beneficio = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Beneficio not found: " + id));
        
        beneficio.setNome(dto.getNome());
        beneficio.setDescricao(dto.getDescricao());
        beneficio.setValor(dto.getValor());
        beneficio.setAtivo(dto.getAtivo());
        
        beneficio = repository.save(beneficio);
        return toDTO(beneficio);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Beneficio not found: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public void transfer(TransferRequestDTO request) {
        // Validate input
        if (request.getFromId() == null || request.getToId() == null) {
            throw new IllegalArgumentException("IDs cannot be null");
        }
        
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        if (request.getFromId().equals(request.getToId())) {
            throw new IllegalArgumentException("Cannot transfer to the same beneficio");
        }

        // Use pessimistic locking to prevent concurrent modifications
        Beneficio from = entityManager.find(Beneficio.class, request.getFromId(), LockModeType.PESSIMISTIC_WRITE);
        Beneficio to = entityManager.find(Beneficio.class, request.getToId(), LockModeType.PESSIMISTIC_WRITE);

        // Validate entities exist
        if (from == null) {
            throw new IllegalArgumentException("Source beneficio not found: " + request.getFromId());
        }
        
        if (to == null) {
            throw new IllegalArgumentException("Destination beneficio not found: " + request.getToId());
        }

        // Validate both beneficios are active
        if (!from.getAtivo()) {
            throw new IllegalArgumentException("Source beneficio is not active");
        }
        
        if (!to.getAtivo()) {
            throw new IllegalArgumentException("Destination beneficio is not active");
        }

        // Validate sufficient balance
        if (from.getValor().compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException(
                String.format("Insufficient balance. Available: %s, Required: %s", 
                    from.getValor(), request.getAmount())
            );
        }

        // Perform the transfer
        from.setValor(from.getValor().subtract(request.getAmount()));
        to.setValor(to.getValor().add(request.getAmount()));

        repository.save(from);
        repository.save(to);
    }

    private void validateBeneficioDTO(BeneficioDTO dto) {
        if (dto.getNome() == null || dto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome is required");
        }
        if (dto.getValor() == null || dto.getValor().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor must be non-negative");
        }
    }

    private BeneficioDTO toDTO(Beneficio entity) {
        return new BeneficioDTO(
            entity.getId(),
            entity.getNome(),
            entity.getDescricao(),
            entity.getValor(),
            entity.getAtivo(),
            entity.getVersion()
        );
    }

    private Beneficio toEntity(BeneficioDTO dto) {
        Beneficio entity = new Beneficio();
        entity.setNome(dto.getNome());
        entity.setDescricao(dto.getDescricao());
        entity.setValor(dto.getValor());
        entity.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        return entity;
    }
}
