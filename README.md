# Dinosaur Microservice

Breve descripción del propósito del servicio.

---

## Stack tecnológico

| Tecnología      | Versión  | Rol                              |
|-----------------|----------|----------------------------------|
| Java            | 21       | Lenguaje                         |
| Spring Boot     | 4.0.5    | Framework principal              |
| Spring Data JPA | —        | Persistencia                     |
| PostgreSQL      | 17       | Base de datos                    |
| RabbitMQ        | alpine   | Message broker                   |
| Maven           | 3.9+     | Build tool                       |
| Docker          | 29+      | Contenerización                  |
| Docker Compose  | —        | Orquestación local               |
| H2              | —        | Base de datos en memoria (tests) |

---

## Arquitectura

El proyecto implementa **Arquitectura Hexagonal (Ports & Adapters)**. El flujo de dependencias siempre apunta hacia adentro: adapters → ports → application → domain.

```
HTTP Client
    │
adapters/in/HttpAdapter.java          (@RestController)
    │
application/port/in/                  (casos de uso: inbound ports)
    │
application/DinosaurService.java      (orquestación)
    │
domain/                               (Dinosaur, Status, excepciones)
    │
application/port/out/                 (outbound ports: DB, Notification)
    │
adapters/out/DbAdapter.java           (JPA)
adapters/out/messaging/               (RabbitMQ)
    │
PostgreSQL / RabbitMQ
```

---

## Requisitos previos

- [ ] Java 21
- [ ] Maven 3.9+
- [ ] Docker Desktop 4.34+ con **"Allow the default Docker socket to be used"** habilitado
- [ ] Docker Compose

---

## Configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/sergioandressoto-2/challenge-microservice-dinosaur
cd challenge-microservice-dinosaur
```

### 2. Crear el archivo `.env`

Copiar el archivo de ejemplo y completar los valores:

```bash
cp .env.example .env
```

---

## Ejecución

### Con Docker Compose (recomendado)

Levanta la app, PostgreSQL y RabbitMQ en un solo comando:

```bash
docker compose up --build
```

Para ejecutar en background:

```bash
docker compose up --build -d
```

Para detener y eliminar contenedores:

```bash
docker compose down
```

Para detener y eliminar contenedores **y volúmenes** (resetea la base de datos):

```bash
docker compose down -v
```

### En local (sin Docker)

Requisitos adicionales: PostgreSQL corriendo en `localhost:5432` y RabbitMQ en `localhost:5672`.

```bash
mvn spring-boot:run
```

---

## API REST

Base path: `/api/v1/dinosaur`

| Método | Endpoint  | Descripción                  | Status |
|--------|-----------|------------------------------|--------|
| POST   | `/`       | Crear dinosaurio             | 201    |
| GET    | `/`       | Listar todos los dinosaurios | 200    |
| GET    | `/{id}`   | Obtener dinosaurio por ID    | 200    |
| PUT    | `/{id}`   | Actualizar dinosaurio        | 200    |
| DELETE | `/{id}`   | Eliminar dinosaurio          | 200    |

### Ejemplo de request (POST / PUT)

```json
{
  "name": "T-Rex",
  "species": "Tyrannosaurus Rex",
  "discoveryDate": 1609459200000,
  "extinctionDate": 1640995200000,
  "status": "ALIVE"
}
```

### Estados posibles (`status`)

| Estado       | Descripción                                        |
|--------------|----------------------------------------------------|
| `ALIVE`      | Estado inicial al crear                            |
| `ENDANGERED` | Transición automática cuando faltan menos de 24h   |
| `EXTINCT`    | Transición automática al alcanzar `extinctionDate` |

---

## Reglas de negocio

- `discoveryDate` debe ser anterior a `extinctionDate`
- No se puede actualizar un dinosaurio con estado `EXTINCT`
- El estado no puede retroceder desde `EXTINCT`
- El estado inicial siempre es `ALIVE`

---

## Scheduler

El scheduler revisa automáticamente los estados cada 10 minutos (configurable vía `SCHEDULER_CRON`).

- Dinosaurios con `extinctionDate <= now` → transicionan a `EXTINCT`
- Dinosaurios con `extinctionDate` dentro de las próximas 24h → transicionan a `ENDANGERED`

Cada transición publica un mensaje en RabbitMQ.

---

## Mensajería (RabbitMQ)

| Elemento    | Valor                        |
|-------------|------------------------------|
| Exchange    | `NotificationStatus` (topic) |
| Queue       | `notification.status.queue`  |
| Routing key | `notification.#`             |

### Formato del mensaje

```json
{
  "dinosaurId": 1,
  "newStatus": "ENDANGERED",
  "timestamp": "2024-10-01T09:00:00"
}
```

Los mensajes se publican cuando el status cambia, tanto por el scheduler como por el endpoint PUT.

---

## Tests

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar una clase específica
mvn test -Dtest=DinosaurServiceTest

# Ejecutar un método específico
mvn test -Dtest=DinosaurServiceTest#createDinosaur_savesWhenNameIsUnique
```

| Suite                       | Tipo        | Descripción                               |
|-----------------------------|-------------|-------------------------------------------|
| `DinosaurTest`              | Unitario    | Reglas de negocio del dominio             |
| `DinosaurServiceTest`       | Unitario    | Casos de uso de la capa de aplicación     |
| `DinosaurIntegrationTest`   | Integración | API REST end-to-end con H2 y MockitoBean  |
| `DinosaurApplicationTests`  | Smoke test  | Carga del contexto de Spring              |

---

## Verificación del broker

```bash
# Ver exchanges
docker exec challenge-microservice-dinosaur-rabbitmq-1 rabbitmqctl list_exchanges

# Ver queues y mensajes pendientes
docker exec challenge-microservice-dinosaur-rabbitmq-1 rabbitmqctl list_queues name messages messages_ready

# Ver bindings
docker exec challenge-microservice-dinosaur-rabbitmq-1 rabbitmqctl list_bindings
```

---

## Estructura del proyecto

```
src/
├── main/
│   ├── java/com/challenge/microservice/
│   │   ├── adapters/
│   │   │   ├── in/
│   │   │   │   ├── HttpAdapter.java
│   │   │   │   ├── SchedulerAdapter.java
│   │   │   │   └── messaging/
│   │   │   │       └── NotificationListener.java
│   │   │   └── out/
│   │   │       ├── DbAdapter.java
│   │   │       ├── messaging/
│   │   │       │   ├── RabbitMQConfig.java
│   │   │       │   └── RabbitMQNotificationAdapter.java
│   │   │       ├── model/
│   │   │       │   └── DinosaurEntity.java
│   │   │       └── repository/
│   │   │           └── DbRepository.java
│   │   ├── application/
│   │   │   ├── DinosaurService.java
│   │   │   ├── DinosaurSchedulerService.java
│   │   │   ├── dto/
│   │   │   └── port/
│   │   │       ├── in/
│   │   │       └── out/
│   │   ├── domain/
│   │   │   ├── Dinosaur.java
│   │   │   ├── Status.java
│   │   │   └── ...
│   │   └── DinosaurApplication.java
│   └── resources/
│       └── application.properties
└── test/
    ├── java/com/challenge/microservice/
    │   ├── application/DinosaurServiceTest.java
    │   ├── domain/DinosaurTest.java
    │   ├── dinosaur/DinosaurApplicationTests.java
    │   └── integration/DinosaurIntegrationTest.java
    └── resources/
        ├── application-test.properties
```
