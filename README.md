# Task Manager

API de gerenciamento de tarefas (To-Do) com arquitetura hexagonal, event-driven via Kafka e frontend React + TypeScript.

---

## Guia Completo de Execução

### Pré-requisitos

- Docker + Docker Compose v2
- `curl` ou navegador para testar a API
- Java 21 + Maven 3.9 *(somente para rodar fora do Docker)*
- Node.js 20+ *(somente para rodar fora do Docker)*

---

### Passo 1 — Iniciar o Docker

O Docker daemon precisa estar rodando antes de qualquer coisa.

```bash
# Iniciar o serviço Docker
sudo systemctl start docker

# Verificar se está ativo
sudo systemctl status docker
# A linha "Active:" deve mostrar "active (running)"
```

Para não precisar usar `sudo` no Docker (opcional, recomendado):

```bash
sudo usermod -aG docker $USER
newgrp docker   # aplica sem precisar relogar
```

Para que o Docker inicie automaticamente com o sistema:

```bash
sudo systemctl enable docker
```

---

### Passo 2 — Clonar / Acessar o projeto

```bash
cd /home/lhuan/Documents/entrevista
```

---

### Passo 3 — Subir a aplicação

```bash
docker compose up --build
```

O que acontece:
- **Build** das imagens da API (Maven multi-stage) e do Frontend (Node + nginx)
- **Inicialização** na ordem: PostgreSQL → Zookeeper → Kafka → API → Frontend
- **Flyway** executa a migration `V1__create_tasks_table.sql` automaticamente
- Na primeira vez leva ~3-5 minutos (download das imagens + build)

Para rodar em background (sem travar o terminal):

```bash
docker compose up --build -d
```

---

### Passo 4 — Verificar que tudo subiu

```bash
# Ver status dos containers
docker compose ps

# Todos devem estar com status "running" ou "Up"
```

Testar o health check da API:

```bash
curl http://localhost:8080/actuator/health
```

Resposta esperada:
```json
{"status":"UP","components":{"db":{"status":"UP"},"kafka":{"status":"UP"}}}
```

---

### Passo 5 — Acessar o Frontend

Abra o navegador em: **http://localhost:3000**

A interface exibe:
- Painel de contadores (total, pendentes, em andamento, concluídas)
- Lista de tarefas em cards
- Botão **"+ Nova Tarefa"** no cabeçalho

---

### Passo 6 — Usar o sistema pelo Frontend

**Criar uma tarefa:**
1. Clique em **"+ Nova Tarefa"**
2. Preencha o **Título** (obrigatório) e a **Descrição** (opcional)
3. Clique em **"Criar"**
4. A tarefa aparece na lista com status **Pendente**

**Editar uma tarefa:**
1. Clique em **"Editar"** no card desejado
2. Altere título, descrição e/ou status
3. Status disponíveis: **Pendente** → **Em andamento** → **Concluído**
4. Clique em **"Salvar"**

**Excluir uma tarefa:**
1. Clique em **"Excluir"** no card desejado
2. Confirme na caixa de diálogo

---

### Passo 7 — Usar o sistema pela API (curl)

**Criar tarefa:**
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title": "Estudar Kafka", "description": "Revisar conceitos de mensageria"}'
```

Resposta `201 Created`:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Estudar Kafka",
  "description": "Revisar conceitos de mensageria",
  "status": "PENDING",
  "statusDisplayName": "Pendente",
  "createdAt": "2024-01-15T10:30:00"
}
```

**Listar todas as tarefas:**
```bash
curl http://localhost:8080/api/tasks
```

**Atualizar tarefa** (substitua `{id}` pelo UUID retornado):
```bash
curl -X PUT http://localhost:8080/api/tasks/{id} \
  -H "Content-Type: application/json" \
  -d '{"title": "Estudar Kafka", "description": "Revisão concluída", "status": "DONE"}'
```

Status válidos: `PENDING` | `IN_PROGRESS` | `DONE`

**Excluir tarefa:**
```bash
curl -X DELETE http://localhost:8080/api/tasks/{id}
# Resposta: 204 No Content
```

---

### Passo 8 — Observar os eventos Kafka nos logs

Cada criação ou atualização publica um evento no Kafka. Para ver em tempo real:

```bash
docker compose logs -f api
```

Exemplo de saída (logs em JSON estruturado):
```
{"message":"Publishing task.created event: taskId=abc-123, title=Estudar Kafka"}
{"message":"Event consumed [task.created]: taskId=abc-123, title=Estudar Kafka, status=PENDING, occurredAt=..."}
{"message":"Publishing task.updated event: taskId=abc-123, status=DONE"}
{"message":"Event consumed [task.updated]: taskId=abc-123, title=Estudar Kafka, status=DONE, occurredAt=..."}
```

Para ver logs de todos os serviços simultaneamente:
```bash
docker compose logs -f
```

---

### Passo 9 — Encerrar a aplicação

```bash
# Parar todos os containers (mantém os dados do banco)
docker compose down

# Parar E apagar os dados do banco (volume PostgreSQL)
docker compose down -v

# Parar o Docker daemon (opcional)
sudo systemctl stop docker
```

---

### Rodando os Testes

Os testes não precisam do Docker (não conectam em banco nem Kafka):

```bash
cd backend
mvn test
```

Testes incluídos:
- `CreateTaskServiceTest` — unitário com Mockito puro, sem Spring context
- `TaskControllerTest` — camada web com `@WebMvcTest`, sem DB/Kafka

---

### Opção Alternativa: Infraestrutura no Docker + Aplicações Local

Útil durante desenvolvimento para hot-reload:

```bash
# Terminal 1 — sobe apenas a infraestrutura
docker compose up postgres zookeeper kafka

# Terminal 2 — API com hot-reload
cd backend
mvn spring-boot:run

# Terminal 3 — Frontend com Vite dev server
cd frontend
npm install
npm run dev
# Disponível em http://localhost:5173
# O Vite já está configurado para proxiar /api → localhost:8080
```

---

## Arquitetura

### Diagrama

```
┌─────────────────────────────────────────────────────────────┐
│                        Docker Compose                       │
│                                                             │
│  ┌─────────────┐    ┌───────────────────────────────────┐  │
│  │   Frontend  │    │         Backend (Spring Boot)     │  │
│  │  React + TS │───▶│                                   │  │
│  │  (nginx:80) │    │  ┌─────────────────────────────┐  │  │
│  └─────────────┘    │  │  api/  (Controller, DTOs)   │  │  │
│                     │  │  application/ (Use Cases)   │  │  │
│                     │  │  domain/ (Model, Events)    │  │  │
│                     │  │  infrastructure/            │  │  │
│                     │  │  ├── persistence/ (JPA)     │  │  │
│                     │  │  ├── messaging/ (Kafka)     │  │  │
│                     │  │  └── config/                │  │  │
│                     │  └─────────────────────────────┘  │  │
│                     │          │              │          │  │
│                     └──────────┼──────────────┼──────────┘  │
│                                │              │             │
│                   ┌────────────▼──┐  ┌────────▼──────────┐  │
│                   │  PostgreSQL   │  │  Kafka + Zookeeper │  │
│                   │  (port 5432)  │  │  (port 9092)       │  │
│                   └───────────────┘  └────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Estrutura de Pacotes (Hexagonal Simplificada)

```
backend/src/main/java/com/tasks/
├── api/
│   ├── controller/       # TaskController — endpoints REST
│   ├── dto/              # CreateTaskRequest, UpdateTaskRequest, TaskResponse
│   └── exception/        # GlobalExceptionHandler (@RestControllerAdvice)
├── application/
│   ├── port/
│   │   ├── input/        # Interfaces de entrada: CreateTaskUseCase, ...
│   │   └── output/       # Interfaces de saída: TaskRepositoryPort, TaskEventPort
│   └── usecase/          # Implementações: CreateTaskService, ...
├── domain/
│   ├── model/            # Task (POJO puro), TaskStatus (enum)
│   ├── event/            # TaskCreatedEvent (record imutável)
│   └── exception/        # TaskNotFoundException
└── infrastructure/
    ├── persistence/       # TaskEntity (JPA), TaskJpaRepository, TaskRepositoryAdapter
    ├── messaging/         # TaskEventProducer (Kafka), TaskEventConsumer
    └── config/            # KafkaConfig, WebConfig (CORS)
```

### Fluxo de Eventos

```
POST /api/tasks
       │
       ▼
 TaskController
       │
       ▼
 CreateTaskService ──── save ────▶ TaskRepositoryAdapter ──▶ PostgreSQL
       │
       └──── publish ──▶ TaskEventProducer ──▶ Kafka (topic: task.created)
                                                       │
                                               TaskEventConsumer
                                                       │
                                               LOG estruturado (JSON)
```

---

## Decisões Técnicas

### Java 21 + Spring Boot 3.2
Java 21 com virtual threads disponíveis e records nativos para eventos e DTOs imutáveis. Spring Boot 3.2 oferece suporte completo ao Java 21 e integração nativa com Kafka, Flyway e Actuator.

### Arquitetura Hexagonal (Ports & Adapters)
A separação rigorosa em `domain → application → api/infrastructure` garante que a lógica de negócio não depende de framework, banco ou mensageria. O domínio (`Task`, `TaskStatus`, `TaskCreatedEvent`) é código Java puro sem anotações externas. Isso permite:
- Trocar PostgreSQL por outro banco sem tocar no domínio
- Trocar Kafka por RabbitMQ sem reescrever os use cases
- Testar serviços de aplicação com mocks simples (sem contexto Spring)

### Kafka (Event-Driven)
Ao criar ou atualizar uma tarefa, o `TaskEventProducer` publica no tópico correspondente (`task.created`, `task.updated`). O `TaskEventConsumer` consome e loga com estrutura JSON. Isso desacopla futuras integrações (ex: notificações, auditoria) sem modificar os use cases.

### PostgreSQL + Flyway
Migrations versionadas em `db/migration/V1__create_tasks_table.sql` garantem rastreabilidade e reprodutibilidade do schema. O Flyway executa automaticamente na inicialização.

### Logs Estruturados (JSON via Logstash Encoder)
Todos os logs saem em JSON (`logback-spring.xml` + `logstash-logback-encoder`), prontos para ingestão em ELK/Grafana Loki sem configuração adicional.

### Frontend React + TypeScript + Vite
Interface funcional sem biblioteca de UI externa para manter foco na integração. O Vite proxy em dev (`/api → localhost:8080`) e o nginx proxy em Docker garantem zero CORS cross-origin issues.

---

## Endpoints da API

| Método | Endpoint | Descrição | Status |
|--------|----------|-----------|--------|
| `POST` | `/api/tasks` | Criar tarefa | 201 |
| `GET` | `/api/tasks` | Listar todas | 200 |
| `PUT` | `/api/tasks/{id}` | Atualizar tarefa | 200 |
| `DELETE` | `/api/tasks/{id}` | Excluir tarefa | 204 |
| `GET` | `/actuator/health` | Health check | 200 |

### Códigos de Erro

| Código | Situação |
|--------|----------|
| `400` | Dados inválidos (título em branco, status ausente) |
| `404` | Tarefa não encontrada |
| `500` | Erro interno |

---

## Uso de Inteligência Artificial

Este projeto foi desenvolvido com auxílio do **Claude (Anthropic — claude-sonnet-4-6)** via Claude Code CLI.

**O que foi gerado com IA:**
- Código Java seguindo a arquitetura hexagonal planejada previamente
- Componentes React + TypeScript
- Testes unitários e de controller
- Este README

**Como foi utilizado:**
Dado o plano de arquitetura definido nas especificações, o Claude Code foi instruído a implementar cada camada com decisões técnicas específicas fornecidas pelo desenvolvedor (Spring Boot em vez de Micronaut, PostgreSQL, arquitetura hexagonal). Toda a lógica de design — separação de camadas, contratos de interface, fluxo de eventos — foi especificada previamente, com a IA atuando como implementador das decisões tomadas.
