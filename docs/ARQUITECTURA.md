# Arquitectura y reglas de MesaTech Cloud

## Flujo extremo a extremo

1. React inicia sesión con Microsoft Entra ID mediante MSAL.
2. React obtiene un Access Token para `access_as_user`.
3. React llama exclusivamente a AWS API Gateway con `Authorization: Bearer`.
4. El JWT Authorizer de API Gateway valida el token antes de la integración.
5. El BFF vuelve a validar firma, issuer, audiencia, expiración, scope y roles.
6. El BFF propaga únicamente el contexto mínimo del usuario al microservicio correspondiente.
7. El microservicio ejecuta las reglas y persiste en su propio esquema MySQL.

## Matriz de permisos

| Acción | CLIENTE | OPERADOR | ADMINISTRADOR |
| --- | :---: | :---: | :---: |
| Crear solicitud | Sí | Sí | Sí |
| Ver solicitudes propias | Sí | Sí | Sí |
| Ver todas | No | Sí | Sí |
| Asignar/cambiar estado | No | Sí | Sí |
| Mantener catálogo | No | No | Sí |

## Flujo de estados

`CREADA -> ASIGNADA -> EN_PROCESO -> RESUELTA -> CERRADA`

Desde `CREADA`, `ASIGNADA` y `EN_PROCESO` se permite `CANCELADA`. Desde `RESUELTA` se permite volver a `EN_PROCESO` o cerrar. No se puede resolver una solicitud sin haber pasado por `EN_PROCESO`.

## Versionamiento

- `GET /api/v1/solicitudes/{id}`: contrato básico, sin datos internos de atención.
- `GET /api/v2/solicitudes/{id}`: agrega operador, fecha de actualización e historial de estados.
- V1 permanece disponible para no romper clientes existentes.

## Decisión de persistencia

Para AWS se recomienda Amazon RDS MySQL porque separa la base de datos de las instancias EC2, automatiza respaldos y reduce la administración del motor. Cada microservicio utiliza un esquema independiente y el BFF no posee credenciales de base de datos.
