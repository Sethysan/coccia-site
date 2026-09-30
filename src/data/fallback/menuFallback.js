// -----------------------------------------------------------------------------
// Menu Fallback
//
// This file contains the static fallback configuration for the public menu.
//
// The database-backed menu is the primary source of menu content. If the public
// menu API cannot be reached or does not return a usable menu, the site should
// fall back to this configuration so customers can still view the restaurant
// menu.
//
// The fallback menu images themselves live in:
//   src/assets/menu/
//
// Keep this fallback independent of the database and external services. It is
// intentionally bundled with the frontend so the menu remains available during
// a backend, database, or network outage.
//
// IMPORTANT:
// When the restaurant menu changes, update both the database-backed menu and
// these fallback assets/configuration so the emergency fallback does not become
// outdated.
// -----------------------------------------------------------------------------

// TODO:
// -----------------------------------------------------------------------------
// CURRENT MENU FALLBACK IMPLEMENTATION
//
// NOTE: The menu fallback has not yet been moved into this file.
//
// Currently, the fallback menu is implemented directly inside MenuView.vue.
// MenuView imports five static menu images from:
//
//   src/assets/menu/
//     - starters-salads.png
//     - pizza-pasta.png
//     - house-favorites.png
//     - sandwiches.png
//     - desserts-drinks.png
//
// MenuView defines these images in its local `menuPages` array.
//
// When the Menu page loads, it first attempts to retrieve the database-backed
// menu through `getPublicMenu()`. If the API returns a valid menu containing
// sections, MenuView renders the database version using LiveMenu.vue.
//
// If the API request fails or does not produce a usable menu,
// `liveMenuAvailable` remains false. MenuView then renders the original
// image-based menu using its local `menuPages` configuration.
//
// Current flow:
//
//   PostgreSQL
//       ↓
//   Railway API
//       ↓
//   getPublicMenu()
//       ↓
//   Valid menu? ── YES ──→ LiveMenu.vue
//       │
//       NO
//       ↓
//   MenuView.vue `menuPages`
//       ↓
//   src/assets/menu/*.png
//
// This means the public menu can still be viewed if the backend, database,
// or network request is unavailable.
//
// FUTURE CLEANUP:
// Move the `menuPages` fallback configuration out of MenuView.vue and into
// this file. MenuView should then import the fallback from here rather than
// defining it locally. The static fallback images can remain in
// `src/assets/menu/`.
//
// Until that refactor is completed, this file is documentation/placeholding
// only and is NOT currently responsible for the menu fallback.
// -----------------------------------------------------------------------------