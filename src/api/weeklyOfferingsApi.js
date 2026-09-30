import { apiRequest } from "@/api/apiClient"

// -----------------------------------------------------------------------------
// Public
// -----------------------------------------------------------------------------

export const getCurrentWeeklyOffering = () => {
  return apiRequest("/api/public/weekly-offerings/current", {
    method: "GET"
  })
}

// -----------------------------------------------------------------------------
// Admin - Weekly Offerings
// -----------------------------------------------------------------------------

export const getWeeklyOfferings = (status = null) => {
  const params = new URLSearchParams()

  if (status) {
    params.set("status", status)
  }

  const query = params.toString()

  const endpoint = query
    ? `/api/admin/weekly-offerings?${query}`
    : "/api/admin/weekly-offerings"

  return apiRequest(endpoint, {
    method: "GET"
  })
}

export const getWeeklyOfferingById = (id) => {
  return apiRequest(
    `/api/admin/weekly-offerings/${id}`,
    {
      method: "GET"
    }
  )
}

export const createWeeklyOffering = ({
  startDate,
  endDate
}) => {
  return apiRequest(
    "/api/admin/weekly-offerings",
    {
      method: "POST",
      body: JSON.stringify({
        startDate,
        endDate
      })
    }
  )
}

export const updateWeeklyOfferingDates = (
  id,
  {
    startDate,
    endDate
  }
) => {
  return apiRequest(
    `/api/admin/weekly-offerings/${id}`,
    {
      method: "PUT",
      body: JSON.stringify({
        startDate,
        endDate
      })
    }
  )
}

export const scheduleWeeklyOffering = (id) => {
  return apiRequest(
    `/api/admin/weekly-offerings/${id}/schedule`,
    {
      method: "PUT"
    }
  )
}

export const archiveWeeklyOffering = (id) => {
  return apiRequest(
    `/api/admin/weekly-offerings/${id}/archive`,
    {
      method: "PUT"
    }
  )
}

export const deleteWeeklyOffering = (id) => {
  return apiRequest(
    `/api/admin/weekly-offerings/${id}`,
    {
      method: "DELETE"
    }
  )
}

// -----------------------------------------------------------------------------
// Admin - Weekly Offering Items
// -----------------------------------------------------------------------------

export const addWeeklyOfferingItem = (
  offeringId,
  item
) => {
  return apiRequest(
    `/api/admin/weekly-offerings/${offeringId}/items`,
    {
      method: "POST",
      body: JSON.stringify(item)
    }
  )
}

export const updateWeeklyOfferingItem = (
  offeringId,
  itemId,
  item
) => {
  return apiRequest(
    `/api/admin/weekly-offerings/${offeringId}/items/${itemId}`,
    {
      method: "PUT",
      body: JSON.stringify(item)
    }
  )
}

export const deleteWeeklyOfferingItem = (
  offeringId,
  itemId
) => {
  return apiRequest(
    `/api/admin/weekly-offerings/${offeringId}/items/${itemId}`,
    {
      method: "DELETE"
    }
  )
}