# Backend Design

##  Architecture

Vue 3 application hosted on Netlify.

Website content is currently stored locally in the frontend repository.
Updating content requires a new frontend deployment.

### Target architecture

Public frontend:
- Vue 3
- Vite
- Pinia
- Netlify

Backend:
- Spring Boot REST API
- Spring Security
- Railway or another managed Java host

Database:
- PostgreSQL
- Managed database service

### Core architectural rule

The public website should remain usable during a backend outage.

Remote content features should use one of these behaviors:

1. Display safe fallback content.
2. Hide the optional feature.
3. Use a recently cached response when appropriate.

The site should never display stale pricing as though it is current.

### Migration approach

Features will migrate independently.

Planned order:

1. Weekly special
2. Announcements
3. Scheduled closures
4. Hours overrides
5. Events
6. Menu items
7. Gallery and About content

### Out of scope for the first release

- Online ordering
- Customer accounts
- Payment processing
- Table reservations
- Multiple administrator roles
- Complex analytics dashboard

## Database

### Initial table: weekly_specials

Planned columns:

- id
- title
- description
- price
- image_url
- image_alt
- start_date
- end_date
- status
- dish_creator_staff_id
- created_at
- updated_at

### Important decisions

#### Price

Use PostgreSQL NUMERIC rather than floating point.

Suggested definition:

NUMERIC(8, 2)

#### Deletion

Prefer deactivation or archiving over permanent deletion.

#### Date ranges

The backend should reject an end date that is before the start date.

#### Multiple active specials

Initial assumption:

Only one special should be displayed at a time.

The service layer must define what happens when two active records overlap.

### Future tables

- announcements
- business_hours
- hours_overrides
- events
- menu_categories
- menu_items
- menu_item_prices
- admin_users
- audit_log

### TODO

- [ x] Finalize weekly_specials schema.
- [ x] Create Flyway migration V1.
- [ ] Decide whether images use URLs or uploads.
- [ ] Decide whether to add an archived column.
- [ ] Define audit-history requirements.

## API Plan

### API versioning

Initial routes will use `/api`.
Before public release, decide whether to begin with `/api/v1`.
Changing this after multiple features are deployed will be more disruptive.

### Public weekly-special route

GET /api/public/weekly-specials/current

Expected response:

json
{
  "id": 12,
  "title": "Baked Ziti",
  "description": "Baked ziti served with a dinner salad.",
  "price": "18.95",
  "imageUrl": null,
  "imageAlt": "",
  "startDate": "2026-08-19",
  "endDate": "2026-08-23",
  "active": true,
  "createdAt": "2026-08-17T15:30:00Z",
  "updatedAt": "2026-08-17T15:30:00Z"
}
### TODO

- [ ] Decide API versioning.
- [ ] Decide 204 versus inactive-object behavior.
- [ ] Define validation-error response shape.
- [ ] Define authentication method.
- [ ] Define image-upload strategy.


## Deployment

## Decisions

## Open questions