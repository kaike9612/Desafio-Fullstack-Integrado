# Documentação da API - Sistema de Benefícios

## Base URL

\`\`\`
http://localhost:8080/api/v1
\`\`\`

## Autenticação

Atualmente a API não requer autenticação. Em produção, implementar JWT ou OAuth2.

## Endpoints

### 1. Listar Todos os Benefícios

**GET** `/beneficios`

Retorna lista completa de benefícios cadastrados.

**Response 200 OK:**
\`\`\`json
[
  {
    "id": 1,
    "nome": "Beneficio A",
    "descricao": "Descrição A",
    "valor": 1000.00,
    "ativo": true,
    "version": 0
  },
  {
    "id": 2,
    "nome": "Beneficio B",
    "descricao": "Descrição B",
    "valor": 500.00,
    "ativo": true,
    "version": 0
  }
]
\`\`\`

### 2. Listar Benefícios Ativos

**GET** `/beneficios/ativos`

Retorna apenas benefícios com status ativo.

**Response 200 OK:** (mesmo formato acima, filtrado)

### 3. Buscar Benefício por ID

**GET** `/beneficios/{id}`

**Path Parameters:**
- `id` (Long): ID do benefício

**Response 200 OK:**
\`\`\`json
{
  "id": 1,
  "nome": "Beneficio A",
  "descricao": "Descrição A",
  "valor": 1000.00,
  "ativo": true,
  "version": 0
}
\`\`\`

**Response 404 Not Found:**
\`\`\`json
{
  "timestamp": "2025-01-15T10:30:00",
  "message": "Beneficio not found: 999",
  "status": 404
}
\`\`\`

### 4. Criar Novo Benefício

**POST** `/beneficios`

**Request Body:**
\`\`\`json
{
  "nome": "Novo Beneficio",
  "descricao": "Descrição do novo benefício",
  "valor": 750.00,
  "ativo": true
}
\`\`\`

**Validações:**
- `nome`: obrigatório, não vazio
- `valor`: obrigatório, >= 0
- `ativo`: opcional, default true

**Response 201 Created:**
\`\`\`json
{
  "id": 3,
  "nome": "Novo Beneficio",
  "descricao": "Descrição do novo benefício",
  "valor": 750.00,
  "ativo": true,
  "version": 0
}
\`\`\`

**Response 400 Bad Request:**
\`\`\`json
{
  "timestamp": "2025-01-15T10:30:00",
  "message": "Nome is required",
  "status": 400
}
\`\`\`

### 5. Atualizar Benefício

**PUT** `/beneficios/{id}`

**Path Parameters:**
- `id` (Long): ID do benefício

**Request Body:**
\`\`\`json
{
  "nome": "Beneficio Atualizado",
  "descricao": "Nova descrição",
  "valor": 1500.00,
  "ativo": true
}
\`\`\`

**Response 200 OK:** (benefício atualizado)

**Response 404 Not Found:** (benefício não existe)

### 6. Deletar Benefício

**DELETE** `/beneficios/{id}`

**Path Parameters:**
- `id` (Long): ID do benefício

**Response 204 No Content:** (sucesso)

**Response 404 Not Found:** (benefício não existe)

### 7. Transferir Valor

**POST** `/beneficios/transferir`

Transfere valor de um benefício para outro com validação de saldo e locking.

**Request Body:**
\`\`\`json
{
  "fromId": 1,
  "toId": 2,
  "amount": 200.00
}
\`\`\`

**Validações:**
- IDs não podem ser nulos
- IDs devem ser diferentes
- Amount deve ser positivo
- Benefício origem deve ter saldo suficiente
- Ambos benefícios devem estar ativos
- Ambos benefícios devem existir

**Response 200 OK:**
\`\`\`json
"Transferência realizada com sucesso"
\`\`\`

**Response 400 Bad Request:**
\`\`\`json
{
  "timestamp": "2025-01-15T10:30:00",
  "message": "Insufficient balance. Available: 1000.00, Required: 2000.00",
  "status": 400
}
\`\`\`

## Códigos de Status HTTP

| Código | Descrição |
|--------|-----------|
| 200 | OK - Requisição bem-sucedida |
| 201 | Created - Recurso criado com sucesso |
| 204 | No Content - Recurso deletado com sucesso |
| 400 | Bad Request - Dados inválidos |
| 404 | Not Found - Recurso não encontrado |
| 500 | Internal Server Error - Erro no servidor |

## Modelos de Dados

### Beneficio

\`\`\`typescript
{
  id?: number;           // Gerado automaticamente
  nome: string;          // Obrigatório, max 100 caracteres
  descricao?: string;    // Opcional, max 255 caracteres
  valor: number;         // Obrigatório, decimal(15,2)
  ativo: boolean;        // Default: true
  version?: number;      // Controle de versão (optimistic locking)
}
\`\`\`

### TransferRequest

\`\`\`typescript
{
  fromId: number;        // ID do benefício origem
  toId: number;          // ID do benefício destino
  amount: number;        // Valor a transferir
}
\`\`\`

## Exemplos com cURL

### Listar todos
\`\`\`bash
curl -X GET http://localhost:8080/api/v1/beneficios
\`\`\`

### Criar novo
\`\`\`bash
curl -X POST http://localhost:8080/api/v1/beneficios \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Beneficio C",
    "descricao": "Novo benefício",
    "valor": 800.00,
    "ativo": true
  }'
\`\`\`

### Transferir
\`\`\`bash
curl -X POST http://localhost:8080/api/v1/beneficios/transferir \
  -H "Content-Type: application/json" \
  -d '{
    "fromId": 1,
    "toId": 2,
    "amount": 150.00
  }'
\`\`\`

## Swagger UI

Para documentação interativa e testes, acesse:

\`\`\`
http://localhost:8080/swagger-ui.html
\`\`\`

## Tratamento de Erros

Todos os erros retornam um objeto padronizado:

\`\`\`json
{
  "timestamp": "2025-01-15T10:30:00",
  "message": "Mensagem de erro descritiva",
  "status": 400,
  "details": "Detalhes adicionais (opcional)"
}
\`\`\`

## Considerações de Performance

- Pessimistic locking usado em transferências para garantir consistência
- Índices no banco de dados para queries otimizadas
- Connection pooling configurado
- Cache de segundo nível pode ser adicionado para leituras frequentes

## Versionamento

API versão 1.0 (v1)

Mudanças futuras serão versionadas (v2, v3, etc.) mantendo retrocompatibilidade.
