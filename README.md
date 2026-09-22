# MesaTech Cloud

MVP de gestión de solicitudes de soporte construido según las directrices de la EP1 DSY1107.

## Arquitectura

`React + MSAL -> AWS API Gateway -> BFF Spring Boot -> solicitudes-service / catalogo-service -> MySQL`

- El frontend nunca consume directamente los microservicios.
- API Gateway y el BFF validan el Access Token de Microsoft Entra ID.
- El BFF aplica autorización y no accede a la base de datos.
- Los microservicios implementan reglas de negocio y persistencia.
- `/api/v1/solicitudes/{id}` y `/api/v2/solicitudes/{id}` coexisten. V2 agrega historial y datos extendidos.

## Puertos locales

| Componente | Puerto |
| --- | ---: |
| React | 3000 |
| BFF | 8080 |
| solicitudes-service | 8081 |
| catalogo-service | 8082 |
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

Para desarrollo sin Docker, cada backend se ejecuta con `mvn spring-boot:run` desde su carpeta y React con `npm install && npm start`.

## Roles

Los App Roles esperados en el claim `roles` son `CLIENTE`, `OPERADOR` y `ADMINISTRADOR`. El scope `access_as_user` autoriza el acceso general; los roles determinan las operaciones de negocio.

## Documentación

- [Arquitectura y reglas](docs/ARQUITECTURA.md)
- [Configuración AWS](docs/AWS.md)
- [Pruebas obligatorias](docs/PRUEBAS.md)
- [Colección Postman](docs/MesaTech.postman_collection.json)
