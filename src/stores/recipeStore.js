import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useRecipeStore = defineStore('recipes', () => {
    const recipes = ref([])
    const loading = ref(false)
    const error = ref(null)

    async function fetchRecipes(search = '') {
        loading.value = true
        error.value = null

        try {
            const params = new URLSearchParams()

            if (search.trim()) {
                params.set('search', search.trim())
            }

            const query = params.toString()

            const url = query
                ? `/api/admin/recipes?${query}`
                : '/api/admin/recipes'

            const response = await fetch(url, {
                method: 'GET',
                credentials: 'include'
            })

            if (!response.ok) {
                throw new Error('Unable to load recipes.')
            }

            recipes.value = await response.json()

        } catch (err) {
            console.error(err)

            recipes.value = []
            error.value = err.message

        } finally {
            loading.value = false
        }
    }

    return {
        recipes,
        loading,
        error,
        fetchRecipes,
        fetchLatestOfferingItem
    }

    async function fetchLatestOfferingItem(recipeId) {
        error.value = null

        try {
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

            return await response.json()

        } catch (err) {
            console.error(err)
            error.value = err.message
            throw err
        }
    }

})