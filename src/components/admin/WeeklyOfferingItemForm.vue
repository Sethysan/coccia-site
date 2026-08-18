<template>
    <form @submit.prevent="submitForm">
        <h3>
            {{ editing ? 'Edit Weekly Offering Item' : 'Add Weekly Offering Item' }}
        </h3>

        <div>
            <label for="recipe">
                Recipe
            </label>

            <select id="recipe" v-model="form.recipeId" @change="handleRecipeSelected" required>
                <option disabled :value="null">
                    Select a recipe
                </option>

                <option v-for="recipe in recipeStore.recipes" :key="recipe.id" :value="recipe.id">
                    {{ recipe.name }}
                </option>
            </select>

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
            <label for="public-description">
                Description
            </label>

            <textarea id="public-description" v-model="form.publicDescription"></textarea>
        </div>

        <div>
            <label for="image">
                Replace Image
            </label>

            <input id="image" type="file" accept="image/*" @change="handleImageSelected" />

            <p v-if="uploadingImage">
                Uploading image...
            </p>

            <div v-if="form.imageUrl">
                <p>Current image:</p>

                <img :src="form.imageUrl" :alt="form.imageAlt || 'Weekly offering image'"
                    class="weekly-offering-image-preview" />
            </div>

            <label for="image-alt">
                Image description
            </label>

            <input id="image-alt" v-model="form.imageAlt" type="text" />
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
import { computed, onMounted, reactive, watch, ref } from 'vue'
import { useRecipeStore } from '@/stores/recipeStore'
import { getCsrfToken } from '@/utils/csrf'

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

const editing = computed(() => Boolean(props.item))

const uploadingImage = ref(false)

const form = reactive({
    recipeId: null,
    offeringType: '',
    publicDescription: '',
    includesHouseSalad: false,
    includesHomemadeBread: false,
    prices: [
        {
            label: '',
            amount: null,
            displayOrder: 0
        }
    ],
    imageUrl: null,
    imageAlt: ''
})

onMounted(() => {
    recipeStore.fetchRecipes()
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
    form.publicDescription = item.publicDescription ?? ''
    form.imageUrl = item.imageUrl ?? null
    form.imageAlt = item.imageAlt ?? ''
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
    form.publicDescription = ''
    form.imageUrl = null
    form.imageAlt = ''
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
defineExpose({
    resetForm
})

async function handleRecipeSelected() {
    if (!form.recipeId) {
        return
    }

    const previousItem =
        await recipeStore.fetchLatestOfferingItem(
            form.recipeId
        )

    if (!previousItem) {
        form.offeringType = ''
        form.publicDescription = ''
        form.imageUrl = null
        form.imageAlt = ''
        form.includesHouseSalad = false
        form.includesHomemadeBread = false
        form.prices = [
            {
                label: '',
                amount: null,
                displayOrder: 0
            }
        ]

        return
    }

    form.offeringType =
        previousItem.offeringType ?? ''

    form.publicDescription =
        previousItem.publicDescription ?? ''

    form.imageUrl =
        previousItem.imageUrl ?? null

    form.imageAlt =
        previousItem.imageAlt ?? ''

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
    const isDinner =
        form.offeringType === 'DINNER'

    emit('submit', {
        recipeId: form.recipeId,
        offeringType: form.offeringType,
        publicDescription:
            form.publicDescription || null,

        imageUrl: form.imageUrl || null,
        imageAlt: form.imageAlt?.trim() || null,

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

async function handleImageSelected(event) {
    const file = event.target.files?.[0]

    if (!file) {
        return
    }

    uploadingImage.value = true

    try {
        const formData = new FormData()

        formData.append('file', file)

        const csrfToken = getCsrfToken()

        const response = await fetch(
            '/api/admin/weekly-offerings/images',
            {
                method: 'POST',
                headers: {
                    'X-XSRF-TOKEN': csrfToken
                },
                credentials: 'include',
                body: formData
            }
        )

        if (!response.ok) {
            const errorResponse =
                await response.json().catch(() => null)

            throw new Error(
                errorResponse?.message
                ?? 'Unable to upload image.'
            )
        }

        const result = await response.json()

        form.imageUrl = result.imageUrl

    } catch (error) {
        window.alert(
            error.message
            ?? 'Unable to upload image.'
        )
    } finally {
        uploadingImage.value = false
    }
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


/* ==========================================================
   IMAGE
   ========================================================== */

.weekly-offering-image-preview {
    display: block;

    width: 100%;
    max-width: 320px;
    max-height: 240px;

    margin: 0.5rem 0;

    object-fit: contain;

    border: 1px solid var(--bronze-color);
    border-radius: 0.5rem;
}

input[type="file"] {
    padding: 0.5rem;
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

    .weekly-offering-image-preview {
        max-width: 100%;
    }

    button {
        width: 100%;
    }
}
</style>