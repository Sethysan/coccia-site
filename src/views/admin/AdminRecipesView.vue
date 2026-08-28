<template>
    <main class="admin-page">
        <div class="admin-page-container">

            <header class="page-header">
                <div>
                    <p class="admin-eyebrow">
                        Coccia House Admin
                    </p>

                    <h1>Recipes</h1>

                    <p class="admin-subtext">
                        Manage reusable dishes used by weekly features.
                    </p>
                </div>

                <button type="button" @click="handleLogout">
                    Log Out
                </button>
            </header>

            <div class="page-actions">
                <button
                    type="button"
                    @click="router.push('/admin')"
                >
                    ← Dashboard
                </button>
            </div>

            <section class="admin-card create-card">
                <div class="admin-section-heading">
                    <h2>Add Recipe</h2>
                </div>

                <form
                    class="admin-form create-form"
                    @submit.prevent="handleCreate"
                >
                    <label>
                        Recipe name

                        <input
                            v-model="newRecipeName"
                            type="text"
                            maxlength="150"
                            placeholder="Example: Chicken Marsala"
                            required
                        >
                    </label>

                    <div class="admin-form-actions">
                        <button
                            type="submit"
                            class="primary-button"
                            :disabled="creating"
                        >
                            {{ creating ? 'Adding...' : 'Add Recipe' }}
                        </button>
                    </div>
                </form>
            </section>

            <section class="recipe-tools">
                <label class="search-field">
                    Search recipes

                    <input
                        v-model="search"
                        type="search"
                        placeholder="Search by name"
                        @input="handleSearch"
                    >
                </label>

                <div class="filter-buttons">
                    <button
                        type="button"
                        :class="{ active: filter === 'all' }"
                        @click="filter = 'all'"
                    >
                        All
                    </button>

                    <button
                        type="button"
                        :class="{ active: filter === 'active' }"
                        @click="filter = 'active'"
                    >
                        Active
                    </button>

                    <button
                        type="button"
                        :class="{ active: filter === 'inactive' }"
                        @click="filter = 'inactive'"
                    >
                        Inactive
                    </button>
                </div>
            </section>

            <div
                v-if="recipeStore.error"
                class="error-message"
                role="alert"
            >
                {{ recipeStore.error }}
            </div>

            <p
                v-if="recipeStore.loading"
                class="state-message"
            >
                Loading recipes...
            </p>

            <p
                v-else-if="filteredRecipes.length === 0"
                class="state-message"
            >
                No recipes found.
            </p>

            <section
                v-else
                class="recipe-list"
            >
                <article
                    v-for="recipe in filteredRecipes"
                    :key="recipe.id"
                    class="admin-card recipe-card"
                    :class="{ inactive: !recipe.active }"
                >
                    <div
                        v-if="editingId !== recipe.id"
                        class="recipe-display"
                    >
                        <div class="recipe-heading">
                            <h2>
                                {{ recipe.name }}
                            </h2>

                            <span
                                class="status-badge"
                                :class="recipe.active
                                    ? 'status-current'
                                    : 'status-archived'"
                            >
                                {{
                                    recipe.active
                                        ? 'Active'
                                        : 'Inactive'
                                }}
                            </span>
                        </div>

                        <div class="recipe-actions">
                            <button
                                type="button"
                                @click="beginEdit(recipe)"
                            >
                                Edit
                            </button>

                            <button
                                type="button"
                                :disabled="savingId === recipe.id"
                                @click="toggleActive(recipe)"
                            >
                                {{
                                    savingId === recipe.id
                                        ? 'Saving...'
                                        : recipe.active
                                            ? 'Deactivate'
                                            : 'Reactivate'
                                }}
                            </button>
                        </div>
                    </div>

                    <form
                        v-else
                        class="admin-form edit-form"
                        @submit.prevent="handleSave(recipe)"
                    >
                        <label>
                            Recipe name

                            <input
                                v-model="editName"
                                type="text"
                                maxlength="150"
                                required
                            >
                        </label>

                        <div class="admin-form-actions">
                            <button
                                type="submit"
                                class="primary-button"
                                :disabled="savingId === recipe.id"
                            >
                                {{
                                    savingId === recipe.id
                                        ? 'Saving...'
                                        : 'Save'
                                }}
                            </button>

                            <button
                                type="button"
                                :disabled="savingId === recipe.id"
                                @click="cancelEdit"
                            >
                                Cancel
                            </button>
                        </div>
                    </form>
                </article>
            </section>

        </div>
    </main>
</template>

<script setup>
import {
    computed,
    onBeforeUnmount,
    onMounted,
    ref
} from 'vue'
import { useRouter } from 'vue-router'

import { useAuthStore } from '@/stores/authStore'
import { useRecipeStore } from '@/stores/recipeStore'

const router = useRouter()
const auth = useAuthStore()
const recipeStore = useRecipeStore()

const search = ref('')
const filter = ref('all')

const newRecipeName = ref('')
const creating = ref(false)

const editingId = ref(null)
const editName = ref('')
const savingId = ref(null)

let searchTimer = null

const filteredRecipes = computed(() => {
    if (filter.value === 'active') {
        return recipeStore.recipes.filter(
            recipe => recipe.active
        )
    }

    if (filter.value === 'inactive') {
        return recipeStore.recipes.filter(
            recipe => !recipe.active
        )
    }

    return recipeStore.recipes
})

onMounted(async () => {
    await recipeStore.fetchRecipes()
})

onBeforeUnmount(() => {
    clearTimeout(searchTimer)
})

async function handleLogout() {
    await auth.logout()
    await router.push('/admin/login')
}

function handleSearch() {
    clearTimeout(searchTimer)

    searchTimer = setTimeout(() => {
        recipeStore.fetchRecipes(search.value)
    }, 300)
}

async function handleCreate() {
    const name = newRecipeName.value.trim()

    if (!name) {
        return
    }

    creating.value = true
    recipeStore.clearError()

    try {
        const createdRecipe =
            await recipeStore.addRecipe(name)

        if (createdRecipe) {
            newRecipeName.value = ''
        }

    } finally {
        creating.value = false
    }
}

function beginEdit(recipe) {
    editingId.value = recipe.id
    editName.value = recipe.name
    recipeStore.clearError()
}

function cancelEdit() {
    editingId.value = null
    editName.value = ''
    recipeStore.clearError()
}

async function handleSave(recipe) {
    const name = editName.value.trim()

    if (!name) {
        return
    }

    savingId.value = recipe.id
    recipeStore.clearError()

    try {
        const updatedRecipe =
            await recipeStore.saveRecipe(
                recipe.id,
                name,
                recipe.active
            )

        if (updatedRecipe) {
            cancelEdit()
        }

    } finally {
        savingId.value = null
    }
}

async function toggleActive(recipe) {
    savingId.value = recipe.id
    recipeStore.clearError()

    try {
        await recipeStore.saveRecipe(
            recipe.id,
            recipe.name,
            !recipe.active
        )

    } finally {
        savingId.value = null
    }
}
</script>

<style scoped>
.create-card {
    margin-bottom: 1rem;
}

.create-form {
    grid-template-columns: minmax(0, 1fr) auto;
    align-items: end;
}

.create-form .admin-form-actions {
    margin-top: 0;
}

.recipe-tools {
    display: flex;
    justify-content: space-between;
    align-items: end;
    gap: 1rem;

    margin: 1.5rem 0;
}

.search-field {
    display: grid;
    gap: 0.4rem;

    flex: 1;
    max-width: 32rem;
}

.filter-buttons {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
}

.filter-buttons button.active {
    background: var(--bronze-bold);
    border-color: var(--bronze-hover);
}

.recipe-list {
    display: grid;
    gap: 0.75rem;
}

.recipe-card {
    transition: opacity 180ms ease;
}

.recipe-card.inactive {
    opacity: 0.65;
}

.recipe-display {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 1rem;
}

.recipe-heading {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 0.75rem;
}

.recipe-heading h2 {
    margin: 0;
}

.recipe-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
}

.edit-form .admin-form-actions {
    margin-top: 0;
}

@media (max-width: 700px) {
    .create-form {
        grid-template-columns: 1fr;
    }

    .recipe-tools,
    .recipe-display {
        flex-direction: column;
        align-items: stretch;
    }

    .search-field {
        max-width: none;
    }
}
</style>