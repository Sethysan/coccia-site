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
                <button type="button" @click="router.push('/admin')">
                    ← Dashboard
                </button>
            </div>

            <section class="admin-card create-card">
                <div class="admin-section-heading">
                    <h2>Add Recipe</h2>
                </div>

                <form class="admin-form create-form" @submit.prevent="handleCreate">
                    <label>
                        Recipe name

                        <input v-model="newRecipeName" type="text" maxlength="150"
                            placeholder="Example: Chicken Marsala" required>
                    </label>

                    <label>
                        Description

                        <textarea v-model="newDescription" rows="3"
                            placeholder="Describe the dish for customers."></textarea>
                    </label>

                    <label>
                        Image description

                        <input v-model="newImageAlt" type="text" maxlength="255"
                            placeholder="Example: Chicken Marsala with mushrooms">
                    </label>

                    <div class="admin-form-actions">
                        <button type="submit" class="primary-button" :disabled="creating">
                            {{ creating ? 'Adding...' : 'Add Recipe' }}
                        </button>
                    </div>

                    <div v-if="successMessage && successRecipeId === null" class="admin-success-message" role="status">
                        ✓ {{ successMessage }}
                    </div>

                </form>
            </section>

            <section class="recipe-tools">
                <label class="search-field">
                    Search recipes

                    <input v-model="search" type="search" placeholder="Search by name" @input="handleSearch">
                </label>

                <div class="filter-buttons">
                    <button type="button" :class="{ active: filter === 'all' }" @click="filter = 'all'">
                        All
                    </button>

                    <button type="button" :class="{ active: filter === 'active' }" @click="filter = 'active'">
                        Active
                    </button>

                    <button type="button" :class="{ active: filter === 'inactive' }" @click="filter = 'inactive'">
                        Inactive
                    </button>
                </div>
            </section>

            <div v-if="recipeStore.error" class="error-message" role="alert">
                {{ recipeStore.error }}
            </div>

            <p v-if="recipeStore.loading" class="state-message">
                Loading recipes...
            </p>

            <p v-else-if="filteredRecipes.length === 0" class="state-message">
                No recipes found.
            </p>

            <section v-else class="recipe-list">

                <article v-for="recipe in filteredRecipes" :key="recipe.id" class="admin-card recipe-card"
                    :class="{ inactive: !recipe.active }">

                    <RecipeSummary v-if="editingId !== recipe.id" :name="recipe.name" :description="recipe.description"
                        :image-url="recipe.imageUrl" :image-alt="recipe.imageAlt">
                        <div v-if="
                            successMessage &&
                            successRecipeId === recipe.id
                        " class="admin-success-message" role="status">
                            ✓ {{ successMessage }}
                        </div>
                        <div class="recipe-meta">
                            <span class="status-badge" :class="recipe.active
                                ? 'status-current'
                                : 'status-archived'">
                                {{ recipe.active ? 'Active' : 'Inactive' }}
                            </span>
                        </div>

                        <div class="recipe-actions">
                            <label class="image-upload-button">
                                {{
                                    uploadingId === recipe.id
                                        ? 'Uploading...'
                                        : recipe.imageUrl
                                            ? 'Replace Photo'
                                            : 'Add Photo'
                                }}

                                <input type="file" accept="image/*" :disabled="uploadingId === recipe.id"
                                    @change="handleImageUpload(recipe, $event)">
                            </label>

                            <button type="button" @click="beginEdit(recipe)">
                                Edit Details
                            </button>

                            <button type="button" :disabled="savingId === recipe.id" @click="toggleActive(recipe)">
                                {{
                                    savingId === recipe.id
                                        ? 'Saving...'
                                        : recipe.active
                                            ? 'Deactivate'
                                            : 'Reactivate'
                                }}
                            </button>
                        </div>
                    </RecipeSummary>

                    <form v-else class="admin-form edit-form" @submit.prevent="handleSave(recipe)">
                        <label>
                            Recipe name

                            <input v-model="editName" type="text" maxlength="150" required>
                        </label>

                        <label>
                            Description

                            <textarea v-model="editDescription" rows="4"
                                placeholder="Describe the dish for customers."></textarea>
                        </label>

                        <label>
                            Image description

                            <input v-model="editImageAlt" type="text" maxlength="255"
                                placeholder="Describe what appears in the photo">
                        </label>

                        <div class="admin-form-actions">
                            <button type="submit" class="primary-button" :disabled="savingId === recipe.id">
                                {{
                                    savingId === recipe.id
                                        ? 'Saving...'
                                        : 'Save Details'
                                }}
                            </button>

                            <button type="button" :disabled="savingId === recipe.id" @click="cancelEdit">
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
import RecipeSummary from '@/components/admin/RecipeSummary.vue'

import { useAuthStore } from '@/stores/authStore'
import { useRecipeStore } from '@/stores/recipeStore'

const router = useRouter()
const auth = useAuthStore()
const recipeStore = useRecipeStore()

const search = ref('')
const filter = ref('all')

const newRecipeName = ref('')
const newDescription = ref('')
const newImageAlt = ref('')
const creating = ref(false)
const successMessage = ref('')
const successRecipeId = ref(null)

const editingId = ref(null)
const editName = ref('')
const editDescription = ref('')
const editImageAlt = ref('')

const savingId = ref(null)
const uploadingId = ref(null)

let searchTimer = null
let successTimer = null

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
    clearTimeout(successTimer)
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

function showSuccessMessage(message, recipeId = null) {
    clearTimeout(successTimer)

    successMessage.value = message
    successRecipeId.value = recipeId

    successTimer = setTimeout(() => {
        successMessage.value = ''
        successRecipeId.value = null
    }, 4000)
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
            await recipeStore.addRecipe(
                name,
                newDescription.value,
                newImageAlt.value
            )

        if (createdRecipe) {
            newRecipeName.value = ''
            newDescription.value = ''
            newImageAlt.value = ''

            showSuccessMessage('Recipe created successfully.')
        }

    } finally {
        creating.value = false
    }
}

function beginEdit(recipe) {
    editingId.value = recipe.id
    editName.value = recipe.name
    editDescription.value = recipe.description || ''
    editImageAlt.value = recipe.imageAlt || ''

    recipeStore.clearError()
}

function cancelEdit() {
    editingId.value = null
    editName.value = ''
    editDescription.value = ''
    editImageAlt.value = ''

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
                editDescription.value,
                editImageAlt.value,
                recipe.active
            )

        if (updatedRecipe) {
            cancelEdit()

            showSuccessMessage(
                `${updatedRecipe.name} updated successfully.`,
                updatedRecipe.id
            )
        }


    } finally {
        savingId.value = null
    }
}

async function toggleActive(recipe) {
    savingId.value = recipe.id
    recipeStore.clearError()

    try {
        const updatedRecipe =
            await recipeStore.saveRecipe(
                recipe.id,
                recipe.name,
                recipe.description || '',
                recipe.imageAlt || '',
                !recipe.active
            )

        if (updatedRecipe) {
            showSuccessMessage(
                `${updatedRecipe.name} ${updatedRecipe.active
                    ? 'reactivated'
                    : 'deactivated'
                } successfully.`,
                updatedRecipe.id
            )
        }

    } finally {
        savingId.value = null
    }
}
async function handleImageUpload(recipe, event) {
    const input = event.target
    const file = input.files?.[0]

    if (!file) {
        return
    }

    uploadingId.value = recipe.id
    recipeStore.clearError()

    try {
        const updatedRecipe =
            await recipeStore.uploadImage(
                recipe.id,
                file
            )

        if (updatedRecipe) {
            showSuccessMessage(
                `${updatedRecipe.name} photo ${recipe.imageUrl
                    ? 'replaced'
                    : 'added'
                } successfully.`,
                updatedRecipe.id
            )
        }

    } finally {
        uploadingId.value = null
        input.value = ''
    }
}

</script>

<style scoped>
.create-card {
    margin-bottom: 1rem;
}

.create-form {
    grid-template-columns: 1fr;
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

/* ==========================================================
   RECIPE DETAILS & ACTIONS
   ========================================================== */

.recipe-meta {
    margin: 0.75rem 0;
}

.recipe-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;

    margin-top: 1rem;
}

.image-upload-button {
    display: inline-flex;
    align-items: center;

    width: fit-content;
    padding: 0.65rem 1rem;

    color: #140f0c;
    background: var(--default-color);

    border: 1px solid var(--bronze-color);
    border-radius: 0.35rem;

    font-weight: 700;
    cursor: pointer;
}

.image-upload-button:hover {
    background: var(--bronze-hover);
}

.image-upload-button input {
    position: absolute;

    width: 1px;
    height: 1px;

    opacity: 0;
    pointer-events: none;
}

.edit-form .admin-form-actions {
    margin-top: 0;
}

@media (max-width: 700px) {
    .create-form {
        grid-template-columns: 1fr;
    }

    .recipe-tools {
        flex-direction: column;
        align-items: stretch;
    }

    .search-field {
        max-width: none;
    }
}
</style>