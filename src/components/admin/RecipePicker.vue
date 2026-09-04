<template>
    <div>
        <label for="recipe">
            Recipe
        </label>

        <div class="recipe-picker">
            <input
                id="recipe"
                v-model="recipeSearch"
                type="search"
                placeholder="Search recipes..."
                autocomplete="off"
                @input="handleRecipeSearch"
                @focus="showRecipeResults = true"
            >

            <div
                v-if="showRecipeResults"
                class="recipe-results"
            >
                <p
                    v-if="searchingRecipes"
                    class="recipe-results-message"
                >
                    Searching...
                </p>

                <button
                    v-for="recipe in activeRecipes"
                    v-else
                    :key="recipe.id"
                    type="button"
                    class="recipe-result"
                    @click="selectRecipe(recipe)"
                >
                    {{ recipe.name }}
                </button>

                <p
                    v-if="
                        !searchingRecipes
                        && activeRecipes.length === 0
                    "
                    class="recipe-results-message"
                >
                    No active recipes found.
                </p>
            </div>

            <button
                v-if="selectedRecipe"
                type="button"
                class="change-recipe-button"
                @click="clearSelectedRecipe"
            >
                Change Recipe
            </button>

            <p v-if="recipeSearchError">
                {{ recipeSearchError }}
            </p>
        </div>

        <div
            v-if="selectedRecipe"
            class="selected-recipe"
        >
            <p class="selected-recipe-label">
                Recipe Details
            </p>

            <RecipeSummary
                :name="selectedRecipe.name"
                :description="selectedRecipe.description"
                :image-url="selectedRecipe.imageUrl"
                :image-alt="selectedRecipe.imageAlt"
            />

            <p class="recipe-source-note">
                Description and photo are managed in Recipes.
            </p>
        </div>

        <p v-if="recipeStore.loading">
            Loading recipes...
        </p>

        <p v-if="recipeStore.error">
            {{ recipeStore.error }}
        </p>
    </div>
</template>

<script setup>
import {
    computed,
    onBeforeUnmount,
    onMounted,
    ref,
    watch
} from 'vue'

import { useRecipeStore } from '@/stores/recipeStore'
import { getActiveRecipes } from '@/api/recipesApi'
import RecipeSummary from '@/components/admin/RecipeSummary.vue'

const props = defineProps({
    modelValue: {
        type: Number,
        default: null
    }
})

const emit = defineEmits([
    'update:modelValue',
    'selected',
    'cleared'
])

const recipeStore = useRecipeStore()

const recipeSearch = ref('')
const activeRecipes = ref([])
const searchingRecipes = ref(false)
const recipeSearchError = ref('')
const showRecipeResults = ref(false)

let recipeSearchTimer = null

const selectedRecipe = computed(() => {
    if (!props.modelValue) {
        return null
    }

    return activeRecipes.value.find(
        recipe => recipe.id === props.modelValue
    ) ?? recipeStore.recipes.find(
        recipe => recipe.id === props.modelValue
    ) ?? null
})

onMounted(async () => {
    await Promise.all([
        recipeStore.fetchRecipes(),
        loadActiveRecipes()
    ])

    syncRecipeSearch()
})

watch(
    () => props.modelValue,
    () => {
        syncRecipeSearch()
    }
)

onBeforeUnmount(() => {
    clearTimeout(recipeSearchTimer)
})

function syncRecipeSearch() {
    if (!props.modelValue) {
        recipeSearch.value = ''
        return
    }

    if (selectedRecipe.value) {
        recipeSearch.value = selectedRecipe.value.name
    }
}

async function loadActiveRecipes(search = '') {
    searchingRecipes.value = true
    recipeSearchError.value = ''

    try {
        activeRecipes.value =
            await getActiveRecipes(search)
    } catch (error) {
        recipeSearchError.value =
            error.message || 'Unable to load recipes.'
    } finally {
        searchingRecipes.value = false
    }
}

function handleRecipeSearch() {
    clearTimeout(recipeSearchTimer)

    emit('update:modelValue', null)

    showRecipeResults.value = true

    recipeSearchTimer = setTimeout(() => {
        loadActiveRecipes(recipeSearch.value)
    }, 300)
}

function selectRecipe(recipe) {
    recipeSearch.value = recipe.name
    recipeSearchError.value = ''
    showRecipeResults.value = false

    emit('update:modelValue', recipe.id)
    emit('selected', recipe)
}

async function clearSelectedRecipe() {
    emit('update:modelValue', null)
    emit('cleared')

    recipeSearch.value = ''
    showRecipeResults.value = true

    await loadActiveRecipes()
}
</script>

<style scoped>
.recipe-picker {
    position: relative;

    display: grid;
    gap: 0.5rem;
}

.recipe-picker input {
    width: 100%;
}

.recipe-results {
    display: grid;

    max-height: 240px;
    overflow-y: auto;

    background: #140f0c;

    border: 1px solid var(--bronze-color);
    border-radius: 0.4rem;
}

.recipe-result {
    width: 100%;
    padding: 0.75rem 0.8rem;

    color: var(--default-color);
    background: transparent;

    border: 0;
    border-bottom: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 0;

    text-align: left;
}

.recipe-result:last-child {
    border-bottom: 0;
}

.recipe-result:hover {
    background: rgba(255, 255, 255, 0.08);
}

.recipe-results-message {
    padding: 0.75rem 0.8rem;
}

.change-recipe-button {
    width: fit-content;
    margin-top: 0.25rem;
}

.selected-recipe {
    margin-top: 0.75rem;
    padding: 1rem;

    background: rgba(255, 255, 255, 0.04);

    border: 1px solid rgba(255, 255, 255, 0.12);
    border-radius: 0.5rem;
}

.selected-recipe-label {
    margin: 0 0 0.75rem;

    color: var(--bronze-bold);

    font-size: 0.75rem;
    font-weight: 700;
    letter-spacing: 0.1em;
    text-transform: uppercase;
}

.recipe-source-note {
    margin: 0.75rem 0 0;

    font-size: 0.8rem;
    opacity: 0.65;
}
</style>