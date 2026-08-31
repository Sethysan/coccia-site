import { defineStore } from 'pinia'
import { ref } from 'vue'

import {
    getRecipes,
    getActiveRecipes,
    createRecipe,
    updateRecipe,
    uploadRecipeImage,
    getLatestRecipeOfferingItem
} from '@/api/recipesApi'

export const useRecipeStore = defineStore('recipes', () => {
    const recipes = ref([])
    const loading = ref(false)
    const error = ref(null)

    function clearError() {
        error.value = null
    }

    async function fetchRecipes(search = '') {
        loading.value = true
        error.value = null

        try {
            recipes.value = await getRecipes(search)

        } catch (err) {
            console.error(err)

            recipes.value = []
            error.value = err.message

        } finally {
            loading.value = false
        }
    }

    async function addRecipe(name, description = '', imageAlt = '') {
        error.value = null

        try {
            const createdRecipe = await createRecipe({
                name,
                description,
                imageAlt
            })

            recipes.value = [
                ...recipes.value,
                createdRecipe
            ].sort((a, b) =>
                a.name.localeCompare(b.name)
            )

            return createdRecipe

        } catch (err) {
            console.error(err)
            error.value = err.message

            return null
        }
    }
    function replaceRecipe(updatedRecipe) {
        recipes.value = recipes.value
            .map(recipe =>
                recipe.id === updatedRecipe.id
                    ? updatedRecipe
                    : recipe
            )
            .sort((a, b) =>
                a.name.localeCompare(b.name)
            )
    }

    async function saveRecipe(
        id,
        name,
        description,
        imageAlt,
        active
    ) {
        error.value = null

        try {
            const updatedRecipe = await updateRecipe(
                id,
                {
                    name,
                    description,
                    imageAlt,
                    active
                }
            )

            replaceRecipe(updatedRecipe)

            return updatedRecipe

        } catch (err) {
            console.error(err)
            error.value = err.message

            return null
        }
    }

    async function uploadImage(id, file) {
        error.value = null

        try {
            const updatedRecipe =
                await uploadRecipeImage(id, file)

            replaceRecipe(updatedRecipe)

            return updatedRecipe

        } catch (err) {
            console.error(err)
            error.value = err.message

            return null
        }
    }

    async function fetchLatestOfferingItem(recipeId) {
        error.value = null

        try {
            return await getLatestRecipeOfferingItem(recipeId)

        } catch (err) {
            console.error(err)
            error.value = err.message
            throw err
        }
    }

    return {
        recipes,
        loading,
        error,
        fetchRecipes,
        addRecipe,
        saveRecipe,
        uploadImage,
        fetchLatestOfferingItem,
        clearError
    }
})