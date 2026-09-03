import { apiRequest } from '@/api/apiClient'

export async function getMenuSections() {
    return apiRequest('/api/admin/menu/sections')
}

export async function createMenuSection(section) {
    return apiRequest('/api/admin/menu/sections', {
        method: 'POST',
        body: JSON.stringify(section)
    })
}

export async function updateMenuSection(id, section) {
    return apiRequest(`/api/admin/menu/sections/${id}`, {
        method: 'PUT',
        body: JSON.stringify(section)
    })
}

export async function getMenuItems(sectionId) {
    return apiRequest(
        `/api/admin/menu/sections/${sectionId}/items`
    )
}

export async function createMenuItem(sectionId, item) {
    return apiRequest(
        `/api/admin/menu/sections/${sectionId}/items`,
        {
            method: 'POST',
            body: JSON.stringify(item)
        }
    )
}

export async function updateMenuItem(
    sectionId,
    menuItemId,
    item
) {
    return apiRequest(
        `/api/admin/menu/sections/${sectionId}/items/${menuItemId}`,
        {
            method: 'PUT',
            body: JSON.stringify(item)
        }
    )
}