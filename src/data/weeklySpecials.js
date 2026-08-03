/*
 * WHITE NOTE:
 * This is currently the production source of truth.
 *
 * Do not remove this file when the backend is connected.
 * It can remain as an emergency fallback while the API architecture matures.
 *
 * TODO:
 * Replace manual imports from this file with weeklySpecialService.
 */

export const localWeeklySpecial = {
  id: "local-weekly-special",
  title: "Weekly Special",
  description: "Check back soon for this week's featured special.",
  price: null,
  imageUrl: null,
  imageAlt: "",
  startDate: "2026-07-29",
  endDate: "2026-08-02",
  active: false
}