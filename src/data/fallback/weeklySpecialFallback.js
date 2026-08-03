/*
 * WHITE NOTE:
 * This content is used only when:
 *
 * 1. Remote content is enabled.
 * 2. The API request fails.
 * 3. Frontend fallback content is allowed.
 *
 * Keep this message neutral so an API outage never displays expired,
 * incorrect, or misleading pricing.
 */

export const weeklySpecialFallback = {
  id: "weekly-special-fallback",
  title: "Weekly Special",
  description: "Please call us for information about this week's special.",
  price: null,
  imageUrl: null,
  imageAlt: "",
  startDate: null,
  endDate: null,
  active: false
}