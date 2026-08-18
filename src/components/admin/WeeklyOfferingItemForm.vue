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

            <div v-for="(price, index) in form.prices" :key="index">
                <label>
                    Label
                </label>

                <input v-model="price.label" type="text" placeholder="Optional, e.g. Cup" />

                <label>
                    Amount
                </label>

                <input v-model="price.amount" type="number" min="0.01" step="0.01" required />

                <button v-if="form.prices.length > 1" type="button" @click="removePrice(index)">
                    Remove Price
                </button>
            </div>

            <button type="button" @click="addPrice">
                Add Price
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

<style scoped></style>