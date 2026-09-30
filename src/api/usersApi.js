import { apiRequest } from "@/api/apiClient"

export const getAdminUsers = () => {
  return apiRequest("/api/admin/users", {
    method: "GET"
  })
}

export const createAdminUser = (user) => {
  return apiRequest("/api/admin/users", {
    method: "POST",
    body: JSON.stringify(user)
  })
}

export const updateAdminUser = (id, user) => {
  return apiRequest(`/api/admin/users/${id}`, {
    method: "PUT",
    body: JSON.stringify(user)
  })
}

export const resetAdminUserPassword = (id, password) => {
  return apiRequest(`/api/admin/users/${id}/password`, {
    method: "PUT",
    body: JSON.stringify({
      password
    })
  })
}