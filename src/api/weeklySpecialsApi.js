import { apiRequest } from "@/api/apiClient"

export const getCurrentWeeklySpecial = () => {
  return apiRequest("/api/public/weekly-specials/current")
}

/*
 * TODO: ADMIN PHASE
 *
 * These methods should not be activated until authentication
 * and backend authorization are complete.
 */

// export const getAllWeeklySpecials = () => {
//   return apiRequest("/api/admin/weekly-specials")
// }

// export const createWeeklySpecial = (special) => {
//   return apiRequest("/api/admin/weekly-specials", {
//     method: "POST",
//     body: JSON.stringify(special)
//   })
// }

// export const updateWeeklySpecial = (id, special) => {
//   return apiRequest(`/api/admin/weekly-specials/${id}`, {
//     method: "PUT",
//     body: JSON.stringify(special)
//   })
// }

// export const deactivateWeeklySpecial = (id) => {
//   return apiRequest(`/api/admin/weekly-specials/${id}/deactivate`, {
//     method: "PATCH"
//   })
// }