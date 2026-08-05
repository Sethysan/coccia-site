# Coccia House Backend Design

## 1. Purpose

The backend will provide a Spring Boot REST API and PostgreSQL database for
managing Coccia House website content without requiring a frontend redeploy.

The first managed feature will be recipes and weekly offerings.

The public Vue application must continue functioning safely if the backend is
temporarily unavailable.

## Guiding Principles

The backend should model how the restaurant actually operates rather than how
the website currently displays information.

Recipes are permanent.

Weekly offerings are temporary promotions of recipes.

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


## 4. Weekly-offering-centered design

A `Recipe` represents a reusable dish.

A `WeeklyOfferingItem` represents one independently priced recipe being
promoted during a particular week.

A `WeeklyOffering` groups the independently priced dinner, soup, and dessert
features that share a promotional date range.

```text
Recipe
    ↓ reused by
WeeklyOfferingItem
    ↓ grouped inside
WeeklyOffering
```

### `StaffMember` ↔ `Recipe`

A staff member may know how to prepare many recipes.

A recipe may be known by many staff members.

This is a many-to-many relationship implemented through the
`staff_member_recipes` join table.

```text
StaffMember
    many
      ↕
    many
Recipe
```

Staff members are deactivated rather than deleted so historical recipe
knowledge remains available.

### `Recipe` → `WeeklyOfferingItem`

One recipe may appear in many weekly-offering items over time.

Each weekly-offering item references exactly one recipe.

```text
Recipe
    1
    |
    └── many WeeklyOfferingItem records
```

This allows the same recipe to be promoted repeatedly with different prices,
descriptions, date ranges, and price options.

### `WeeklyOffering` → `WeeklyOfferingItem`

One weekly offering may contain:

- Zero or one dinner item
- Zero or one soup item
- Zero or one dessert item

At least one item is required before a weekly offering can be scheduled.

```text
WeeklyOffering
    1
    |
    └── many WeeklyOfferingItem records
```

The soup and dessert items are sold separately from the dinner special and
are available in addition to the restaurant's normal soup and dessert
offerings.

### Recipe-owned information

The `Recipe` owns permanent information about the dish:

- Internal recipe name
- Staff members who know how to prepare it
- Active status
- Future ingredient relationships
- Future preparation instructions
- Future internal notes

### Weekly-offering-owned information

The `WeeklyOffering` owns information shared by the promoted items:

- Start date
- End date
- Publication status
- Created timestamp
- Updated timestamp

### Weekly-offering-item-owned information

Each `WeeklyOfferingItem` owns information specific to one promoted dinner,
soup, or dessert:

- Recipe reference
- Offering type
- Public title
- Public description
- Promotional image
- House-salad inclusion
- Homemade-bread inclusion
- Display order
- One or more prices

House-salad and homemade-bread inclusions apply only to dinner items.

### Multiple item prices

Each weekly-offering item must have at least one price.

Examples include:

```text
Pork Chop Special
- Single: $21.95
- Double: $25.95

Baked Ziti
- Regular: $20.00
- Large: $24.99

Featured Soup
- Cup: $4.95
- Bowl: $6.95
```

When an item has exactly one price, its label may be omitted.

When an item has two or more prices:

- Every price must have a nonblank label.
- Labels must be unique within that item.
- Label comparisons are case-insensitive.
- Every amount must be zero or greater.

The database enforces nonnegative amounts. The backend service enforces the
rules that depend on the complete list of prices.

### Future ingredient and allergen design

Ingredients and allergen metadata will relate to `Recipe`, not directly to
`Weeklyoffering`.

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

### weekly_offerings

Purpose:

Groups the dinner, soup, and dessert features promoted during the same date
range.

Columns:

- `id`
- `start_date`
- `end_date`
- `status`
- `created_at`
- `updated_at`

### weekly_offering_items

Purpose:

Stores one independently priced dinner, soup, or dessert feature within a
weekly offering.

Columns:

- `id`
- `weekly_offering_id`
- `recipe_id`
- `offering_type`
- `public_title`
- `public_description`
- `image_url`
- `image_alt`
- `includes_house_salad`
- `includes_homemade_bread`
- `display_order`
- `created_at`
- `updated_at`

### weekly_offering_item_prices

Purpose:

Stores one or more prices for a weekly-offering item.

Columns:

- `id`
- `weekly_offering_item_id`
- `label`
- `amount`
- `display_order`

## 6. Database migration strategy

Flyway migration files are the source of truth for the database schema.

Current migrations:

- `V1__create_weekly_special_schema.sql`
- `V2__add_recipes_and_link_weekly_specials.sql`
- `V3__replace_recipe_creator_with_staff_recipe_relationship.sql`
- `V4__replace_weekly_specials_with_weekly_offerings.sql`

Migration history must not be rewritten after it has been applied to a shared
or production database.


The ERD is a living diagram of the current database structure. Git preserves
its earlier versions.

---

## 7. Initial public API

### Current weekly offering

```http
GET /api/public/weekly-offerings/current
```

Possible responses:

 - 200 OK when a current published offering exists
 - 204 No Content when no offering is currently available

The public response may include:

 - Weekly-offering ID
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

### Planned weekly-offering endpoints:

```http
GET    /api/admin/weekly-offerings
```
```http
GET    /api/admin/weekly-offerings/{id}
```
```http
POST   /api/admin/weekly-offerings
```
```http
PUT    /api/admin/weekly-offerings/{id}
```
```http
POST   /api/admin/weekly-offerings/{id}/publish
```
```http
POST   /api/admin/weekly-offerings/{id}/archive
```

#### `Authentication` and `Authorization` will be added after the initial public read-only data flow works.

---

## 9. Initial implementation order

1. Apply the weeklyoffering-centered database migration.
2. Create StaffMember entity.
3. Create Recipe entity.
4. Create WeeklyofferingStatus enum.
5. Create Weeklyoffering entity.
6. Create repositories.
7. Create public response DTOs.
8. Create the weekly-offering service.
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
 - Weekly-offering scheduling
 - Public current-offering endpoint

### Phase 2
 - Authentication
 - Administrator recipe management
 - Administrator weekly-offering management

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