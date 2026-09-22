# Despliegue en AWS

## 1. Amazon EC2

Desplegar el BFF y los dos microservicios en una instancia EC2 o en instancias separadas:

- BFF: `8080`
- solicitudes-service: `8081`
- catalogo-service: `8082`

Los puertos `8081` y `8082` no deben exponerse públicamente. Solo el BFF debe recibir tráfico desde API Gateway.

## 2. Amazon RDS MySQL

Crear una instancia MySQL y los esquemas `mesatech_solicitudes` y `mesatech_catalogo`. Configurar `DB_URL`, `DB_USER` y `DB_PASSWORD` como variables de entorno en cada microservicio. No incluir credenciales en Git.

## 3. AWS API Gateway HTTP API

1. Crear una HTTP API.
2. Crear integración HTTP hacia la URL pública del BFF.
3. Configurar las rutas `/api/v1/{proxy+}` y `/api/v2/{proxy+}`.
4. Habilitar CORS para el origen de React, métodos `GET,POST,PUT,PATCH,DELETE,OPTIONS` y encabezados `Authorization,Content-Type`.
5. Crear un JWT Authorizer:
   - Issuer: `https://login.microsoftonline.com/055a632e-e72b-4c47-8721-0622709b654f/v2.0`
   - Audience: `d76c42fd-0fa1-4c27-8e13-3bee84d0d3f3`
6. Asociar el authorizer a todas las rutas de negocio.
7. Probar que una llamada sin token sea rechazada por API Gateway y no aparezca en los logs del BFF.

## 4. Frontend

Configurar `REACT_APP_API_BASE_URL` con la URL de la etapa de API Gateway. Registrar esa URL como origen permitido en CORS. La Redirect URI del registro SPA debe coincidir con la URL donde se publique React.

## 5. App Roles en Entra ID

En el registro de la API `back`, crear los App Roles `CLIENTE`, `OPERADOR` y `ADMINISTRADOR`, permitidos para usuarios/grupos. Asignar los usuarios de prueba desde Aplicaciones empresariales. Los roles aparecerán en el claim `roles` del Access Token.

Mientras un usuario autenticado no tenga un App Role explícito, el BFF le asigna únicamente el perfil mínimo `CLIENTE`. Los perfiles `OPERADOR` y `ADMINISTRADOR` siempre requieren asignación en Entra ID.
