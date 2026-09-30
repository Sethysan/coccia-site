import { apiRequest } from "@/api/apiClient"

export const getRestaurantHours = () => {
  return apiRequest("/api/public/hours", {
    method: "GET"
  })
}

export const updateRestaurantHours = (dayOfWeek, hours) => {
  return apiRequest(`/api/admin/hours/${dayOfWeek}`, {
    method: "PUT",
    body: JSON.stringify(hours)
  })
}