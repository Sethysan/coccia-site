<template>
    <main>
        <div v-if="weeklyOfferings.error" role="alert">
            <p>
                {{ weeklyOfferings.error }}
            </p>

            <button type="button" @click="weeklyOfferings.clearError()">
                OK
            </button>
        </div>

        <button @click="router.push('/admin/weekly-offerings')">
            ← Back to Weekly Offerings
        </button>

        <p v-if="weeklyOfferings.loading">
            Loading weekly offering...
        </p>

        <section v-else-if="offering">
            <h1>Weekly Offering</h1>

            <p>
                {{ offering.startDate }} – {{ offering.endDate }}
            </p>

            <p>
                Status: {{ offering.status }}
            </p>

            <h2>Items</h2>

            <button v-if="offering.status === 'DRAFT' && !showAddItemForm" @click="showAddItemForm = true">
                Add Item
            </button>

            <form v-if="showAddItemForm" @submit.prevent="saveItem">
                <h3>
                    {{ editingItemId ? 'Edit Weekly Offering Item' : 'Add Weekly Offering Item' }}
                </h3>

                <div>
                    <label for="recipe">
                        Recipe
                    </label>

                    <select id="recipe" v-model="newItem.recipeId" @change="handleRecipeSelected" required>
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

                    <select id="offering-type" v-model="newItem.offeringType" required>
                        <option disabled value="">
                            Select a type
                        </option>

                        <option value="DINNER">Dinner</option>
                        <option value="SOUP">Soup</option>
                        <option value="DESSERT">Dessert</option>
                    </select>
                </div>

                <div>
                    <label for="public-title">
                        Public Title
                    </label>

                    <input id="public-title" v-model="newItem.publicTitle" type="text" required />
                </div>

                <div>
                    <label for="public-description">
                        Description
                    </label>

                    <textarea id="public-description" v-model="newItem.publicDescription"></textarea>
                </div>

                <div>
                    <h4>Prices</h4>

                    <div v-for="(price, index) in newItem.prices" :key="index">
                        <label>
                            Label
                        </label>

                        <input v-model="price.label" type="text" placeholder="Optional, e.g. Cup" />

                        <label>
                            Amount
                        </label>

                        <input v-model="price.amount" type="number" min="0.01" step="0.01" required />

                        <button type="button" @click="removePrice(index)" v-if="newItem.prices.length > 1">
                            Remove Price
                        </button>
                    </div>

                    <button type="button" @click="addPrice">
                        Add Price
                    </button>
                </div>

                <fieldset v-if="newItem.offeringType === 'DINNER'">
                    <legend>Dinner Includes</legend>

                    <label>
                        <input v-model="newItem.includesHouseSalad" type="checkbox" />
                        House Salad
                    </label>

                    <label>
                        <input v-model="newItem.includesHomemadeBread" type="checkbox" />
                        Homemade Bread
                    </label>
                </fieldset>

                <button type="submit" :disabled="savingItem">
                    {{
                        savingItem
                            ? 'Saving...'
                            : editingItemId
                                ? 'Save Changes'
                                : 'Save Item'
                    }}
                </button>

                <button type="button" @click="cancelAddItem">
                    Cancel
                </button>
            </form>

            <p v-if="offering.items.length === 0">
                No items have been added yet.
            </p>

            <article v-for="item in offering.items" :key="item.id">
                <h3>{{ item.publicTitle }}</h3>

                <p>{{ item.offeringType }}</p>

                <p v-if="item.publicDescription">
                    {{ item.publicDescription }}
                </p>

                <p>
                    Recipe: {{ item.recipeName }}
                </p>

                <p v-if="item.includedSidesText">
                    {{ item.includedSidesText }}
                </p>

                <ul>
                    <li v-for="price in item.prices" :key="price.id">
                        <span v-if="price.label">
                            {{ price.label }}:
                        </span>

                        ${{ price.amount }}
                    </li>
                </ul>

                <button v-if="offering.status === 'DRAFT'" type="button" @click="startEditingItem(item)">
                    Edit
                </button>

                <button v-if="offering.status === 'DRAFT'" type="button" @click="deleteItem(item.id)">
                    Delete
                </button>

            </article>

        </section>
    </main>
</template>

<script setup>

import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWeeklyOfferingStore } from '@/stores/weeklyOfferingStore'
import { useRecipeStore } from '@/stores/recipeStore'


const route = useRoute()
const router = useRouter()
const weeklyOfferings = useWeeklyOfferingStore()
const recipeStore = useRecipeStore()
const showAddItemForm = ref(false)
const savingItem = ref(false)
const editingItemId = ref(null)


const newItem = ref({
    recipeId: null,
    offeringType: '',
    publicTitle: '',
    publicDescription: '',
    includesHouseSalad: false,
    includesHomemadeBread: false,
    prices: []
})

const offering = computed(
    () => weeklyOfferings.currentOffering
)

onMounted(async () => {
    await Promise.all([
        weeklyOfferings.fetchOfferingById(route.params.id),
        recipeStore.fetchRecipes()
    ])
})

async function handleRecipeSelected() {
    if (!newItem.value.recipeId) {
        return
    }

    try {
        const previousItem =
            await recipeStore.fetchLatestOfferingItem(
                newItem.value.recipeId
            )

        if (!previousItem) {
            // This recipe has never been featured before.
            // Keep the selected recipe but clear the offering-specific fields.
            newItem.value.offeringType = ''
            newItem.value.publicTitle = ''
            newItem.value.publicDescription = ''
            newItem.value.includesHouseSalad = false
            newItem.value.includesHomemadeBread = false
            newItem.value.prices = [
                {
                    label: '',
                    amount: null,
                    displayOrder: 0
                }
            ]

            return
        }

        newItem.value.offeringType =
            previousItem.offeringType ?? ''

        newItem.value.publicTitle =
            previousItem.publicTitle ?? ''

        newItem.value.publicDescription =
            previousItem.publicDescription ?? ''

        newItem.value.includesHouseSalad =
            previousItem.includesHouseSalad ?? false

        newItem.value.includesHomemadeBread =
            previousItem.includesHomemadeBread ?? false

        newItem.value.prices =
            previousItem.prices?.map(price => ({
                label: price.label ?? '',
                amount: price.amount,
                displayOrder: price.displayOrder
            })) ?? []

    } catch (error) {
        console.error(
            'Unable to populate previous recipe details.',
            error
        )
    }
}

function addPrice() {
    newItem.value.prices.push({
        label: '',
        amount: null,
        displayOrder: newItem.value.prices.length
    })
}

function removePrice(index) {
    newItem.value.prices.splice(index, 1)

    newItem.value.prices.forEach((price, priceIndex) => {
        price.displayOrder = priceIndex
    })
}

function resetNewItem() {
    newItem.value = {
        recipeId: null,
        offeringType: '',
        publicTitle: '',
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
    }
}

function cancelAddItem() {
    editingItemId.value = null
    resetNewItem()
    showAddItemForm.value = false
}

async function saveItem() {
    savingItem.value = true

    try {
        const isDinner =
            newItem.value.offeringType === 'DINNER'

        const payload = {
            recipeId: newItem.value.recipeId,
            offeringType: newItem.value.offeringType,
            publicTitle: newItem.value.publicTitle,
            publicDescription:
                newItem.value.publicDescription || null,

            imageUrl: null,
            imageAlt: null,

            includesHouseSalad:
                isDinner
                    ? newItem.value.includesHouseSalad
                    : false,

            includesHomemadeBread:
                isDinner
                    ? newItem.value.includesHomemadeBread
                    : false,

            displayOrder:
                weeklyOfferings.currentOffering.items.length,

            prices: newItem.value.prices.map(
                (price, index) => ({
                    label:
                        price.label?.trim()
                            ? price.label.trim()
                            : null,

                    amount: Number(price.amount),

                    displayOrder: index
                })
            )
        }

        const saved = editingItemId.value
            ? await weeklyOfferings.updateItem(
                route.params.id,
                editingItemId.value,
                payload
            )
            : await weeklyOfferings.addItem(
                route.params.id,
                payload
            )

        if (!saved) {
            window.alert(
                weeklyOfferings.error
                ?? 'Unable to save weekly offering item.'
            )

            weeklyOfferings.clearError()
            resetNewItem()

            return
        }
        editingItemId.value = null
        resetNewItem()
        showAddItemForm.value = false

    } finally {
        savingItem.value = false
    }
}

async function deleteItem(itemId) {
    const confirmed = window.confirm(
        'Are you sure you want to delete this item?'
    )

    if (!confirmed) {
        return
    }

    await weeklyOfferings.deleteItem(
        route.params.id,
        itemId
    )
}

function startEditingItem(item) {
    editingItemId.value = item.id

    newItem.value = {
        recipeId: item.recipeId,
        offeringType: item.offeringType,
        publicTitle: item.publicTitle,
        publicDescription: item.publicDescription ?? '',
        includesHouseSalad: item.includesHouseSalad ?? false,
        includesHomemadeBread: item.includesHomemadeBread ?? false,
        prices: item.prices.map((price, index) => ({
            label: price.label ?? '',
            amount: price.amount,
            displayOrder: index
        }))
    }

    showAddItemForm.value = true
}

</script>

<style scoped></style>