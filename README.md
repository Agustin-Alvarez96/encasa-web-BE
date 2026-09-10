# encasa-web-BE

Backend de EnCasa: guía/contacto/calificación de profesionales de servicios para el hogar (plomería, electricidad, pintura, etc.).

## Stack

- Java 21, Spring Boot 3.3.5 (Web, Security, Data JPA)
- PostgreSQL
- JWT propio (`io.jsonwebtoken`) — el login/registro/sync devuelven un token que el frontend manda como `Authorization: Bearer`

## Correr en local

```bash
docker-compose up -d          # levanta Postgres en :5432 (encasa/encasa_user/encasa_pass)
./mvnw spring-boot:run         # o: mvn spring-boot:run
```

La app queda en `http://localhost:8080`. `spring.jpa.hibernate.ddl-auto=update` (ver `application.yml`), así que las tablas se crean/actualizan solas al arrancar — no hay migraciones manuales.

Variables relevantes en `application.yml`:
- `booking.auto-complete-days` — días de una confirmación unilateral antes de auto-cerrar una solicitud (default 5)
- `cors.allowed-origins` — orígenes permitidos para el frontend (default `http://localhost:3000`; agregar la URL de producción cuando exista)

## Modelo de dominio

- **User**: cuenta con `role` (`CLIENT` | `PROFESSIONAL`). Se crea como `CLIENT` en el registro/sync; pasa a `PROFESSIONAL` automáticamente al crear un perfil de profesional (`POST /professionals/me`).
- **Professional**: perfil de profesional, uno por usuario (`userId` único). `rating`/`reviewCount` no se persisten, se calculan al vuelo desde `Review`.
- **Booking**: una solicitud de un cliente a un profesional. Estados: `REQUESTED → IN_PROGRESS → COMPLETED`.
- **Review**: calificación de un cliente a un profesional, atada 1 a 1 a un `Booking` (`bookingId` único).

### Por qué las reviews están atadas a un booking

Las reseñas son el activo de confianza de la plataforma — el objetivo es que no se puedan dejar reseñas falsas o de competencia. Por eso un `Review` sólo se puede crear si existe un `Booking` de ese cliente con ese profesional en estado `COMPLETED`.

Como el trabajo (plomería, pintura, etc.) pasa fuera de la plataforma, "completado" no lo decide una sola parte: requiere **confirmación de ambas partes** (`POST /bookings/{id}/confirm-completion`, llamable por el cliente o por el profesional dueño de la solicitud). Si confirma una sola parte y la otra no responde en `booking.auto-complete-days` días, el booking se cierra solo la próxima vez que se lee (chequeo perezoso, sin cron job).

## Endpoints

| Método | Path | Auth | Descripción |
|---|---|---|---|
| POST | `/auth/register` | público | registro con email/password |
| POST | `/auth/login` | público | login con email/password |
| POST | `/auth/sync` | público | usado por NextAuth en cada login (OAuth o credenciales): crea el usuario si no existe y devuelve un JWT propio |
| GET | `/users/me` | auth | perfil + rol del usuario actual |
| GET | `/services` | público | catálogo fijo de servicios |
| GET | `/professionals` | público | lista, filtros `serviceId`, `q` |
| GET | `/professionals/{id}` | público | detalle |
| POST | `/professionals/me` | auth | crear/actualizar el propio perfil (setea rol a `PROFESSIONAL`) |
| POST | `/bookings` | auth (cliente) | crear solicitud a un profesional |
| GET | `/bookings/mine` | auth | bookings del usuario actual (como cliente y/o como profesional) |
| PATCH | `/bookings/{id}/status` | auth (profesional dueño) | `REQUESTED → IN_PROGRESS` |
| POST | `/bookings/{id}/confirm-completion` | auth (cliente o profesional dueño) | confirmación de finalización de la parte que llama |
| POST | `/reviews` | auth (cliente) | crear review (requiere booking `COMPLETED`, 1 por booking) |
| GET | `/reviews` | público | todas las reviews |
| GET | `/reviews/professional/{id}` | público | reviews de un profesional |

## Notas

- Ver `BOOKINGS_AND_REVIEWS.md` en el repo del frontend (`encasa-web`) para el flujo completo (frontend + backend) del sistema de solicitudes y calificaciones.
- Fuera de alcance por ahora: KYC/verificación de identidad de profesionales, admin, pagos.
