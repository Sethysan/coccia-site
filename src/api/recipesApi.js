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

export async function getLatestRecipeOfferingItem(recipeId) {
    const response = await fetch(
        `/api/admin/recipes/${recipeId}/latest-offering-item`,
        {
            method: 'GET',
            credentials: 'include'
        }
    )

    if (response.status === 204) {
        return null
    }

    if (!response.ok) {
        throw new Error(
            'Unable to load previous recipe details.'
        )
    }

    return response.json()
}
