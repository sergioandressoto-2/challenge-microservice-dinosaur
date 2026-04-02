# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build
./mvnw clean package

# Run
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=DinosaurApplicationTests

# Run a single test method
./mvnw test -Dtest=DinosaurApplicationTests#methodName
```

**Prerequisites:** PostgreSQL running at `localhost:5432` with database `dbtest`, user `postgres`, password `mysecretpassword`.

## Architecture

This project implements **Hexagonal Architecture (Ports & Adapters)**. The dependency flow always points inward: adapters → ports → application → domain.

```
HTTP Client
    │
adapters/in/HttpAdapter.java   (@RestController, implements HttpPort)
    │
port/http/HttpPort.java        (inbound port interface)
    │
application/DinosaurService.java
    │
domain/                        (Dinosaur entity, Status enum, DomainException)
    │
port/db/DbPort.java            (outbound port interface)
    │
adapters/out/DbAdapter.java    (@Component, implements DbPort)
    │
adapters/out/repository/       (Spring Data JPA)
    │
PostgreSQL
```

**Key rule:** The domain layer has zero Spring/framework dependencies. Business rules live only in `domain/Dinosaur.java`.

## Layer Responsibilities

| Layer | Package | Role |
|-------|---------|------|
| Domain | `domain/` | Business entities, rules, exceptions. No framework deps. |
| Application | `application/` | `DinosaurService` orchestrates use cases; DTOs in `dto/` |
| Ports | `port/http/`, `port/db/` | Interfaces that define the boundaries |
| Adapters (in) | `adapters/in/` | REST controller — translates HTTP to application calls |
| Adapters (out) | `adapters/out/` | JPA adapter + `DinosaurEntity` for DB persistence |

## REST API

Base path: `/api/v1/dinosaur`

| Method | Path | Status |
|--------|------|--------|
| POST | `/` | Implemented |
| GET | `/` | Implemented |
| GET | `/{id}` | Implemented |
| PUT | `/{id}` | Stub (not implemented) |
| DELETE | `/{id}` | Stub (not implemented) |

## Domain Business Rules

Enforced in `Dinosaur.java`:
- Discovery date must be before extinction date
- Cannot update an extinct dinosaur
- Cannot change status once it is `EXTINCT`
- Initial status is `ALIVE`

Status values: `ALIVE`, `ENDANGERED`, `EXTINCT` (defined in `Status.java`).

## Object Mapping

`ModelMapper` (v3.1.0) is used in `DinosaurService` to convert between domain objects, DTOs (`DinosaurRequest`/`DinosaurResponse`), and JPA entities (`DinosaurEntity`). When adding new fields, ensure they are mapped consistently across all three representations.
