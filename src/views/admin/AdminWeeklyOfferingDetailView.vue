<template>
    <main class="admin-page">

        <!-- Error Message -->
        <div v-if="weeklyOfferings.error" class="admin-alert" role="alert">
            <p>
                {{ weeklyOfferings.error }}
            </p>

            <button type="button" @click="weeklyOfferings.clearError()">
                OK
            </button>
        </div>

        <div class="admin-page-container admin-page-container-wide">

            <button class="back-button" type="button" @click="router.push('/admin/weekly-offerings')">
                ← Back to Weekly Offerings
            </button>

            <p v-if="weeklyOfferings.loading">
                Loading weekly offering...
            </p>

            <div v-else-if="offering" class="admin-offering-layout">

                <!-- =========================================
                     LEFT: ADMIN EDITOR
                ========================================== -->

                <section class="admin-editor-panel">

                    <header class="offering-header">

                        <div>
                            <p class="admin-eyebrow">
                                Weekly Offering
                            </p>

                            <h1>
                                Manage Weekly Offering
                            </h1>

                            <p class="offering-dates">
                                {{ formatDateRange(
                                    offering.startDate,
                                offering.endDate
                                ) }}
                            </p>
                        </div>

                        <span class="status-badge" :class="`status-${offering.status.toLowerCase()}`">
                            {{ offering.status }}
                        </span>

                    </header>


                    <!-- Offering Actions -->

                    <div class="offering-actions">

                        <button v-if="offering.status === 'DRAFT'" type="button" class="primary-button"
                            @click="scheduleOffering">
                            Schedule Offering
                        </button>

                        <button v-if="
                            offering.status !== 'ARCHIVED'
                            && !showDateForm
                        " type="button" @click="showDateForm = true">
                            Edit Dates
                        </button>

                        <button v-if="
                            offering.status === 'SCHEDULED'
                            || offering.status === 'PUBLISHED'
                        " type="button" @click="archiveOffering">
                            Archive Offering
                        </button>

                        <button v-if="offering.status === 'DRAFT'" type="button" class="danger-button"
                            @click="deleteOffering">
                            Delete Offering
                        </button>

                    </div>


                    <!-- Date Form -->

                    <div v-if="showDateForm" class="admin-form-container">
                        <WeeklyOfferingForm :saving="savingDates" submit-label="Save Dates"
                            :initial-start-date="offering.startDate" :initial-end-date="offering.endDate"
                            @submit="updateOfferingDates" @cancel="showDateForm = false" />
                    </div>


                    <!-- =====================================
                         ITEMS
                    ====================================== -->

                    <section class="items-section">

                        <div class="section-heading">

                            <div>
                                <p class="admin-eyebrow">
                                    Menu Items
                                </p>

                                <h2>
                                    Weekly Features
                                </h2>
                            </div>

                            <button v-if="
                                offering.status !== 'ARCHIVED'
                                && !showAddItemForm
                            " type="button" class="primary-button" @click="startAddingItem">
                                + Add Item
                            </button>

                        </div>


                        <!-- Add / Edit Item Form -->

                        <div v-if="showAddItemForm" ref="itemFormSection" class="item-form-section">
                            <div class="form-heading">

                                <p class="admin-eyebrow">
                                    {{
                                        editingItem
                                            ? 'Editing Item'
                                            : 'New Item'
                                    }}
                                </p>

                                <h2>
                                    {{
                                        editingItem
                                            ? editingItem.recipeName
                                            : 'Add Weekly Feature'
                                    }}
                                </h2>

                            </div>

                            <WeeklyOfferingItemForm ref="itemForm" :item="editingItem" :saving="savingItem"
                                @submit="saveItem" @cancel="cancelItemForm" />
                        </div>


                        <p v-if="offering.items.length === 0" class="empty-state">
                            No items have been added yet.
                        </p>


                        <!-- Item Cards -->

                        <div class="admin-item-list">

                            <article v-for="item in sortedItems" :key="item.id" class="admin-item-card">

                                <img v-if="item.imageUrl" :src="item.imageUrl" :alt="item.imageAlt
                                    || item.recipeName
                                    " class="admin-item-image" />

                                <div v-else class="admin-item-image-placeholder">
                                    No Image
                                </div>


                                <div class="admin-item-content">

                                    <p class="admin-item-type">
                                        Featured
                                        {{
                                            formatOfferingType(
                                                item.offeringType
                                            )
                                        }}
                                    </p>

                                    <h3>
                                        {{ item.recipeName }}
                                    </h3>

                                    <p v-if="item.publicDescription" class="admin-item-description">
                                        {{ item.publicDescription }}
                                    </p>

                                    <p v-if="item.includedSidesText" class="admin-item-sides">
                                        {{ item.includedSidesText }}
                                    </p>


                                    <ul class="admin-price-list">

                                        <li v-for="price in item.prices" :key="price.id">
                                            <span v-if="price.label">
                                                {{ price.label }}:
                                            </span>

                                            ${{ price.amount }}
                                        </li>

                                    </ul>


                                    <div v-if="
                                        offering.status
                                        !== 'ARCHIVED'
                                    " class="admin-item-actions">

                                        <button type="button" @click="
                                            startEditingItem(
                                                item
                                            )
                                            ">
                                            Edit
                                        </button>

                                        <button type="button" class="danger-button" @click="
                                            deleteItem(
                                                item.id
                                            )
                                            ">
                                            Delete
                                        </button>

                                    </div>

                                </div>

                            </article>

                        </div>

                    </section>

                </section>


                <!-- =========================================
                     RIGHT: PUBLIC PREVIEW
                ========================================== -->

                <aside class="preview-panel">

                    <header class="preview-heading">

                        <p class="admin-eyebrow">
                            Customer View
                        </p>

                        <h2>
                            Public Preview
                        </h2>

                        <p>
                            This is how the weekly features
                            will appear to customers.
                        </p>

                    </header>

                    <div class="preview-content">

                        <WeeklyOffering :offering="offering" />

                    </div>

                </aside>

            </div>

        </div>

    </main>
</template>

<script setup>

import { computed, nextTick, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWeeklyOfferingStore } from '@/stores/weeklyOfferingStore'
import { useRecipeStore } from '@/stores/recipeStore'
import WeeklyOfferingItemForm
    from '@/components/admin/WeeklyOfferingItemForm.vue'
import WeeklyOfferingForm from '@/components/admin/WeeklyOfferingForm.vue'
import WeeklyOffering from '@/components/WeeklyOffering.vue'
import { formatDateRange } from '@/utils/dateFormat'

const route = useRoute()
const router = useRouter()
const weeklyOfferings = useWeeklyOfferingStore()
const recipeStore = useRecipeStore()
const showAddItemForm = ref(false)
const savingItem = ref(false)
const itemForm = ref(null)
const itemFormSection = ref(null)
const editingItem = ref(null)
const showDateForm = ref(false)
const savingDates = ref(false)

const offering = computed(
    () => weeklyOfferings.currentOffering
)

const sortedItems = computed(() => {
    return [...(offering.value?.items ?? [])]
        .sort((a, b) => a.displayOrder - b.displayOrder)
})

onMounted(async () => {
    await Promise.all([
        weeklyOfferings.fetchOfferingById(route.params.id),
        recipeStore.fetchRecipes()
    ])
})

async function scrollToItemForm() {
    await nextTick()

    itemFormSection.value?.scrollIntoView({
        behavior: 'smooth',
        block: 'start'
    })
}

async function startAddingItem() {
    editingItem.value = null
    showAddItemForm.value = true

    await scrollToItemForm()
}

async function startEditingItem(item) {
    editingItem.value = item
    showAddItemForm.value = true

    await scrollToItemForm()
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

<style scoped>
/* ==========================================================
   ADMIN PAGE
   ========================================================== */

.back-button {
    margin-bottom: 1rem;
}


/* ==========================================================
   MAIN LAYOUT
   ========================================================== */

.admin-offering-layout {
    display: grid;
    grid-template-columns:
        minmax(500px, 1fr) minmax(400px, 0.9fr);

    gap: 2rem;
    align-items: start;
}

.admin-editor-panel,
.preview-panel {
    padding: 1.5rem;

    background: var(--background-dark-trans);

    border: 1px solid var(--bronze-color);
    border-radius: 0.6rem;
}


/* ==========================================================
   HEADER
   ========================================================== */

.offering-header {
    display: flex;
    justify-content: space-between;
    gap: 1rem;
    align-items: flex-start;

    padding-bottom: 1.5rem;

    border-bottom: 1px solid rgba(255, 255, 255, 0.12);
}

.offering-header h1 {
    margin: 0.2rem 0;
}


.offering-dates {
    margin: 0.5rem 0 0;
    opacity: 0.8;
}

/* ==========================================================
   ACTIONS
   ========================================================== */

.offering-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 0.6rem;

    padding: 1.25rem 0;
}

.danger-button {
    opacity: 0.85;
}


/* ==========================================================
   ITEMS SECTION
   ========================================================== */

.items-section {
    margin-top: 1rem;
}

.section-heading {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 1rem;

    margin-bottom: 1rem;
}

.section-heading h2 {
    margin: 0;
}


/* ==========================================================
   ITEM FORM
   ========================================================== */

.item-form-section,
.admin-form-container {
    margin: 1rem 0 1.5rem;
    padding: 1.25rem;

    background: rgba(255, 255, 255, 0.05);

    border: 1px solid var(--bronze-color);
    border-radius: 0.5rem;

    scroll-margin-top: 2rem;
}

.form-heading {
    margin-bottom: 1rem;
}

.form-heading h2 {
    margin: 0;
}


/* ==========================================================
   ITEM CARDS
   ========================================================== */

.admin-item-list {
    display: grid;
    gap: 1rem;
}

.admin-item-card {
    display: grid;
    grid-template-columns: 150px minmax(0, 1fr);
    gap: 1rem;

    padding: 1rem;

    background: var(--background-dark-trans);

    border: 1px solid rgba(255, 255, 255, 0.12);

    border-radius: 0.5rem;
}

.admin-item-image,
.admin-item-image-placeholder {
    width: 150px;
    height: 125px;

    border-radius: 0.4rem;
}

.admin-item-image {
    display: block;

    object-fit: cover;
}

.admin-item-image-placeholder {
    display: flex;
    align-items: center;
    justify-content: center;

    background: rgba(255, 255, 255, 0.06);

    font-size: 0.8rem;
    opacity: 0.6;
}

.admin-item-content {
    min-width: 0;
}

.admin-item-type {
    margin: 0 0 0.3rem;

    color: var(--bronze-bold);

    font-size: 0.7rem;
    font-weight: 700;
    letter-spacing: 0.1em;
    text-transform: uppercase;
}

.admin-item-content h3 {
    margin: 0 0 0.6rem;
}

.admin-item-description,
.admin-item-sides {
    margin: 0.4rem 0;

    line-height: 1.5;
}

.admin-price-list {
    margin: 0.75rem 0 0;
    padding: 0;

    list-style: none;
}

.admin-price-list li {
    margin: 0.2rem 0;

    font-weight: 700;
}

.admin-item-actions {
    display: flex;
    gap: 0.5rem;

    margin-top: 1rem;
}


/* ==========================================================
   PUBLIC PREVIEW
   ========================================================== */

.preview-panel {
    position: sticky;
    top: 1rem;
}

.preview-heading {
    padding-bottom: 1rem;

    border-bottom: 1px solid rgba(255, 255, 255, 0.12);
}

.preview-heading h2 {
    margin: 0.2rem 0;
}

.preview-heading>p:last-child {
    margin-bottom: 0;

    font-size: 0.85rem;
    opacity: 0.7;
}

.preview-content {
    margin-top: 1rem;
    color: var(--text-primary);
    font-family: inherit;
}


/*
   Override the public component's outer sizing ONLY
   while it is being shown inside the admin preview.
*/

.preview-content :deep(.weekly-offering) {
    width: 100%;
    max-width: none;
    margin: 0;
    color: var(--text-primary);
}


/* ==========================================================
   OTHER
   ========================================================== */

.empty-state {
    padding: 2rem;

    text-align: center;
    opacity: 0.7;

    border: 1px dashed rgba(255, 255, 255, 0.2);
    border-radius: 0.5rem;
}

.admin-alert {
    width: min(700px, 90%);
    margin: 0 auto 1rem;
    padding: 1rem;

    border: 1px solid var(--bronze-color);
}


/* ==========================================================
   RESPONSIVE
   ========================================================== */

@media (max-width: 1100px) {

    .admin-offering-layout {
        grid-template-columns: 1fr;
    }

    .preview-panel {
        position: static;
    }
}


@media (max-width: 650px) {

    .admin-editor-panel,
    .preview-panel {
        padding: 1rem;
    }

    .offering-header,
    .section-heading {
        align-items: flex-start;
        flex-direction: column;
    }

    .admin-item-card {
        grid-template-columns: 1fr;
    }

    .admin-item-image,
    .admin-item-image-placeholder {
        width: 100%;
        height: 200px;
    }

    .offering-actions {
        align-items: stretch;
        flex-direction: column;
    }
}
</style>