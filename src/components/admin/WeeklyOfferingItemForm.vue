<template>
    <form @submit.prevent="submitForm">
        <h3>
            {{ editing ? 'Edit Weekly Offering Item' : 'Add Weekly Offering Item' }}
        </h3>

        <div class="recipe-selection">
            <RecipePicker v-model="form.recipeId" @selected="handleRecipeSelected" @cleared="handleRecipeCleared"
                @create-requested="beginCreateRecipe" />

            <div v-if="showCreateRecipe" class="inline-recipe-form">
                <h4>Create New Recipe</h4>

                <label>
                    Recipe name

                    <input v-model="newRecipeName" type="text" maxlength="150" placeholder="Example: Chicken Cacciatore"
                        required>
                </label>

                <label>
                    Description

                    <textarea v-model="newRecipeDescription" rows="3"
                        placeholder="Describe the dish for customers."></textarea>
                </label>

                <div class="recipe-photo-field">
                    <span class="recipe-photo-label">Photo</span>

                    <label class="recipe-photo-picker">
                        <input type="file" accept="image/*" @change="handleNewRecipePhotoSelected">

                        <span class="recipe-photo-button">
                            + Add Photo
                        </span>

                        <span class="recipe-photo-filename">
                            {{
                                newRecipePhoto
                                    ? newRecipePhoto.name
                                    : 'No photo selected'
                            }}
                        </span>
                    </label>
                </div>

                <label>
                    Image description

                    <input v-model="newRecipeImageAlt" type="text" maxlength="255"
                        placeholder="Optional description of the photo">
                </label>

                <label>
                    Photo caption

                    <input v-model="newRecipeImageCaption" type="text" maxlength="255"
                        placeholder="Example: Pictured with homemade bread.">
                </label>

                <div class="admin-form-actions">
                    <button type="button" class="primary-button" :disabled="creatingRecipe" @click="createRecipe">
                        {{
                            creatingRecipe
                                ? 'Creating Recipe...'
                                : 'Create & Select Recipe'
                        }}
                    </button>

                    <button type="button" :disabled="creatingRecipe" @click="cancelCreateRecipe">
                        Cancel
                    </button>
                </div>
            </div>
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
    reactive,
    ref,
    watch
} from 'vue'

import { useRecipeStore } from '@/stores/recipeStore'
import RecipePicker from '@/components/admin/RecipePicker.vue'

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

const showCreateRecipe = ref(false)
const creatingRecipe = ref(false)

const newRecipeName = ref('')
const newRecipeDescription = ref('')
const newRecipeImageAlt = ref('')
const newRecipeImageCaption = ref('')
const newRecipePhoto = ref(null)

const editing = computed(() => Boolean(props.item))

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

watch(
    () => props.item,
    (item) => {
        populateForm(item)
    },
    {
        immediate: true
    }
)

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

function beginCreateRecipe(name) {
    newRecipeName.value = name
    newRecipeDescription.value = ''
    newRecipeImageAlt.value = ''
    newRecipeImageCaption.value = ''
    newRecipePhoto.value = null
    showCreateRecipe.value = true

    recipeStore.clearError()
}

function handleNewRecipePhotoSelected(event) {
    newRecipePhoto.value = event.target.files?.[0] ?? null
}

function cancelCreateRecipe() {
    showCreateRecipe.value = false
    newRecipeName.value = ''
    newRecipeDescription.value = ''
    newRecipeImageAlt.value = ''
    newRecipeImageCaption.value = ''
    newRecipePhoto.value = null

    recipeStore.clearError()
}

async function createRecipe() {
    const name = newRecipeName.value.trim()

    if (!name) {
        return
    }

    creatingRecipe.value = true
    recipeStore.clearError()

    try {
        const createdRecipe =
            await recipeStore.addRecipe(
                name,
                newRecipeDescription.value,
                newRecipeImageAlt.value,
                newRecipeImageCaption.value
            )

        if (createdRecipe) {
            if (newRecipePhoto.value) {
                const updatedRecipe =
                    await recipeStore.uploadImage(
                        createdRecipe.id,
                        newRecipePhoto.value
                    )

                if (!updatedRecipe) {
                    return
                }
            }

            form.recipeId = createdRecipe.id

            resetWeeklyDetails()
            cancelCreateRecipe()
        }

    } finally {
        creatingRecipe.value = false
    }
}

function handleRecipeCleared() {
    resetWeeklyDetails()
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
    min-width: 0;
}

.price-field input {
    width: 100%;
    min-width: 0;
    box-sizing: border-box;
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

.inline-recipe-form {
    display: grid;
    gap: 0.75rem;

    padding: 1rem;

    border: 1px solid rgba(255, 255, 255, 0.12);
    border-radius: 0.4rem;
}

.inline-recipe-form h4 {
    margin: 0;
}

.inline-recipe-form label {
    display: grid;
    gap: 0.4rem;
}

.recipe-photo-field {
    display: grid;
    gap: 0.4rem;
}

.recipe-photo-label {
    font-weight: 600;
}

.recipe-photo-picker {
    display: flex;
    align-items: center;
    gap: 0.75rem;

    width: fit-content;
    max-width: 100%;

    cursor: pointer;
}

.recipe-photo-picker input {
    position: absolute;

    width: 1px;
    height: 1px;

    overflow: hidden;

    clip: rect(0, 0, 0, 0);
    white-space: nowrap;
}

.recipe-photo-button {
    flex: 0 0 auto;

    padding: 0.65rem 1rem;

    color: var(--text-primary);
    background: transparent;

    border: 1px solid var(--bronze-color);
    border-radius: 0.35rem;

    font-weight: 700;
}

.recipe-photo-picker:hover .recipe-photo-button {
    background: var(--bronze-bold);
}

.recipe-photo-filename {
    min-width: 0;

    color: var(--text-secondary);

    font-size: 0.9rem;

    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}


/* ==========================================================
   MOBILE
   ========================================================== */

@media (max-width: 600px) {
    .price-row {
        grid-template-columns: 1fr;
    }

    .price-row button {
        width: 100%;
    }

    .recipe-photo-picker {
        align-items: flex-start;
        flex-direction: column;
    }

    .recipe-photo-filename {
        max-width: 100%;
    }

    button {
        width: 100%;
    }
}
</style>