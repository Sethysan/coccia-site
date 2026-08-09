# Coccia House Full-Stack Migration

## Before August 3

- [ ✔] Commit and tag the current stable production version.
- [ ✔] Confirm Netlify deploy rollback is available.
- [ ✔] Add `.env.example`.
- [ ✔] Add frontend API configuration.
- [ ✔] Add weekly-special model.
- [ ✔] Add weekly-special service.
- [ ✔] Add weekly-special Pinia store.
- [ ✔] Keep `VITE_USE_REMOTE_CONTENT=false`.
- [ ✔] Add backend and documentation folders.
- [ ✔] Document the existing Netlify build configuration.
- [ ✔] Record all current production environment variables.

## During the August 3–18 closure

### Backend foundation

- [ ✔] Generate Spring Boot project.
- [ ✔] Add Spring Web.
- [ ✔] Add Spring Data JPA.
- [ ✔] Add PostgreSQL driver.
- [ ✔] Add Validation.
- [ ✔] Add Spring Security.
- [ ✔] Add Flyway.
- [ ✔] Configure development profile.
- [ ✔] Create local PostgreSQL database.
- [ ✔] Create first migration.

### Weekly special

- [✔] Create WeeklySpecial entity.
- [✔] Create WeeklySpecialRepository.
- [✔] Create WeeklySpecialService.
- [✔] Create WeeklySpecialResponse DTO.
- [✔] Create public current-special endpoint.
- [✔] Add validation tests.
- [✔] Add controller tests.
- [✔] Seed a test weekly special.

### Deployment

- [✔] Create backend hosting service.
- [✔] Create production PostgreSQL database.
- [✔] Add production environment variables.
- [✔] Configure allowed frontend origin.
- [✔] Add API health endpoint.
- [✔] Test API independently from Vue.
- [✔] Test API from a Netlify deploy preview.

### Frontend connection

- [ ] Keep local production site unchanged.
- [ ] Set API URL in a deploy preview.
- [ ] Enable remote content only in the preview.
- [ ] Verify API success behavior.
- [ ] Verify API timeout behavior.
- [ ] Verify API outage fallback.
- [ ] Verify no-current-special behavior.
- [ ] Confirm mobile display.

### Production release

- [ ] Create database backup.
- [ ] Confirm rollback procedure.
- [ ] Deploy backend.
- [ ] Verify backend health.
- [ ] Enable API content in Netlify.
- [ ] deploy frontend.
- [ ] Verify the live special.
- [ ] Test editing without redeploying Vue.
- [ ] Confirm the local fallback remains available.

## After reopening

- [ ] Observe weekly-special updates for two cycles.
- [ ] Fix workflow problems before adding another feature.
- [ ] Add announcements next.
- [ ] Add hours overrides after announcements.
- [ ] Delay menu migration until the admin workflow is reliable.

### Once implemented, publishing a new weekly special should work like this:

Administrator saves draft
        ↓
Backend validates it
        ↓
PostgreSQL stores it
        ↓
Administrator activates it
        ↓
Public GET endpoint returns it
        ↓
Website displays it without a Netlify rebuild

#### The old special can remain active until the new one is successfully saved. Activation should happen only after the complete new record exists.

## POST /api/admin/weekly-specials/{id}/activate

The service would perform activation in one database transaction:

1. Validate the new special.
2. Deactivate the previous special.
3. Activate the new special.
4. Commit everything together.