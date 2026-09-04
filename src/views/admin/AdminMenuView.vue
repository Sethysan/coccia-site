<template>
    <section>
        <header class="page-header">
            <div>
                <h1>Menu</h1>

                <p class="admin-subtext">
                    Manage menu sections, dishes, prices and visibility.
                </p>
            </div>
        </header>

        <div class="page-actions">
            <button type="button" class="primary-button" @click="showCreateForm = !showCreateForm">
                {{ showCreateForm ? 'Cancel' : '+ Add Section' }}
            </button>
        </div>

        <section v-if="showCreateForm" class="admin-card create-card">
            <div class="admin-section-heading">
                <h2>Add Menu Section</h2>
            </div>

            <form class="admin-form create-form" @submit.prevent="handleCreateSection">
                <label>
                    Section name

                    <input v-model="newSection.name" type="text" maxlength="100" placeholder="Example: Starters"
                        required>
                </label>

                <label>
                    Included with this section

                    <textarea v-model="newSection.subtitle" rows="2"
                        placeholder="Example: Served with small dinner salad, choice of side & homemade bread & butter"></textarea>
                </label>

                <label>
                    Additional note

                    <textarea v-model="newSection.footerText" rows="2"
                        placeholder="Example: Ask about available dressings."></textarea>
                </label>

                <label>
                    Display order

                    <input v-model.number="newSection.displayOrder" type="number" min="0" required>
                </label>

                <div class="admin-form-actions">
                    <button type="submit" class="primary-button" :disabled="creatingSection">
                        {{
                            creatingSection
                                ? 'Adding...'
                                : 'Add Section'
                        }}
                    </button>

                    <button type="button" :disabled="creatingSection" @click="cancelCreateSection">
                        Cancel
                    </button>
                </div>
            </form>
        </section>

        <div v-if="menuStore.error" class="error-message" role="alert">
            {{ menuStore.error }}
        </div>

        <p v-if="menuStore.loading" class="state-message">
            Loading menu...
        </p>

        <p v-else-if="menuStore.sections.length === 0" class="state-message">
            No menu sections yet.
        </p>

        <section v-else class="section-list">
            <article v-for="section in menuStore.sections" :key="section.id" class="admin-card section-card"
                :class="{ inactive: !section.active }">

                <!-- EDIT MODE -->
                <form v-if="editingSectionId === section.id" class="admin-form section-edit-form"
                    @submit.prevent="handleSaveSection(section.id)">
                    <div class="admin-section-heading">
                        <h2>Edit Section</h2>
                    </div>

                    <label>
                        Section name

                        <input v-model="editSection.name" type="text" maxlength="100" required>
                    </label>

                    <label>
                        Included with this section

                        <textarea v-model="editSection.subtitle" rows="2"></textarea>
                    </label>

                    <label>
                        Additional note

                        <textarea v-model="editSection.footerText" rows="2"></textarea>
                    </label>

                    <label>
                        Display order

                        <input v-model.number="editSection.displayOrder" type="number" min="0" required>
                    </label>

                    <div class="admin-form-actions">
                        <button type="submit" class="primary-button" :disabled="savingSectionId === section.id">
                            {{
                                savingSectionId === section.id
                                    ? 'Saving...'
                                    : 'Save Changes'
                            }}
                        </button>

                        <button type="button" :disabled="savingSectionId === section.id" @click="cancelEditingSection">
                            Cancel
                        </button>
                    </div>
                </form>


                <!-- NORMAL CARD -->

                <template v-else>
                    <div class="section-heading">
                        <div>
                            <h2>{{ section.name }}</h2>

                            <p v-if="section.subtitle" class="section-subtitle">
                                {{ section.subtitle }}
                            </p>
                        </div>

                        <span class="status-badge" :class="section.active
                            ? 'status-current'
                            : 'status-archived'
                            ">
                            {{ section.active ? 'Active' : 'Inactive' }}
                        </span>
                    </div>

                    <p v-if="section.footerText" class="section-footer">
                        {{ section.footerText }}
                    </p>

                    <div class="section-meta">
                        Display order: {{ section.displayOrder }}
                    </div>

                    <div class="section-actions">

                        <button type="button" @click="toggleSectionItems(section.id)">
                            {{
                                openSectionId === section.id
                                    ? 'Hide Items'
                                    : 'Manage Items'
                            }}
                        </button>

                        <button type="button" @click="startEditingSection(section)">
                            Edit Section
                        </button>

                        <button type="button" :disabled="savingSectionId === section.id"
                            @click="toggleSectionActive(section)">
                            {{
                                section.active
                                    ? 'Deactivate'
                                    : 'Reactivate'
                            }}
                        </button>
                    </div>
                </template>

                <!-- SELECTED SECTION ACTIVE EDIT -->

                <section v-if="
                    openSectionId === section.id
                    && editingSectionId !== section.id
                " class="menu-items-panel">

                    <div class="admin-section-heading">
                        <div>
                            <h3>Menu Items</h3>

                            <span>
                                {{
                                    (menuStore.itemsBySection[section.id] ?? []).length
                                }}
                                {{
                                    (menuStore.itemsBySection[section.id] ?? []).length === 1
                                        ? 'item'
                                        : 'items'
                                }}
                            </span>
                        </div>

                        <button v-if="addingItemSectionId !== section.id" type="button" class="primary-button"
                            @click="startAddingMenuItem(section.id)">
                            + Add Menu Item
                        </button>
                    </div>

                    <div v-if="addingItemSectionId === section.id" class="menu-item-form-panel">
                        <MenuItemForm :item="editingMenuItem" :saving="savingMenuItem" :default-display-order="(menuStore.itemsBySection[section.id] ?? []).length
                            " @submit="
                                payload =>
                                    handleSaveMenuItem(section.id, payload)
                            " @cancel="cancelMenuItemForm" />
                    </div>

                    <p v-if="
                        (menuStore.itemsBySection[section.id] ?? []).length === 0
                    " class="state-message">
                        No items in this section yet.
                    </p>

                    <div v-else class="menu-item-list">
                        <article v-for="item in menuStore.itemsBySection[section.id]" :key="item.id"
                            class="menu-item-row">
                            <div>
                                <strong>{{ item.recipeName }}</strong>

                                <p v-if="item.description">
                                    {{ item.description }}
                                </p>

                                <ul class="menu-item-prices">
                                    <li v-for="price in item.prices" :key="price.id">
                                        <span v-if="price.label">
                                            {{ price.label }}:
                                        </span>

                                        ${{ price.amount }}
                                    </li>
                                </ul>
                            </div>

                            <div class="menu-item-actions">
                                <span>
                                    {{ item.visible ? 'Visible' : 'Hidden' }}
                                </span>

                                <button type="button" @click="startEditingMenuItem(section.id, item)">
                                    Edit
                                </button>
                            </div>
                        </article>
                    </div>
                </section>

            </article>
        </section>
    </section>
</template>

<script setup>

import { onMounted, ref } from 'vue'

import { useMenuStore } from '@/stores/menuStore'
import MenuItemForm from '@/components/admin/MenuItemForm.vue'

const menuStore = useMenuStore()

const showCreateForm = ref(false)

const creatingSection = ref(false)

const editingSectionId = ref(null)
const savingSectionId = ref(null)
const openSectionId = ref(null)

const addingItemSectionId = ref(null)
const editingMenuItem = ref(null)
const savingMenuItem = ref(false)

const editSection = ref({
    name: '',
    subtitle: '',
    footerText: '',
    displayOrder: 0,
    active: true
})

const newSection = ref({
    name: '',
    subtitle: '',
    footerText: '',
    displayOrder: 0
})

onMounted(async () => {
    await menuStore.fetchSections()
})

function cancelCreateSection() {
    showCreateForm.value = false

    newSection.value = {
        name: '',
        subtitle: '',
        footerText: '',
        displayOrder: 0
    }

    menuStore.clearError()
}

function startEditingSection(section) {
    menuStore.clearError()

    editingSectionId.value = section.id

    editSection.value = {
        name: section.name,
        subtitle: section.subtitle ?? '',
        footerText: section.footerText ?? '',
        displayOrder: section.displayOrder,
        active: section.active
    }
}

function cancelEditingSection() {
    editingSectionId.value = null

    editSection.value = {
        name: '',
        subtitle: '',
        footerText: '',
        displayOrder: 0,
        active: true
    }

    menuStore.clearError()
}

async function handleSaveSection(sectionId) {
    const name = editSection.value.name.trim()

    if (!name) {
        return
    }

    savingSectionId.value = sectionId
    menuStore.clearError()

    try {
        const savedSection =
            await menuStore.saveSection(
                sectionId,
                {
                    name,
                    subtitle: editSection.value.subtitle,
                    footerText: editSection.value.footerText,
                    displayOrder:
                        editSection.value.displayOrder,
                    active: editSection.value.active
                }
            )

        if (savedSection) {
            cancelEditingSection()
        }

    } finally {
        savingSectionId.value = null
    }
}

async function toggleSectionActive(section) {
    const action = section.active
        ? 'deactivate'
        : 'reactivate'

    const confirmed = window.confirm(
        `Are you sure you want to ${action} ${section.name}?`
    )

    if (!confirmed) {
        return
    }

    savingSectionId.value = section.id
    menuStore.clearError()

    try {
        await menuStore.saveSection(
            section.id,
            {
                name: section.name,
                subtitle: section.subtitle,
                footerText: section.footerText,
                displayOrder: section.displayOrder,
                active: !section.active
            }
        )

    } finally {
        savingSectionId.value = null
    }
}

function startAddingMenuItem(sectionId) {
    editingMenuItem.value = null
    addingItemSectionId.value = sectionId

    menuStore.clearError()
}

function startEditingMenuItem(sectionId, item) {
    openSectionId.value = sectionId
    addingItemSectionId.value = sectionId
    editingMenuItem.value = item

    menuStore.clearError()
}

function cancelMenuItemForm() {
    addingItemSectionId.value = null
    editingMenuItem.value = null

    menuStore.clearError()
}

async function handleSaveMenuItem(sectionId, payload) {
    savingMenuItem.value = true
    menuStore.clearError()

    try {
        const savedItem = editingMenuItem.value
            ? await menuStore.saveItem(
                sectionId,
                editingMenuItem.value.id,
                payload
            )
            : await menuStore.addItem(
                sectionId,
                payload
            )

        if (savedItem) {
            cancelMenuItemForm()
        }

    } finally {
        savingMenuItem.value = false
    }
}

async function handleCreateSection() {
    const name = newSection.value.name.trim()

    if (!name) {
        return
    }

    creatingSection.value = true
    menuStore.clearError()

    try {
        const createdSection =
            await menuStore.addSection({
                name,
                subtitle: newSection.value.subtitle,
                footerText: newSection.value.footerText,
                displayOrder:
                    newSection.value.displayOrder,
                active: true
            })

        if (createdSection) {
            cancelCreateSection()
        }

    } finally {
        creatingSection.value = false
    }
}

async function toggleSectionItems(sectionId) {
    if (openSectionId.value === sectionId) {
        openSectionId.value = null
        return
    }

    openSectionId.value = sectionId

    await menuStore.fetchItems(sectionId)
}

</script>

<style scoped>
.page-actions {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    gap: 1rem;
}

.create-card {
    margin: 1rem 0;
}

.section-list {
    display: grid;
    gap: 0.75rem;

    margin-top: 1.5rem;
}

.section-card {
    transition: opacity 180ms ease;
}

.section-card.inactive {
    opacity: 0.65;
}

.section-heading {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 1rem;
}

.section-heading h2 {
    margin: 0;
}

.section-subtitle {
    margin: 0.4rem 0 0;
}

.section-footer {
    margin: 1rem 0 0;
}

.section-meta {
    margin-top: 1rem;

    font-size: 0.9rem;
    opacity: 0.7;
}

.create-form {
    grid-template-columns: 1fr;
}

.create-form .admin-form-actions {
    margin-top: 0;
}

.section-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;

    margin-top: 1rem;
}

.section-edit-form {
    grid-template-columns: 1fr;
}

.menu-items-panel {
    margin-top: 1.25rem;
    padding-top: 1.25rem;

    border-top: 1px solid rgba(255, 255, 255, 0.12);
}

.menu-items-panel h3 {
    margin: 0;
}

.menu-item-form-panel {
    margin: 1rem 0;
    padding: 1rem;

    background: rgba(255, 255, 255, 0.04);

    border: 1px solid var(--bronze-color);
    border-radius: 0.5rem;
}

.menu-item-actions {
    display: flex;
    align-items: center;
    gap: 0.75rem;
}

.menu-item-prices {
    margin: 0.5rem 0 0;
    padding: 0;

    list-style: none;
}

.menu-item-prices li {
    margin-top: 0.2rem;
    font-weight: 700;
}

.menu-item-list {
    display: grid;
    gap: 0.75rem;

    margin-top: 1rem;
}

.menu-item-row {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 1rem;

    padding: 0.75rem;

    border: 1px solid rgba(255, 255, 255, 0.12);
    border-radius: 0.4rem;
}

.menu-item-row p {
    margin: 0.35rem 0 0;
}

@media (max-width: 700px) {
    .page-actions {
        align-items: stretch;
        flex-direction: column;
    }

    .section-heading {
        flex-direction: column;
    }
}
</style>