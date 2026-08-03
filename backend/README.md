# Coccia House API

Spring Boot REST API for remotely managed Coccia House website content.

## Initial scope

The first remotely managed feature will be the weekly special.

The public Vue application must continue functioning if this service is
temporarily unavailable.

## Planned technology

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL
- Flyway
- Bean Validation

## Migration strategy

1. Keep current Vue content local.
2. Build a read-only weekly-special endpoint.
3. Test the endpoint without changing production.
4. Enable API reads through an environment variable.
5. Build authentication and administrator write operations.
6. Add announcements and hours overrides.
7. Migrate menu content only after smaller features are stable.

## Initial public endpoint

GET /api/public/weekly-specials/current

## Initial admin endpoints

GET    /api/admin/weekly-specials
POST   /api/admin/weekly-specials
PUT    /api/admin/weekly-specials/{id}
PATCH  /api/admin/weekly-specials/{id}/deactivate

## Safety requirements

- Public GET routes require no authentication.
- All write routes require authentication and authorization.
- Validate dates and prices on the server.
- Never expose database credentials to the Vue application.
- Do not delete historical specials by default.
- Only one weekly special should be active for a given date range.

## TODO

- [ ] Generate the Spring Boot application.
- [ ] Configure PostgreSQL.
- [ ] Add Flyway migrations.
- [ ] Create WeeklySpecial entity.
- [ ] Create request and response DTOs.
- [ ] Create repository.
- [ ] Create service layer.
- [ ] Create public controller.
- [ ] Add integration tests.
- [ ] Deploy a private test environment.
- [ ] Add authentication.
- [ ] Add administrator controller.