import { apiRequest } from '@/api/apiClient'

export async function getRecipes(search = '') {
    const params = new URLSearchParams()

    if (search.trim()) {
        params.set('search', search.trim())
    }

    const query = params.toString()

    return apiRequest(
        query
            ? `/api/admin/recipes?${query}`
            : '/api/admin/recipes'
    )
}

export async function getActiveRecipes(search = '') {
    const query = search.trim()
        ? `?search=${encodeURIComponent(search.trim())}`
        : ''

    return apiRequest(
        `/api/admin/recipes/active-recipes${query}`
    )
}

export async function createRecipe(recipe) {
    return apiRequest('/api/admin/recipes', {
        method: 'POST',
        body: JSON.stringify(recipe)
    })
}

export async function updateRecipe(id, recipe) {
    return apiRequest(`/api/admin/recipes/${id}`, {
        method: 'PUT',
        body: JSON.stringify(recipe)
    })
}

export async function uploadRecipeImage(id, file) {
    const formData = new FormData()

    formData.append('file', file)

    return apiRequest(`/api/admin/recipes/${id}/image`, {
        method: 'POST',
        body: formData
    })
}

export async function getLatestRecipeOfferingItem(recipeId) {
    return apiRequest(
        `/api/admin/recipes/${recipeId}/latest-offering-item`
    )
}