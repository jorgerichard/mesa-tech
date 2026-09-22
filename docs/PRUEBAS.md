# Plan de pruebas obligatorio

| ID | Prueba | Resultado esperado |
| --- | --- | --- |
| P01 | Abrir React sin sesión | Se muestra el botón de inicio de sesión. |
| P02 | Llamar API Gateway sin token | API Gateway rechaza la solicitud. |
| P03 | Enviar JWT inválido o expirado | Respuesta 401; la operación no se procesa. |
| P04 | Token válido con `access_as_user` | La operación autorizada responde correctamente. |
| P05 | CLIENTE consulta todas las solicitudes | Respuesta 403. |
| P06 | OPERADOR consulta todas | Respuesta 200. |
| P07 | CLIENTE intenta mantener catálogo | Respuesta 403. |
| P08 | Cambiar `ASIGNADA` directamente a `RESUELTA` | Respuesta 409; transición no permitida. |
| P09 | Flujo hasta `EN_PROCESO` y luego `RESUELTA` | Cambio aceptado. |
| P10 | Consultar la misma solicitud por v1 y v2 | Ambas responden; v2 incluye historial y operador. |
| P11 | Reiniciar servicios y volver a consultar | La información permanece en MySQL. |
| P12 | Consumir desde React | CORS permite el origen configurado y Authorization. |

## Ejemplo Postman

Usar la URL de API Gateway y seleccionar **Authorization > Bearer Token**. Pegar un Access Token, nunca un ID Token.

```http
POST /api/v1/solicitudes
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "titulo": "Equipo no inicia",
  "descripcion": "El computador muestra una pantalla negra al encender.",
  "categoria": "SOPORTE_HARDWARE",
  "prioridad": "ALTA"
}
```
