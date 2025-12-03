# InovaTech Eventos - Sistema de Gerenciamento de Eventos

Sistema completo de gerenciamento de eventos com autenticação JWT, permitindo que organizadores criem e gerenciem eventos enquanto participantes podem se inscrever.

## 🚀 Funcionalidades

### Para Participantes
- ✅ Visualizar todos os eventos disponíveis
- ✅ Buscar eventos por título ou localização
- ✅ Inscrever-se em eventos
- ✅ Visualizar status das inscrições (Pendente, Confirmada, Cancelada)
- ✅ Cancelar inscrições pendentes
- ✅ Gerenciar perfil pessoal

### Para Organizadores
- ✅ Criar novos eventos
- ✅ Editar eventos criados
- ✅ Deletar eventos
- ✅ Visualizar todas as inscrições de um evento
- ✅ Confirmar ou rejeitar inscrições pendentes
- ✅ Ver estatísticas de participantes
- ✅ Gerenciar perfil pessoal

## 🛠️ Tecnologias Utilizadas

- **React 18** - Biblioteca JavaScript para construção de interfaces
- **TypeScript** - Superset tipado do JavaScript
- **React Router** - Navegação entre páginas
- **Tailwind CSS** - Framework CSS utilitário
- **Context API** - Gerenciamento de estado global
- **date-fns** - Manipulação e formatação de datas
- **Sonner** - Notificações toast
- **Lucide React** - Ícones

## 📋 Pré-requisitos

Antes de iniciar, certifique-se de que o backend Java Spring Boot está rodando:

1. Backend rodando em `http://localhost:8080`
2. Banco de dados configurado
3. Endpoints da API funcionando corretamente

## 🔧 Configuração do Backend

Caso precise ajustar a URL da API, edite o arquivo `/services/api.ts`:

```typescript
const API_BASE_URL = 'http://localhost:8080'; // Altere aqui se necessário
```

## 🎯 Como Usar

### 1. Criar uma Conta

1. Acesse a página de registro
2. Preencha nome, email e senha
3. Escolha o tipo de conta:
   - **Participante**: Para participar de eventos
   - **Organizador**: Para criar e gerenciar eventos

### 2. Login

1. Use suas credenciais para fazer login
2. Você será redirecionado para o dashboard apropriado

### 3. Como Participante

**Ver Eventos:**
- Na página inicial, veja todos os eventos disponíveis
- Use a barra de busca para filtrar eventos

**Inscrever-se:**
- Clique em "Inscrever-se" no evento desejado
- Aguarde a confirmação do organizador

**Gerenciar Inscrições:**
- Acesse "Minhas Inscrições" no menu
- Veja o status de cada inscrição
- Cancele inscrições pendentes se necessário

### 4. Como Organizador

**Criar Evento:**
- Clique em "Criar Evento"
- Preencha título, descrição, data, localização e capacidade
- Clique em "Criar Evento"

**Gerenciar Eventos:**
- Acesse "Meus Eventos" no menu
- Edite ou delete eventos
- Clique em "Gerenciar Inscrições" para ver participantes

**Gerenciar Inscrições:**
- Veja todas as inscrições do evento
- Confirme ou rejeite inscrições pendentes
- Acompanhe estatísticas de participantes

### 5. Perfil

**Editar Perfil:**
- Clique no seu nome no canto superior direito
- Selecione "Meu Perfil"
- Edite nome, email ou senha
- Salve as alterações

## 🔐 Segurança

- Todas as rotas protegidas requerem autenticação
- Token JWT armazenado no localStorage
- Validação de senha atual para alterações
- Organizadores só podem editar seus próprios eventos
- Participantes não podem se inscrever em eventos lotados

## 📱 Design Responsivo

O sistema é totalmente responsivo e funciona em:
- 💻 Desktop
- 📱 Tablets
- 📱 Smartphones

## 🎨 Estrutura de Pastas

```
/
├── components/          # Componentes reutilizáveis
│   ├── Navbar.tsx
│   ├── EventCard.tsx
│   ├── ProtectedRoute.tsx
│   ├── CreateEventModal.tsx
│   └── EditEventModal.tsx
├── contexts/           # Contextos React
│   └── AuthContext.tsx
├── pages/             # Páginas da aplicação
│   ├── Login.tsx
│   ├── Register.tsx
│   ├── ParticipantDashboard.tsx
│   ├── OrganizerDashboard.tsx
│   ├── MyEvents.tsx
│   ├── MyEnrollments.tsx
│   ├── EventEnrollments.tsx
│   └── Profile.tsx
├── services/          # Serviços de API
│   └── api.ts
├── types/            # Tipos TypeScript
│   └── index.ts
└── App.tsx           # Componente principal
```

## 🔄 Fluxo de Dados

1. **Autenticação**: Login → Token JWT → Armazenado → Incluído em requisições
2. **Criação de Evento**: Formulário → API → Atualizar lista
3. **Inscrição**: Botão → API → Status Pendente → Organizador confirma
4. **Confirmação**: Organizador → API → Status Confirmado → Participante notificado

## 🐛 Tratamento de Erros

- Mensagens de erro amigáveis com toast notifications
- Validação de formulários no frontend
- Tratamento de erros de rede
- Feedback visual para ações do usuário

## 📊 Status de Inscrição

- **PENDING** (Amarelo): Aguardando confirmação do organizador
- **CONFIRMED** (Verde): Inscrição confirmada pelo organizador
- **CANCELED** (Vermelho): Inscrição cancelada

## 🔗 Integração com Backend

O sistema está configurado para se conectar com a API REST Spring Boot nos seguintes endpoints:

### Autenticação
- POST `/auth/login` - Login
- POST `/users` - Criar usuário

### Usuários
- GET `/users/{id}` - Buscar usuário
- PUT `/users/{id}` - Atualizar usuário
- POST `/users/{id}/validate-password` - Validar senha

### Eventos
- GET `/events` - Listar eventos (paginado)
- POST `/events` - Criar evento
- GET `/events/{id}` - Buscar evento
- PUT `/events/{id}` - Atualizar evento
- DELETE `/events/{id}` - Deletar evento
- GET `/events/search` - Buscar eventos
- GET `/events/my-events` - Meus eventos

### Inscrições
- POST `/enrollments` - Criar inscrição
- GET `/enrollments/my-enrollments` - Minhas inscrições
- GET `/enrollments/event/{eventId}` - Inscrições do evento
- PUT `/enrollments/{id}/cancel` - Cancelar inscrição
- PUT `/enrollments/{id}/confirm` - Confirmar inscrição
- PUT `/enrollments/{id}/reject` - Rejeitar inscrição

## 📝 Observações Importantes

1. Certifique-se de que o backend está rodando antes de usar o frontend
2. As datas são formatadas no padrão brasileiro (PT-BR)
3. Senhas devem ter no mínimo 6 caracteres
4. Eventos passados ainda aparecem em "Meus Eventos" e "Minhas Inscrições"
5. A busca funciona por título e localização do evento

## 🆘 Suporte

Em caso de problemas:
1. Verifique se o backend está rodando
2. Verifique a URL da API em `/services/api.ts`
3. Abra o console do navegador para ver erros
4. Verifique as mensagens de erro do toast
