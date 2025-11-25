# 🎨 InovaTech Eventos - Frontend

Frontend da aplicação de gerenciamento de eventos desenvolvido em React + TypeScript + Vite.

## 📋 Pré-requisitos

- Node.js 18+
- npm ou yarn

## ⚙️ Instalação

1. **Instale as dependências**:

   ```bash
   npm install
   ```

2. **Execute o servidor de desenvolvimento**:
   ```bash
   npm run dev
   ```

A aplicação estará disponível em: **http://localhost:5173**

## 🛠️ Tecnologias

- React 18
- TypeScript
- Vite
- TailwindCSS
- Shadcn/ui
- React Router DOM
- Context API

## 📁 Estrutura

```
src/
├── components/          # Componentes reutilizáveis
│   ├── ui/             # Componentes Shadcn/ui
│   ├── CreateEventModal.tsx
│   ├── EditEventModal.tsx
│   ├── EventCard.tsx
│   └── Navbar.tsx
├── contexts/           # Context API (AuthContext)
├── pages/              # Páginas da aplicação
│   ├── Login.tsx
│   ├── Register.tsx
│   ├── OrganizerDashboard.tsx
│   ├── ParticipantDashboard.tsx
│   ├── MyEvents.tsx
│   ├── MyEnrollments.tsx
│   └── Profile.tsx
├── services/           # Serviços de API
│   └── api.ts
├── types/              # TypeScript types
│   └── index.ts
└── App.tsx            # Componente principal
```

## 🔐 Autenticação

O frontend usa JWT Token armazenado no `localStorage` para autenticação.

## 🎯 Funcionalidades

- Login e Registro de usuários
- Dashboard diferenciado para Organizadores e Participantes
- Criação e gerenciamento de eventos (Organizadores)
- Inscrição em eventos (Participantes)
- Gerenciamento de inscrições
- Perfil do usuário

## 🔧 Scripts

- `npm run dev` - Inicia servidor de desenvolvimento
- `npm run build` - Build para produção
- `npm run preview` - Preview do build de produção

---

Design original: [Figma](https://www.figma.com/design/j6mJbK8kiSZSKmxomWSwhp/Frontend-para-Gerenciamento-de-Eventos)
