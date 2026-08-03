# Coccia House Backend Design

## 1. Purpose

The backend will provide a Spring Boot REST API and PostgreSQL database for
managing Coccia House website content without requiring a frontend redeploy.

The first managed feature will be recipes and weekly specials.

The public Vue application must continue functioning safely if the backend is
temporarily unavailable.

## Guiding Principles

The backend should model how the restaurant actually operates rather than how
the website currently displays information.

Recipes are permanent.

Weekly Specials are temporary promotions of recipes.

Public information and internal restaurant knowledge remain intentionally
separate.

The frontend should continue operating with fallback content if the backend is
temporarily unavailable.

Database changes are managed exclusively through Flyway migrations.

---

## 2. Technology

### Frontend

 - Vue 3
 - Vite
 - Pinia
 - Netlify

### Backend

 - Java 21
 - Spring Boot
 - Spring Web MVC
 - Spring Data JPA
 - Bean Validation
 - Flyway
 - PostgreSQL

### Future security

 - Spring Security
 - Authenticated administrator accounts
 - Role-based access for protected recipe information

---

## 3. Architecture

The request flow will follow this pattern:

```text
Vue Component
    ↓
Pinia Store
    ↓
Frontend Service
    ↓
Frontend API Client
    ↓
Spring REST Controller
    ↓
Backend Service
    ↓
Repository
    ↓
PostgreSQL
```

Each layer has a separate responsibility:

- Controllers translate HTTP requests into service calls.
- Services contain business rules.
- Repositories perform database access.
- Entities represent persisted database records.
- DTOs define the information accepted or returned by the API.


## 4. Recipe-centered design

A `Recipe` represents a reusable dish.

A `WeeklySpecial` represents a time-bound promotion of one recipe.

Recipe data should not be duplicated every time the same dish becomes a
weekly special.

### StaffMember ↔ Recipe

A staff member may know how to prepare many recipes.

A recipe may be known by many staff members.

This is a many-to-many relationship implemented through a join table named
`staff_member_recipes`.

```text
StaffMember
    many
    |
    └── many Recipes
```

### `Recipe` → `WeeklySpecial`

One `recipe` may appear in many `weekly-special` records over time.

```text
Recipe
    1
    |
    └── many WeeklySpecial records
 ```


### Recipe-owned information

The `recipe` owns permanent information about the dish:

 - Internal recipe name
 - Dish creator or knowledgeable staff member
 - Active status
 - Future ingredient relationships
 - Future preparation instructions
 - Future internal notes

### Weekly-special-owned information

The weekly special owns information specific to one promotional period:

 - Recipe reference
 - Public title
 - Public description
 - Price
 - Promotional image
 - Start date
 - End date
 - Publication status

### Future ingredient and allergen design

Ingredients and allergen metadata will relate to `Recipe`, not directly to
`WeeklySpecial`.

The future relationship will broadly be:

```text
Recipe
    many
    |
    └── many Ingredients
```

A join table such as `recipe_ingredients` will eventually store the connection
between `recipes` and `ingredients`.

Ingredient lists, quantities, preparation instructions, and internal notes
will only be returned through protected staff endpoints.

A future public ingredient-search endpoint may return matching dish names
without exposing complete recipe details.

Customer-facing allergy searches must not be described as a guarantee that a
dish is allergy-safe because recipes, suppliers, and cross-contact conditions
may change.

---

## 5. Current database model

### `staff_members`

#### Purpose:

Stores people associated with recipe knowledge.

#### Planned columns:

 - id
 - display_name
 - active
 - created_at
 - updated_at

#### Rules:

 - Staff members should normally be deactivated rather than deleted.
 - Historical recipes must retain their staff association.

### `recipes`

#### Purpose:

Stores reusable dishes independently of any promotional schedule.

#### Planned columns:

 - id
 - name
 - dish_creator_staff_id
 - active
 - created_at
 - updated_at

#### Rules:

 - A `recipe` belongs to one `staff member`.
 - A `staff member` may be associated with many `recipes`.
 - `Recipe` names should initially be unique.
 - Inactive `recipes` remain available for historical reporting.

### `weekly_specials`

#### Purpose:

Stores individual promotional appearances of `recipes`.

#### Planned columns:

 - id
 - recipe_id
 - public_title
 - public_description
 - price
 - image_url
 - image_alt
 - start_date
 - end_date
 - status
 - created_at
 - updated_at

`Statuses`:

 - DRAFT
 - PUBLISHED
 - ARCHIVED

#### Rules:

 - A `weekly special` references exactly one `recipe`.
 - A `recipe` may have many `weekly-special` records.
 - The end date cannot occur before the start date.
 - Draft and archived specials never appear publicly.
 - A published `special` appears only during its configured date range.
 - Historical weekly `specials` are preserved.
 - The public endpoint returns at most one current special.

---

## 6. Database migration strategy

Flyway migration files are the source of truth for the database schema.

Current migrations:

 - `V1__create_weekly_special_schema.sql`
 - `V2__add_recipes_and_link_weekly_specials.sql`

Migration history must not be rewritten after it has been applied to a shared
or production database.

Future schema changes should use new migrations:
`
 - `V3__add_recipe_ingredients.sql`
 - `V4__add_allergen_categories.sql`

The ERD is a living diagram of the current database structure. Git preserves
its earlier versions.

---

## 7. Initial public API

### Current weekly special

```http
GET /api/public/weekly-specials/current
```

Possible responses:

 - 200 OK when a current published special exists
 - 204 No Content when no special is currently available

The public response may include:

 - Weekly-special ID
 - Recipe ID
 - Recipe name
 - Public title
 - Public description
 - Price
 - Promotional image
 - Start date
 - End date

The public response must not include:

 - Recipe instructions
 - Internal notes
 - Full ingredient quantities
 - Staff-only information

---

## 8. Future protected API

### Planned recipe-management endpoints:

```http
GET    /api/admin/recipes
```
```http
GET    /api/admin/recipes/{id}
```
```http
POST   /api/admin/recipes
```
```http
PUT    /api/admin/recipes/{id}
```
```http
POST   /api/admin/recipes/{id}/archive
```

### Planned weekly-special endpoints:

```http
GET    /api/admin/weekly-specials
```
```http
GET    /api/admin/weekly-specials/{id}
```
```http
POST   /api/admin/weekly-specials
```
```http
PUT    /api/admin/weekly-specials/{id}
```
```http
POST   /api/admin/weekly-specials/{id}/publish
```
```http
POST   /api/admin/weekly-specials/{id}/archive
```

#### `Authentication` and `Authorization` will be added after the initial public read-only data flow works.

---

## 9. Initial implementation order

1. Apply the recipe-centered database migration.
2. Create StaffMember entity.
3. Create Recipe entity.
4. Create WeeklySpecialStatus enum.
5. Create WeeklySpecial entity.
6. Create repositories.
7. Create public response DTOs.
8. Create the weekly-special service.
9. Create the public controller.
10. Test the endpoint independently.
11. Connect the Vue deploy preview.
12. Add authentication.
13. Add administrator write endpoints.
14. Add recipe-management UI.

--- 

## 10. Future phases

### Phase 1
 - Recipe entity
 - Weekly-special scheduling
 - Public current-special endpoint

### Phase 2
 - Authentication
 - Administrator recipe management
 - Administrator weekly-special management

### Phase 3
 - Ingredients
 - Recipe-ingredient relationships
 - Protected recipe instructions and notes

### Phase 4
 - Allergen categories
 - Public ingredient/allergen search
 - Staff filtering by available recipe knowledge

### Phase 5
 - Announcements
 - Hours overrides
 - Menu management