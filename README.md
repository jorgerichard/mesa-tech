# MesaTech Cloud

MVP de gestión de solicitudes de soporte con autenticación Microsoft Entra ID, BFF, microservicios, RDS y mensajería RabbitMQ.

## Arquitectura

`React + MSAL -> AWS API Gateway -> BFF Spring Boot -> catalogo-service (productor) -> RabbitMQ -> solicitudes-service (consumidor) -> RDS`

- El frontend nunca consume directamente los microservicios.
- API Gateway y el BFF validan el Access Token de Microsoft Entra ID.
- El BFF aplica autorización y no accede a las bases de datos.
- La creación de solicitudes es asíncrona mediante RabbitMQ.
- Las consultas y cambios de estado permanecen síncronos entre BFF y solicitudes-service.
- Catálogo y Solicitudes usan bases de datos RDS independientes.
- Los mensajes usan un exchange `direct` durable, cola durable y routing key explícita.
- El consumidor es idempotente mediante `eventId`.

## RabbitMQ

| Elemento | Valor |
| --- | --- |
| Exchange | `mesatech.exchange` |
| Tipo | `direct` |
| Cola | `solicitudes.queue` |
| Routing key | `solicitud.creada` |
| Puerto AMQP | `5672` |
| Panel de administración | `15672` |

## Puertos

| Componente | Puerto |
| --- | ---: |
| React | 3000 |
| BFF | 8080 |
| solicitudes-service | 8081 |
| catalogo-service | 8082 |
| RabbitMQ AMQP | 5672 |
| RabbitMQ Management | 15672 |
| MySQL | 3306 |

## Microsoft Entra ID

- Tenant ID: `055a632e-e72b-4c47-8721-0622709b654f`
- Client ID frontend: `663c1419-552a-41e0-bdf1-45665c12c90c`
- Client ID backend: `d76c42fd-0fa1-4c27-8e13-3bee84d0d3f3`
- Scope: `api://d76c42fd-0fa1-4c27-8e13-3bee84d0d3f3/access_as_user`

No se almacenan secretos de Entra ID porque el frontend SPA usa Authorization Code Flow con PKCE.

## Ejecución local

1. Copiar `.env.example` como `.env`.
2. Ejecutar `docker compose up --build`.
3. Abrir `http://localhost:3000`.
4. Abrir RabbitMQ en `http://localhost:15672`.

## Despliegue AWS

- EC2 Catálogo/BFF: ejecuta `catalogo-service` y `bff`.
- EC2 Solicitudes: ejecuta `solicitudes-service`.
- EC2 RabbitMQ: ejecuta RabbitMQ 4.2 Management.
- RDS Catálogo y RDS Solicitudes permanecen privadas.
- Todos los secretos se entregan mediante variables de entorno.

## Roles

Los App Roles esperados en el claim `roles` son `CLIENTE`, `OPERADOR` y `ADMINISTRADOR`.

## Documentación

- [Arquitectura y reglas](docs/ARQUITECTURA.md)
- [Configuración AWS](docs/AWS.md)
- [Pruebas obligatorias](docs/PRUEBAS.md)
- [Colección Postman](docs/MesaTech.postman_collection.json)
