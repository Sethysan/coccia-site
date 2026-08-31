<template>
    <form @submit.prevent="submitForm">
        <h3>
            {{ editing ? 'Edit Weekly Offering Item' : 'Add Weekly Offering Item' }}
        </h3>

        <div>
            <label for="recipe">
                Recipe
            </label>

            <div class="recipe-picker">
                <input id="recipe" v-model="recipeSearch" type="search" placeholder="Search recipes..."
                    autocomplete="off" @input="handleRecipeSearch" @focus="showRecipeResults = true" />

                <div v-if="showRecipeResults" class="recipe-results">
                    <p v-if="searchingRecipes" class="recipe-results-message">
                        Searching...
                    </p>

                    <button v-for="recipe in activeRecipes" v-else :key="recipe.id" type="button" class="recipe-result"
                        @click="selectRecipe(recipe)">
                        {{ recipe.name }}
                    </button>

                    <p v-if="
                        !searchingRecipes &&
                        activeRecipes.length === 0
                    " class="recipe-results-message">
                        No active recipes found.
                    </p>
                </div>

                <button v-if="selectedRecipe" type="button" class="change-recipe-button" @click="clearSelectedRecipe">
                    Change Recipe
                </button>

                <p v-if="recipeSearchError">
                    {{ recipeSearchError }}
                </p>
            </div>

            <div v-if="selectedRecipe" class="selected-recipe">
                <p class="selected-recipe-label">
                    Recipe Details
                </p>

                <RecipeSummary :name="selectedRecipe.name" :description="selectedRecipe.description"
                    :image-url="selectedRecipe.imageUrl" :image-alt="selectedRecipe.imageAlt" />

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

        <div>
            <label for="offering-type">
                Type
            </label>

            <select id="offering-type" v-model="form.offeringType" required>
                <option disabled value="">
                    Select a type
                </option>

                <option value="DINNER">Dinner</option>
                <option value="SOUP">Soup</option>
                <option value="DESSERT">Dessert</option>
            </select>
        </div>

        <div>
            <h4>Prices</h4>
            <div v-for="(price, index) in form.prices" :key="index" class="price-row">
                <div class="price-field">
                    <label>
                        Label
                    </label>

                    <input v-model="price.label" type="text" placeholder="Optional, e.g. Cup" />
                </div>

                <div class="price-field price-amount">
                    <label>
                        Amount
                    </label>

                    <input v-model="price.amount" type="number" min="0.01" step="0.01" required />
                </div>

                <button v-if="form.prices.length > 1" type="button" @click="removePrice(index)">
                    Remove Price
                </button>
            </div>

            <button type="button" class="add-price-button" @click="addPrice">
                + Add Size / Price
            </button>

            <fieldset v-if="form.offeringType === 'DINNER'">
                <legend>Dinner Includes</legend>

                <label>
                    <input v-model="form.includesHouseSalad" type="checkbox" />
                    House Salad
                </label>

                <label>
                    <input v-model="form.includesHomemadeBread" type="checkbox" />
                    Homemade Bread
                </label>
            </fieldset>

        </div>
        <button type="submit" :disabled="saving">
            {{
                saving
                    ? 'Saving...'
                    : editing
                        ? 'Save Changes'
                        : 'Save Item'
            }}
        </button>

        <button type="button" @click="$emit('cancel')">
            Cancel
        </button>
    </form>
</template>

<script setup>
import {
    computed,
    onBeforeUnmount,
    onMounted,
    reactive,
    ref,
    watch
} from 'vue'
import { useRecipeStore } from '@/stores/recipeStore'
import { getActiveRecipes } from '@/api/recipesApi'
import RecipeSummary from '@/components/admin/RecipeSummary.vue'

const props = defineProps({
    item: {
        type: Object,
        default: null
    },

    saving: {
        type: Boolean,
        default: false
    }
})

const emit = defineEmits([
    'submit',
    'cancel'
])

const recipeStore = useRecipeStore()

const recipeSearch = ref('')
const activeRecipes = ref([])
const searchingRecipes = ref(false)
const recipeSearchError = ref('')
const showRecipeResults = ref(false)

let recipeSearchTimer = null

const editing = computed(() => Boolean(props.item))

const selectedRecipe = computed(() => {
    if (!form.recipeId) {
        return null
    }

    return activeRecipes.value.find(
        recipe => recipe.id === form.recipeId
    ) ?? recipeStore.recipes.find(
        recipe => recipe.id === form.recipeId
    ) ?? null
})

const form = reactive({
    recipeId: null,
    offeringType: '',
    includesHouseSalad: false,
    includesHomemadeBread: false,
    prices: [
        {
            label: '',
            amount: null,
            displayOrder: 0
        }
    ]
})

onMounted(async () => {
    await Promise.all([
        recipeStore.fetchRecipes(),
        loadActiveRecipes()
    ])
})

watch(
    () => props.item,
    (item) => {
        populateForm(item)
    },
    {
        immediate: true
    }
)

onBeforeUnmount(() => {
    clearTimeout(recipeSearchTimer)
})

function populateForm(item) {
    if (!item) {
        resetForm()
        return
    }

    form.recipeId = item.recipeId
    form.offeringType = item.offeringType

    form.includesHouseSalad =
        item.includesHouseSalad ?? false

    form.includesHomemadeBread =
        item.includesHomemadeBread ?? false

    form.prices = item.prices.map(
        (price, index) => ({
            label: price.label ?? '',
            amount: price.amount,
            displayOrder: index
        })
    )
}

function resetForm() {
    form.recipeId = null
    form.offeringType = ''
    form.includesHouseSalad = false
    form.includesHomemadeBread = false

    form.prices = [
        {
            label: '',
            amount: null,
            displayOrder: 0
        }
    ]
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

    form.recipeId = null
    showRecipeResults.value = true

    recipeSearchTimer = setTimeout(() => {
        loadActiveRecipes(recipeSearch.value)
    }, 300)
}

async function selectRecipe(recipe) {
    form.recipeId = recipe.id
    recipeSearch.value = recipe.name
    recipeSearchError.value = ''
    showRecipeResults.value = false

    await handleRecipeSelected()
}

async function clearSelectedRecipe() {
    form.recipeId = null
    recipeSearch.value = ''
    showRecipeResults.value = true

    resetWeeklyDetails()

    await loadActiveRecipes()
}

function resetWeeklyDetails() {
    form.offeringType = ''
    form.includesHouseSalad = false
    form.includesHomemadeBread = false

    form.prices = [
        {
            label: '',
            amount: null,
            displayOrder: 0
        }
    ]
}

async function handleRecipeSelected() {
    if (!form.recipeId) {
        return
    }

    const previousItem =
        await recipeStore.fetchLatestOfferingItem(
            form.recipeId
        )

    if (!previousItem) {
        resetWeeklyDetails()
        return
    }

    form.offeringType =
        previousItem.offeringType ?? ''

    form.includesHouseSalad =
        previousItem.includesHouseSalad ?? false

    form.includesHomemadeBread =
        previousItem.includesHomemadeBread ?? false

    form.prices =
        previousItem.prices?.map(
            (price, index) => ({
                label: price.label ?? '',
                amount: price.amount,
                displayOrder: index
            })
        ) ?? []
}

function addPrice() {
    form.prices.push({
        label: '',
        amount: null,
        displayOrder: form.prices.length
    })
}

function removePrice(index) {
    form.prices.splice(index, 1)

    form.prices.forEach((price, priceIndex) => {
        price.displayOrder = priceIndex
    })
}

function submitForm() {
    if (!form.recipeId) {
        recipeSearchError.value =
            'Please select a recipe from the list.'

        showRecipeResults.value = true
        return
    }

    const isDinner =
        form.offeringType === 'DINNER'

    emit('submit', {
        recipeId: form.recipeId,
        offeringType: form.offeringType,

        includesHouseSalad:
            isDinner
                ? form.includesHouseSalad
                : false,

        includesHomemadeBread:
            isDinner
                ? form.includesHomemadeBread
                : false,

        prices: form.prices.map(
            (price, index) => ({
                label:
                    price.label?.trim()
                        ? price.label.trim()
                        : null,

                amount: Number(price.amount),

                displayOrder: index
            })
        )
    })
}

</script>

<style scoped>
form {
    display: grid;
    gap: 1.25rem;

    width: 100%;
}

form h3 {
    margin: 0 0 0.25rem;

    color: var(--default-color);

    font-size: 1.35rem;
}

/* Each major form section */
form>div {
    display: grid;
    gap: 0.45rem;
}

label,
legend {
    color: var(--default-dark);

    font-size: 0.85rem;
    font-weight: 700;
    letter-spacing: 0.04em;
}

/* ==========================================================
   INPUTS
   ========================================================== */

input,
select,
textarea {
    width: 100%;
    padding: 0.7rem 0.8rem;

    color: var(--default-color);
    background: var(--background-dark-trans);

    border: 1px solid var(--bronze-color);
    border-radius: 0.4rem;

    font: inherit;
}

textarea {
    min-height: 110px;
    resize: vertical;
}

input:focus,
select:focus,
textarea:focus {
    outline: 2px solid var(--bronze-bold);
    outline-offset: 2px;
}

select option {
    color: var(--default-color);
    background: #140f0c;
}

.recipe-picker {
    position: relative;

    display: grid;
    gap: 0.5rem;
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
    margin-top: 0.25rem;
}

/* ==========================================================
   SELECTED RECIPE PREVIEW
   ========================================================== */

.selected-recipe {
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

/* ==========================================================
   PRICES
   ========================================================== */

form h4 {
    margin: 0;

    color: var(--bronze-bold);

    font-size: 0.85rem;
    letter-spacing: 0.1em;
    text-transform: uppercase;
}

/* Individual price rows */
.price-row {
    display: grid;
    grid-template-columns: minmax(0, 1fr) 130px auto;
    gap: 0.75rem;
    align-items: end;

    padding: 0.75rem;

    background: var(--background-dark-trans);

    border: 1px solid var(--bronze-color);
    border-radius: 0.4rem;
}

.price-field {
    display: grid;
    gap: 0.4rem;
}

.price-field input {
    width: 100%;
}

/* ==========================================================
   DINNER INCLUDES
   ========================================================== */

fieldset {
    display: flex;
    flex-wrap: wrap;
    gap: 1rem;

    margin: 0;
    padding: 1rem;

    border: 1px solid var(--bronze-color);
    border-radius: 0.4rem;
}

fieldset label {
    display: flex;
    gap: 0.5rem;
    align-items: center;

    color: var(--default-color);

    cursor: pointer;
}

fieldset input[type="checkbox"] {
    width: auto;
    margin: 0;

    accent-color: var(--bronze-bold);
}


/* ==========================================================
   BUTTONS
   ========================================================== */

button {
    width: fit-content;
    padding: 0.65rem 1rem;

    color: #140f0c;
    background: var(--default-color);

    border: 1px solid var(--bronze-color);
    border-radius: 0.35rem;

    font: inherit;
    font-weight: 700;

    cursor: pointer;
}

button:hover:not(:disabled) {
    background: var(--bronze-hover);
}

button:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

/* Save button */
button[type="submit"] {
    margin-top: 0.25rem;

    background: var(--bronze-bold);
    color: var(--default-color);
}


/* ==========================================================
   STATUS MESSAGES
   ========================================================== */

form p {
    margin: 0.25rem 0;

    color: var(--default-dark);
}


/* ==========================================================
   MOBILE
   ========================================================== */

@media (max-width: 600px) {
    form>div>div {
        grid-template-columns: 1fr;
    }

    button {
        width: 100%;
    }
}
</style>