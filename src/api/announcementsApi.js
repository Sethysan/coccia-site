import { apiRequest } from "@/api/apiClient"

// -----------------------------------------------------------------------------
// Public
// -----------------------------------------------------------------------------

export const getCurrentAnnouncements = () => {
    return apiRequest(
        "/api/public/announcements",
        {
            method: "GET"
        }
    )
}

// -----------------------------------------------------------------------------
// Admin
// -----------------------------------------------------------------------------

export const getAnnouncements = () => {
    return apiRequest(
        "/api/admin/announcements",
        {
            method: "GET"
        }
    )
}

export const getAnnouncementById = (id) => {
    return apiRequest(
        `/api/admin/announcements/${id}`,
        {
            method: "GET"
        }
    )
}

export const createAnnouncement = (announcement) => {
    return apiRequest(
        "/api/admin/announcements",
        {
            method: "POST",
            body: JSON.stringify(announcement)
        }
    )
}

export const updateAnnouncement = (
    id,
    announcement
) => {
    return apiRequest(
        `/api/admin/announcements/${id}`,
        {
            method: "PUT",
            body: JSON.stringify(announcement)
        }
    )
}

export const scheduleAnnouncement = (id) => {
    return apiRequest(
        `/api/admin/announcements/${id}/schedule`,
        {
            method: "POST"
        }
    )
}

export const archiveAnnouncement = (id) => {
    return apiRequest(
        `/api/admin/announcements/${id}/archive`,
        {
            method: "POST"
        }
    )
}

export const deleteAnnouncement = (id) => {
    return apiRequest(
        `/api/admin/announcements/${id}`,
        {
            method: "DELETE"
        }
    )
}