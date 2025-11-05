# Frontend Angular - Sistema de Benefícios

## Descrição
Aplicação Angular para gerenciamento de benefícios com operações CRUD e transferências.

## Pré-requisitos
- Node.js 18+
- Angular CLI 17+

## Instalação

\`\`\`bash
npm install
\`\`\`

## Executar

\`\`\`bash
npm start
\`\`\`

A aplicação estará disponível em `http://localhost:4200`

## Build

\`\`\`bash
npm run build
\`\`\`

## Funcionalidades

- Listar todos os benefícios
- Criar novo benefício
- Editar benefício existente
- Deletar benefício
- Transferir valor entre benefícios com validação

## Estrutura

\`\`\`
src/
├── app/
│   ├── components/
│   │   └── beneficio-list/
│   ├── models/
│   ├── services/
│   ├── app.component.ts
│   └── app.routes.ts
├── index.html
├── main.ts
└── styles.css
