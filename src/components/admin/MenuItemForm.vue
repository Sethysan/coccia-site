<template>
    <form class="admin-form menu-item-form" @submit.prevent="submitForm">
        <h3>
            {{ editing ? 'Edit Menu Item' : 'Add Menu Item' }}
        </h3>

        <div class="recipe-selection">
            <RecipePicker v-model="form.recipeId" @create-requested="beginCreateRecipe" />

            <div v-if="showCreateRecipe" class="inline-recipe-form">
                <h4>Create New Recipe</h4>

                <label>
                    Recipe name

                    <input v-model="newRecipeName" type="text" maxlength="150"
                        placeholder="Example: Homemade Provolone Sticks" required>
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
                        placeholder="Example: Pictured with cannellini beans.">
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

        <label v-if="subsections.length">
            Subsection

            <select v-model="form.menuSubsectionId">
                <option :value="null">
                    No subsection
                </option>

                <option v-for="subsection in subsections" :key="subsection.id" :value="subsection.id">
                    {{ subsection.name }}
                </option>
            </select>
        </label>

        <div v-if="usesSharedSubsectionPrice" class="shared-subsection-price">
            <strong>
                {{ selectedSubsection.name }} ·
                ${{ Number(selectedSubsection.price).toFixed(2) }}
            </strong>

            <span>
                Items in this subsection use its shared price.
            </span>
        </div>

        <div v-if="!usesSharedSubsectionPrice">
            <h4>Prices</h4>

            <div v-for="(price, index) in form.prices" :key="index" class="price-row">
                <label>
                    Label

                    <input v-model="price.label" type="text" maxlength="100" placeholder="Optional, e.g. Regular">
                </label>

                <label>
                    Amount

                    <input v-model="price.amount" type="number" min="0.01" step="0.01" required>
                </label>

                <button v-if="form.prices.length > 1" type="button" @click="removePrice(index)">
                    Remove
                </button>
            </div>

            <button type="button" @click="addPrice">
                + Add Size / Price
            </button>
        </div>

        <label class="visible-control">
            <input v-model="form.visible" type="checkbox">

            Visible on menu
        </label>

        <div class="admin-form-actions">
            <button type="submit" class="primary-button" :disabled="saving">
                {{
                    saving
                        ? 'Saving...'
                        : editing
                            ? 'Save Changes'
                            : addButtonLabel
                }}
            </button>

            <button type="button" :disabled="saving" @click="$emit('cancel')">
                Cancel
            </button>
        </div>
    </form>
</template>

<script setup>
import {
    computed,
    ref,
    reactive,
    watch
} from 'vue'

import RecipePicker from '@/components/admin/RecipePicker.vue'
import { useRecipeStore } from '@/stores/recipeStore'

const props = defineProps({

    sectionName: {
        type: String,
        required: true
    },

    item: {
        type: Object,
        default: null
    },

    saving: {
        type: Boolean,
        default: false
    },

    defaultDisplayOrder: {
        type: Number,
        default: 0
    },

    subsections: {
        type: Array,
        default: () => []
    }
})

const emit = defineEmits([
    'submit',
    'cancel'
])

const recipeStore = useRecipeStore()

const showCreateRecipe = ref(false)
const newRecipeName = ref('')
const newRecipeDescription = ref('')
const newRecipeImageAlt = ref('')
const newRecipeImageCaption = ref('')
const newRecipePhoto = ref(null)
const creatingRecipe = ref(false)

const editing = computed(() => Boolean(props.item))

const form = reactive({
    recipeId: null,
    menuSubsectionId: null,
    displayOrder: 0,
    visible: true,
    prices: [
        {
            label: '',
            amount: null,
            displayOrder: 0
        }
    ]
})

const selectedSubsection = computed(() => {
    if (form.menuSubsectionId == null) {
        return null
    }

    return props.subsections.find(
        subsection =>
            Number(subsection.id) ===
            Number(form.menuSubsectionId)
    ) ?? null
})

const usesSharedSubsectionPrice = computed(() =>
    selectedSubsection.value?.price != null
)

const addButtonLabel = computed(() => {
    if (selectedSubsection.value) {
        return `Add ${selectedSubsection.value.name}`
    }

    return `Add to ${props.sectionName}`
})

watch(
    () => props.item,
    item => {
        populateForm(item)
    },
    {
        immediate: true
    }
)

watch(
    () => props.defaultDisplayOrder,
    value => {
        if (!props.item) {
            form.displayOrder = value
        }
    }
)

function populateForm(item) {
    if (!item) {
        resetForm()
        return
    }

    form.recipeId = item.recipeId
    form.menuSubsectionId = item.menuSubsectionId ?? null
    form.displayOrder = item.displayOrder
    form.visible = item.visible

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
    form.menuSubsectionId = null
    form.displayOrder = props.defaultDisplayOrder
    form.visible = true

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
            cancelCreateRecipe()
        }

    } finally {
        creatingRecipe.value = false
    }
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

    emit('submit', {
        recipeId: form.recipeId,
        menuSubsectionId: form.menuSubsectionId,
        displayOrder: form.displayOrder,
        visible: form.visible,

        prices: usesSharedSubsectionPrice.value
            ? []
            : form.prices.map(
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
.menu-item-form {
    grid-template-columns: 1fr;
}

.menu-item-form h3,
.menu-item-form h4 {
    margin: 0;
}

.menu-item-form>div {
    display: grid;
    gap: 0.75rem;
}

.price-row {
    display: grid;
    grid-template-columns: minmax(0, 1fr) 130px auto;
    gap: 0.75rem;
    align-items: end;

    padding: 0.75rem;

    border: 1px solid rgba(255, 255, 255, 0.12);
    border-radius: 0.4rem;
}

.price-row label {
    display: grid;
    gap: 0.4rem;
}

.visible-control {
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

.visible-control input {
    width: auto;
}

.recipe-selection {
    display: grid;
    gap: 0.75rem;
}

.create-recipe-toggle {
    width: fit-content;
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

.price-row {
    display: grid;
    grid-template-columns: minmax(0, 1fr) minmax(8rem, 11rem);
    gap: 0.75rem;
    align-items: end;
}

.price-row label {
    min-width: 0;
}

.price-row input {
    width: 100%;
    min-width: 0;
    box-sizing: border-box;
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


@media (max-width: 600px) {
    .price-row {
        grid-template-columns: 1fr;
    }

    .recipe-photo-picker {
        align-items: flex-start;
        flex-direction: column;
    }

    .recipe-photo-filename {
        max-width: 100%;
    }
}
</style>