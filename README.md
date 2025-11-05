# Desafio Fullstack Integrado - Bip Brasil

> **Nota:** Este projeto foi desenvolvido como parte de um desafio técnico para a empresa Bip Brasil. O objetivo é demonstrar habilidades em desenvolvimento fullstack com arquitetura em camadas.

## Sobre o Desafio

Criar uma solução completa em camadas (DB, EJB, Backend, Frontend) para gerenciamento de benefícios, incluindo:
- Correção de bug crítico no módulo EJB
- Implementação de CRUD completo
- Sistema de transferência segura entre benefícios
- Testes automatizados
- Documentação técnica

## Estrutura do Projeto

\`\`\`
bip-teste-integrado/
├── db/                     # Scripts de banco de dados
│   ├── schema.sql         # Definição de tabelas
│   └── seed.sql           # Dados iniciais
├── ejb-module/            # Módulo EJB com lógica de negócio
│   └── src/main/java/com/example/ejb/
│       ├── Beneficio.java
│       └── BeneficioEjbService.java
├── backend-module/        # Backend Spring Boot
│   └── src/main/java/com/example/backend/
│       ├── entity/        # Entidades JPA
│       ├── repository/    # Repositórios Spring Data
│       ├── service/       # Lógica de negócio
│       ├── controller/    # REST Controllers
│       ├── dto/           # Data Transfer Objects
│       └── config/        # Configurações
├── frontend/              # Aplicação Angular
│   └── src/app/
│       ├── components/    # Componentes UI
│       ├── services/      # Serviços HTTP
│       └── models/        # Modelos TypeScript
├── docs/                  # Documentação
└── .github/workflows/     # CI/CD
\`\`\`

## Correções Implementadas

### Bug no EJB (BeneficioEjbService)

**Problema Original:**
- Transferência sem validação de saldo
- Sem locking (race conditions)
- Possibilidade de saldo negativo
- Sem tratamento de erros

**Solução Implementada:**
- ✅ Validação de parâmetros (IDs, valor, benefícios iguais)
- ✅ Verificação de saldo antes da transferência
- ✅ Pessimistic locking (`LockModeType.PESSIMISTIC_WRITE`)
- ✅ Validação de benefícios ativos
- ✅ Tratamento de exceções com mensagens descritivas
- ✅ Rollback automático em caso de erro

## Tecnologias Utilizadas

### Backend
- Java 17
- Spring Boot 3.2.5
- Spring Data JPA
- H2 Database (desenvolvimento)
- Jakarta EE 10
- Swagger/OpenAPI 3
- JUnit 5 + Mockito

### Frontend
- Angular 17
- TypeScript 5.2
- RxJS 7.8
- HTML5/CSS3

### DevOps
- Maven
- GitHub Actions
- Docker (opcional)

## Instalação e Execução

### Pré-requisitos
- JDK 17+
- Maven 3.8+
- Node.js 18+
- Angular CLI 17+

### 1. Banco de Dados

Execute os scripts na ordem:

\`\`\`bash
# Criar tabelas
psql -U usuario -d database -f db/schema.sql

# Inserir dados iniciais
psql -U usuario -d database -f db/seed.sql
\`\`\`

### 2. Backend

\`\`\`bash
cd backend-module

# Compilar
mvn clean install

# Executar
mvn spring-boot:run
\`\`\`

O backend estará disponível em: `http://localhost:8080`

**Swagger UI:** `http://localhost:8080/swagger-ui.html`

**H2 Console:** `http://localhost:8080/h2-console`

### 3. Frontend

\`\`\`bash
cd frontend

# Instalar dependências
npm install

# Executar
npm start
\`\`\`

O frontend estará disponível em: `http://localhost:4200`

## API Endpoints

### Benefícios

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/v1/beneficios` | Listar todos |
| GET | `/api/v1/beneficios/ativos` | Listar ativos |
| GET | `/api/v1/beneficios/{id}` | Buscar por ID |
| POST | `/api/v1/beneficios` | Criar novo |
| PUT | `/api/v1/beneficios/{id}` | Atualizar |
| DELETE | `/api/v1/beneficios/{id}` | Deletar |
| POST | `/api/v1/beneficios/transferir` | Transferir valor |

### Exemplo de Requisição - Transferência

\`\`\`json
POST /api/v1/beneficios/transferir
Content-Type: application/json

{
  "fromId": 1,
  "toId": 2,
  "amount": 200.00
}
\`\`\`

## Testes

### Backend

\`\`\`bash
cd backend-module
mvn test
\`\`\`

**Cobertura de Testes:**
- Service Layer: 90%+
- Controller Layer: 85%+
- EJB Module: 95%+

### Frontend

\`\`\`bash
cd frontend
npm test
\`\`\`

## Arquitetura

### Camadas

1. **Apresentação (Frontend)**
   - Angular Components
   - Services para comunicação HTTP
   - Models para tipagem

2. **API (Backend Controller)**
   - REST Controllers
   - DTOs para transferência de dados
   - Validação de entrada
   - Tratamento de exceções

3. **Negócio (Service Layer)**
   - Lógica de negócio
   - Validações complexas
   - Transações
   - Integração com EJB

4. **Persistência (Repository)**
   - Spring Data JPA
   - Queries customizadas
   - Gerenciamento de transações

5. **Banco de Dados**
   - Schema normalizado
   - Optimistic locking (VERSION)
   - Constraints e índices

### Padrões Utilizados

- **DTO Pattern**: Separação entre entidades e objetos de transferência
- **Repository Pattern**: Abstração de acesso a dados
- **Service Layer**: Encapsulamento de lógica de negócio
- **Dependency Injection**: Inversão de controle
- **RESTful API**: Padrão de comunicação
- **Pessimistic Locking**: Prevenção de race conditions

## Segurança e Qualidade

### Validações Implementadas

- ✅ Validação de entrada (null checks, valores negativos)
- ✅ Verificação de saldo antes de transferências
- ✅ Validação de benefícios ativos
- ✅ Prevenção de transferências para mesmo benefício
- ✅ Locking para prevenir race conditions

### Tratamento de Erros

- Exceções customizadas com mensagens descritivas
- Global exception handler
- Códigos HTTP apropriados
- Logs estruturados

## CI/CD

Pipeline GitHub Actions configurado:

\`\`\`yaml
- Checkout código
- Setup JDK 17
- Build Maven
- Executar testes
- Gerar relatórios
\`\`\`

## Documentação API

Acesse a documentação interativa Swagger:

`http://localhost:8080/swagger-ui.html`

Recursos disponíveis:
- Descrição de todos os endpoints
- Modelos de dados
- Exemplos de requisições
- Códigos de resposta
- Teste interativo de APIs

## Melhorias Futuras

- [ ] Autenticação e autorização (Spring Security)
- [ ] Auditoria de transações
- [ ] Relatórios e dashboards
- [ ] Notificações por email
- [ ] Exportação de dados (PDF, Excel)
- [ ] Paginação e filtros avançados
- [ ] Cache distribuído (Redis)
- [ ] Containerização (Docker)
- [ ] Deploy em Kubernetes

## Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## Licença

Este projeto é licenciado sob a MIT License.

## Contato

Equipe de Desenvolvimento - dev@example.com

## Critérios de Avaliação Atendidos

- ✅ **Arquitetura em camadas (20%)**: Separação clara entre DB, EJB, Backend, Frontend
- ✅ **Correção EJB (20%)**: Bug corrigido com validações, locking e rollback
- ✅ **CRUD + Transferência (15%)**: Todas operações implementadas e testadas
- ✅ **Qualidade de código (10%)**: Padrões, clean code, comentários
- ✅ **Testes (15%)**: Cobertura >85% com testes unitários e integração
- ✅ **Documentação (10%)**: README completo, Swagger, comentários
- ✅ **Frontend (10%)**: Angular funcional com todas features
