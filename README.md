# encasa-web-BE

Backend de EnCasa: guía/contacto/calificación de profesionales de servicios para el hogar (plomería, electricidad, pintura, etc.).

## Stack

- Java 21, Spring Boot 3.3.5 (Web, Security, Data JPA, Validation)
- PostgreSQL en producción / con `docker-compose`; perfil `dev` con H2 en archivo (sin Docker)
- JWT propio (`io.jsonwebtoken`) — login, registro y OAuth devuelven un token que el frontend manda como `Authorization: Bearer`

## Correr en local

**Sin Docker (perfil `dev`, H2 en archivo, con datos de ejemplo precargados por `DataLoader`):**

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Con Postgres real:**

```bash
docker-compose up -d
mvn spring-boot:run
```

La app queda en `http://localhost:8080`. `ddl-auto=update` (ver `application.yml`), así que las tablas se crean/actualizan solas al arrancar.

Variables relevantes (ver `application.yml`, todas con default para desarrollo local):
- `ALLOWED_ORIGINS` — orígenes permitidos por CORS (default `http://localhost:3000`)
- `BOOKING_AUTO_COMPLETE_DAYS` — días de una confirmación unilateral de un booking antes de auto-cerrarlo (default 5)
- `DB_URL` / `DB_USER` / `DB_PASS`, `JWT_SECRET`, `JWT_EXPIRATION`, `PORT`

## Modelo de dominio

- **User**: cuenta con `role` (`USER` | `professional` | admin). Pasa a `professional` al crear un perfil de profesional; vuelve a `USER` si lo borra.
- **Professional**: perfil de profesional, uno por usuario. `rating`/`reviewCount` se recalculan cada vez que se crea/borra una review (`ReviewService.recalculateProfessionalRating`).
- **Service**: catálogo de servicios, tabla propia (`ServiceRepository`), sembrada por `DataLoader`.
- **Booking**: una reserva de un cliente a un profesional, con fecha programada. Estados: `PENDING → CONFIRMED → COMPLETED`, o `CANCELLED` en cualquier momento antes de `COMPLETED`.
- **Review**: calificación de un cliente a un profesional, atada 1 a 1 a un `Booking` (`bookingId` único).
- **Favorite**: profesionales guardados por un cliente.

### Cómo se completa un booking (confirmación de dos partes)

Las reseñas son el activo de confianza de la plataforma, así que un `Review` solo se puede crear si el `Booking` asociado está `COMPLETED`. Como el trabajo pasa fuera de la plataforma, "completado" no lo decide una sola parte: `PUT /bookings/{id}/complete` lo puede llamar el cliente **o** el profesional de esa reserva. Registra la confirmación de quien llama; el booking pasa a `COMPLETED` recién cuando confirmaron los dos, o si una sola parte confirmó y pasaron `BOOKING_AUTO_COMPLETE_DAYS` días sin que la otra responda (chequeo perezoso al leer el booking, sin cron job).

Ver también `BOOKINGS_AND_REVIEWS.md` en el repo del frontend (`encasa-web`) para el flujo completo, y la tarjeta "Lógica para reviews de usuario" en Trello para el análisis de alternativas que se descartaron.

## Endpoints principales

| Método | Path | Auth | Descripción |
|---|---|---|---|
| POST | `/auth/register` | público | registro con email/password |
| POST | `/auth/login` | público | login con email/password |
| POST | `/auth/oauth/google` | público | usado por NextAuth en el primer login con Google |
| GET/PUT/DELETE | `/users/me` | auth | perfil propio |
| PUT | `/users/me/password` | auth | cambiar contraseña |
| GET/POST/DELETE | `/users/me/favorites` | auth | favoritos del cliente |
| GET | `/services` | público | catálogo de servicios |
| GET | `/professionals` | público | lista (filtro `serviceId`) |
| GET | `/professionals/{id}` | público | detalle |
| GET/POST/PUT/DELETE | `/professionals/me` | auth | perfil propio de profesional |
| PATCH | `/professionals/me/availability` | auth | cambiar disponibilidad |
| POST | `/bookings` | auth (cliente) | crear reserva |
| GET | `/bookings/me` | auth | mis reservas como cliente |
| GET | `/bookings/professional` | auth | reservas recibidas como profesional |
| PUT | `/bookings/{id}/confirm` | auth (profesional) | `PENDING → CONFIRMED` |
| PUT | `/bookings/{id}/cancel` | auth (cliente o profesional) | cancelar |
| PUT | `/bookings/{id}/complete` | auth (cliente o profesional) | confirmación de la parte que llama — ver arriba |
| POST | `/reviews` | auth (cliente) | crear review (requiere booking `COMPLETED`, 1 por booking) |
| GET | `/reviews`, `/reviews/professional/{id}`, `/reviews/me` | público / auth | listados |
| DELETE | `/reviews/{id}` | auth (dueño) | borrar reseña propia |
| `/admin/**` | auth (rol `ADMIN`) | gestión de usuarios, profesionales, servicios, bookings y reviews |
