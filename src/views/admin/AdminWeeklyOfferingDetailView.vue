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
            <button v-if="offering.status === 'DRAFT'" type="button" @click="deleteOffering">
                Delete Offering
            </button>

            <button v-if="
                offering.status === 'SCHEDULED'
                || offering.status === 'PUBLISHED'
            " type="button" @click="archiveOffering">
                Archive Offering
            </button>

            <button v-if="offering.status !== 'ARCHIVED' && !showDateForm" type="button" @click="showDateForm = true">
                Edit Dates
            </button>
            <WeeklyOfferingForm v-if="showDateForm" :saving="savingDates" submit-label="Save Dates"
                :initial-start-date="offering.startDate" :initial-end-date="offering.endDate"
                @submit="updateOfferingDates" @cancel="showDateForm = false" />

            <button v-if="offering.status === 'DRAFT'" type="button" @click="scheduleOffering">
                Schedule Offering
            </button>

            <h2>Items</h2>

            <button v-if="offering.status !== 'ARCHIVED' && !showAddItemForm" @click="startAddingItem">
                Add Item
            </button>

            <WeeklyOfferingItemForm v-if="showAddItemForm" ref="itemForm" :item="editingItem" :saving="savingItem"
                @submit="saveItem" @cancel="cancelItemForm" />

            <p v-if="offering.items.length === 0">
                No items have been added yet.
            </p>

            <article v-for="item in offering.items" :key="item.id">
                <h3>
                    Featured {{ formatOfferingType(item.offeringType) }}
                </h3>

                <h4>{{ item.recipeName }}</h4>

                <p v-if="item.publicDescription">
                    {{ item.publicDescription }}
                </p>

                <h3>{{ item.recipeName }}</h3>

                <img v-if="item.imageUrl" :src="item.imageUrl" :alt="item.imageAlt || item.recipeName"
                    class="weekly-offering-item-image" />

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

                <button v-if="offering.status !== 'ARCHIVED'" type="button" @click="startEditingItem(item)">
                    Edit
                </button>

                <button v-if="offering.status !== 'ARCHIVED'" type="button" @click="deleteItem(item.id)">
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
import WeeklyOfferingItemForm
    from '@/components/admin/WeeklyOfferingItemForm.vue'
import WeeklyOfferingForm from '@/components/admin/WeeklyOfferingForm.vue'

const route = useRoute()
const router = useRouter()
const weeklyOfferings = useWeeklyOfferingStore()
const recipeStore = useRecipeStore()
const showAddItemForm = ref(false)
const savingItem = ref(false)
const itemForm = ref(null)
const editingItem = ref(null)
const showDateForm = ref(false)
const savingDates = ref(false)

const offering = computed(
    () => weeklyOfferings.currentOffering
)

onMounted(async () => {
    await Promise.all([
        weeklyOfferings.fetchOfferingById(route.params.id),
        recipeStore.fetchRecipes()
    ])
})

function startAddingItem() {
    editingItem.value = null
    showAddItemForm.value = true
}

function startEditingItem(item) {
    editingItem.value = item
    showAddItemForm.value = true
}

function cancelItemForm() {
    editingItem.value = null
    showAddItemForm.value = false
}

async function saveItem(payload) {
    savingItem.value = true

    try {
        payload.displayOrder =
            getDisplayOrder(payload.offeringType)

        const saved = editingItem.value
            ? await weeklyOfferings.updateItem(
                route.params.id,
                editingItem.value.id,
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
            itemForm.value?.resetForm()

            return
        }
        editingItem.value = null
        showAddItemForm.value = false

    } finally {
        savingItem.value = false
    }
}

async function updateOfferingDates(formData) {
    savingDates.value = true

    try {
        const updated =
            await weeklyOfferings.updateOfferingDates(
                route.params.id,
                formData.startDate,
                formData.endDate
            )

        if (!updated) {
            window.alert(
                weeklyOfferings.error
                ?? 'Unable to update weekly offering dates.'
            )

            weeklyOfferings.clearError()

            return
        }

        showDateForm.value = false

    } finally {
        savingDates.value = false
    }
}

async function scheduleOffering() {
    const confirmed = window.confirm(
        'Are you sure you want to schedule this weekly offering?'
    )

    if (!confirmed) {
        return
    }

    const scheduled =
        await weeklyOfferings.scheduleOffering(
            route.params.id
        )

    if (!scheduled) {
        window.alert(
            weeklyOfferings.error
            ?? 'Unable to schedule weekly offering.'
        )

        weeklyOfferings.clearError()

        return
    }
}

async function deleteItem(itemId) {
    const confirmed = window.confirm(
        'Are you sure you want to delete this item?'
    )

    if (!confirmed) {
        return
    }

    const deleted = await weeklyOfferings.deleteItem(
        route.params.id,
        itemId
    )

    if (!deleted) {
        window.alert(
            weeklyOfferings.error
            ?? 'Unable to delete weekly offering item.'
        )

        weeklyOfferings.clearError()

        return
    }

    // If the deleted item was currently being edited,
    // close and clear the form.
    if (editingItem.value?.id === itemId) {
        editingItem.value = null
        showAddItemForm.value = false
    }
}

function getDisplayOrder(offeringType) {
    switch (offeringType) {
        case 'DINNER':
            return 0
        case 'SOUP':
            return 1
        case 'DESSERT':
            return 2
        default:
            return 99
    }
}

async function deleteOffering() {
    const confirmed = window.confirm(
        'Are you sure you want to permanently delete this draft weekly offering?'
    )

    if (!confirmed) {
        return
    }

    const deleted =
        await weeklyOfferings.deleteOffering(
            route.params.id
        )

    if (!deleted) {
        window.alert(
            weeklyOfferings.error
            ?? 'Unable to delete weekly offering.'
        )

        weeklyOfferings.clearError()

        return
    }

    await router.push('/admin/weekly-offerings')
}

async function archiveOffering() {
    const confirmed = window.confirm(
        'Are you sure you want to archive this weekly offering?'
    )

    if (!confirmed) {
        return
    }

    const archived =
        await weeklyOfferings.archiveOffering(
            route.params.id
        )

    if (!archived) {
        window.alert(
            weeklyOfferings.error
            ?? 'Unable to archive weekly offering.'
        )

        weeklyOfferings.clearError()

        return
    }
}

function formatOfferingType(type) {
    if (!type) {
        return ''
    }

    return type.charAt(0).toUpperCase()
        + type.slice(1).toLowerCase()
}

</script>

<style scoped></style>