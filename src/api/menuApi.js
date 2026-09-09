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

export async function moveMenuItem(
    sectionId,
    menuItemId,
    direction
) {
    return apiRequest(
        `/api/admin/menu/sections/${sectionId}/items/${menuItemId}/move?direction=${direction}`,
        {
            method: 'PUT'
        }
    )
}

// =========================
// Pizza Sizes
// =========================

export function getPizzaSizes(menuSectionId) {
  return apiRequest(
    `/api/admin/menu/sections/${menuSectionId}/pizza/sizes`
  )
}

export function createPizzaSize(menuSectionId, data) {
  return apiRequest(
    `/api/admin/menu/sections/${menuSectionId}/pizza/sizes`,
    {
      method: 'POST',
      body: JSON.stringify(data),
    }
  )
}

export function updatePizzaSize(
  menuSectionId,
  pizzaSizeId,
  data
) {
  return apiRequest(
    `/api/admin/menu/sections/${menuSectionId}/pizza/sizes/${pizzaSizeId}`,
    {
      method: 'PUT',
      body: JSON.stringify(data),
    }
  )
}

// =========================
// Pizza Add-ons / Pricing
// =========================

export function getPizzaAddOns(menuSectionId) {
  return apiRequest(
    `/api/admin/menu/sections/${menuSectionId}/pizza/add-ons`
  )
}

export function createPizzaAddOn(menuSectionId, data) {
  return apiRequest(
    `/api/admin/menu/sections/${menuSectionId}/pizza/add-ons`,
    {
      method: 'POST',
      body: JSON.stringify(data),
    }
  )
}

export function updatePizzaAddOn(
  menuSectionId,
  pizzaAddOnId,
  data
) {
  return apiRequest(
    `/api/admin/menu/sections/${menuSectionId}/pizza/add-ons/${pizzaAddOnId}`,
    {
      method: 'PUT',
      body: JSON.stringify(data),
    }
  )
}


// =========================
// Pizza Toppings
// =========================

export function getPizzaToppings(menuSectionId) {
  return apiRequest(
    `/api/admin/menu/sections/${menuSectionId}/pizza/toppings`
  )
}

export function createPizzaTopping(menuSectionId, data) {
  return apiRequest(
    `/api/admin/menu/sections/${menuSectionId}/pizza/toppings`,
    {
      method: 'POST',
      body: JSON.stringify(data),
    }
  )
}

export function updatePizzaTopping(
  menuSectionId,
  pizzaToppingId,
  data
) {
  return apiRequest(
    `/api/admin/menu/sections/${menuSectionId}/pizza/toppings/${pizzaToppingId}`,
    {
      method: 'PUT',
      body: JSON.stringify(data),
    }
  )
}


// =========================
// Specialty Pizzas
// =========================

export function getPizzaSpecialties(menuSectionId) {
  return apiRequest(
    `/api/admin/menu/sections/${menuSectionId}/pizza/specialties`
  )
}

export function createPizzaSpecialty(menuSectionId, data) {
  return apiRequest(
    `/api/admin/menu/sections/${menuSectionId}/pizza/specialties`,
    {
      method: 'POST',
      body: JSON.stringify(data),
    }
  )
}

export function updatePizzaSpecialty(
  menuSectionId,
  specialtyId,
  data
) {
  return apiRequest(
    `/api/admin/menu/sections/${menuSectionId}/pizza/specialties/${specialtyId}`,
    {
      method: 'PUT',
      body: JSON.stringify(data),
    }
  )
}