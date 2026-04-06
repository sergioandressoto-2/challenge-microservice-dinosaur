# Dinosaur Microservice

Microservicio que permite el AMB de dinosaurios. 
El proyecto sigue los lineamientos de una arquitectura Hexagonal 

---

## Stack tecnológico

| Tecnología      | Versión | Rol                              |
|-----------------|---------|----------------------------------|
| Java            | 21      | Lenguaje                         |
| Spring Boot     | 4.0.5   | Framework principal              |
| Spring Data JPA | 4.0.4   | Persistencia                     |
| PostgreSQL      | 17      | Base de datos                    |
| RabbitMQ        | alpine  | Message broker                   |
| Maven           | 3.9+    | Build tool                       |
| Docker          | 29+     | Contenerización                  |
| Docker Compose  | —       | Orquestación local               |
| H2              | —       | Base de datos en memoria (tests) |

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
- [ ] Docker Engine 29+ , testeado en docker desktop 4.34 con **"Allow the default Docker socket to be used"** habilitado
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
cp .env_template .env
```

```bash
APP_PORT=8080

DB_NAME=<dbname>
DB_USERNAME=postgres
DB_PASSWORD=<password>
DB_PORT=5432 # default

JPA_DDL_AUTO=update
JPA_SHOW_SQL=true  # development

RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=admin
RABBITMQ_PORT=5672

# Scheduler
SCHEDULER_CRON=0 */10 * * * *
```

## Ejecución

### Con Docker Compose 

Levanta la app, PostgreSQL y RabbitMQ en un solo comando:

```bash
docker compose up --build
```

Para ejecutar en background:

```bash
docker compose up --build -d
```

Para detener y eliminar contenedores:

```bash[docker-compose.yml](docker-compose.yml)
docker compose down
```

Para detener y eliminar contenedores **y volúmenes** (resetea la base de datos):

```bash
docker compose down -v
```

## API REST

Base path: `/api/v1/dinosaur`

| Método | Endpoint  | Descripción                  | Status |
|--------|-----------|------------------------------|--------|
| POST   | `/`       | Crear dinosaurio             | 201    |
| GET    | `/`       | Listar todos los dinosaurios | 200    |
| GET    | `/{id}`   | Obtener dinosaurio por ID    | 200    |
| PUT    | `/{id}`   | Actualizar dinosaurio        | 200    |
| DELETE | `/{id}`   | Eliminar dinosaurio          | 200    |

Endpoint health para chequear el estado de la aplicación y conexión a base de datos
se puede ejecutar con: 

```bash
curl --location 'http://localhost:8080/actuator/health'
```
swagger path: **http:localhost:8080/swagger-ui.html**

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
### Collection Postman para test
Se encuentra disponible en el repositorio el archivo
Dinosaur_API.postman_collection.json

## Reglas de negocio

- `discoveryDate` debe ser anterior a `extinctionDate`
- No se puede actualizar un dinosaurio con estado `EXTINCT`
- No se puede modificar un dinosaurio en estado `EXTINCT`
- El estado inicial siempre es `ALIVE`
- Los valores posibles para status son ALIVE, ENDANGERED y EXTINCT.

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
**NOTA:** He intentado utilizar Testcontainers con @ServiceConnection, no he logrado dejarlo funcional por problemas de comunicacion con maven y docker api al instanciar los containers.
He decidio continuar modificando los test de integración utilizando H2 para la base de datos en memoria. 

```bash
# Ver exchanges
docker exec <nombre_container_broker> rabbitmqctl list_exchanges

# Ver queues y mensajes pendientes
docker exec <nombre_container_broker> rabbitmqctl list_queues name messages messages_ready

# Ver bindings
docker exec <nombre_container_broker> rabbitmqctl list_bindings
```

La aplicación tiene un listener que está escuchando la cola notification.status.queue e imprime en el log el mensaje.

Ejemplo:

```bash
2026-04-06T00:11:22.297Z  INFO 1 --- [dinosaur] [ntContainer#0-1] c.c.m.a.i.m.NotificationListener         : Notification received: dinosaurId=8 newStatus=EXTINCT timestamp=2026-04-06T00:11:22.285919792
```

---

## Mejoras para aplicar y comentarios

1. Generar proyecto Apache Jmeter o postman 
Para realizar pruebas de carga automatizadas, 
simulando carga progresivamente para testear el comportamiento de la API.

2. Para el proceso automatico @scheduler actualmente se utilizó el annotation para el challenge. Para produccion
si se tienen mas instancias de la aplicacion puede traer problemas (correrían en todas las instancias a la vez), se podria utilizar libreria como ShedLock o el framework QUARTZ.
3. Implementar test de integración con Testcontainers para probar la db y broker rabbitmq ( actualmente se intento pero no se logro integrar correctamente).
4. Agregar plugin maven JaCoCo para cobertura de tests
5. Asegurar Api implementando Spring Security con JWT o utilizar un producto como keycloak para produccion.

