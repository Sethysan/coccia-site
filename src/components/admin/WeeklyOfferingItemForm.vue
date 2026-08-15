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
import { computed, onMounted, reactive, watch } from 'vue'
import { useRecipeStore } from '@/stores/recipeStore'

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
    ]
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
    form.publicTitle = item.publicTitle
    form.publicDescription = item.publicDescription ?? ''
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
    form.publicTitle = ''
    form.publicDescription = ''
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
        form.publicTitle = ''
        form.publicDescription = ''
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

    form.publicTitle =
        previousItem.publicTitle ?? ''

    form.publicDescription =
        previousItem.publicDescription ?? ''

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
        publicTitle: form.publicTitle,
        publicDescription:
            form.publicDescription || null,

        imageUrl: null,
        imageAlt: null,

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

<style scoped></style>