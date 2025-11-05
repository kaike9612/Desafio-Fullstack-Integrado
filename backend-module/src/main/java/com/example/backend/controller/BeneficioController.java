package com.example.backend.controller;

import com.example.backend.dto.BeneficioDTO;
import com.example.backend.dto.TransferRequestDTO;
import com.example.backend.service.BeneficioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/beneficios")
@CrossOrigin(origins = "*")
@Tag(name = "Beneficios", description = "API para gerenciamento de benefícios")
public class BeneficioController {

    @Autowired
    private BeneficioService service;

    @GetMapping
    @Operation(summary = "Listar todos os benefícios", description = "Retorna lista completa de benefícios")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<BeneficioDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/ativos")
    @Operation(summary = "Listar benefícios ativos", description = "Retorna apenas benefícios com status ativo")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<BeneficioDTO>> findAllActive() {
        return ResponseEntity.ok(service.findAllActive());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar benefício por ID", description = "Retorna um benefício específico pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Benefício encontrado"),
        @ApiResponse(responseCode = "404", description = "Benefício não encontrado")
    })
    public ResponseEntity<BeneficioDTO> findById(
            @Parameter(description = "ID do benefício") @PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.findById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Criar novo benefício", description = "Cria um novo benefício no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Benefício criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<?> create(@RequestBody BeneficioDTO dto) {
        try {
            BeneficioDTO created = service.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar benefício", description = "Atualiza um benefício existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Benefício atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Benefício não encontrado")
    })
    public ResponseEntity<?> update(
            @Parameter(description = "ID do benefício") @PathVariable Long id,
            @RequestBody BeneficioDTO dto) {
        try {
            BeneficioDTO updated = service.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar benefício", description = "Remove um benefício do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Benefício deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Benefício não encontrado")
    })
    public ResponseEntity<?> delete(
            @Parameter(description = "ID do benefício") @PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/transferir")
    @Operation(summary = "Transferir valor entre benefícios", 
               description = "Transfere valor de um benefício para outro com validação de saldo e locking")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou saldo insuficiente")
    })
    public ResponseEntity<?> transfer(@RequestBody TransferRequestDTO request) {
        try {
            service.transfer(request);
            return ResponseEntity.ok().body("Transferência realizada com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
