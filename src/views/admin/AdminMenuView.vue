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
            </article>
        </section>
    </section>
</template>

<script setup>

import { onMounted, ref } from 'vue'

import { useMenuStore } from '@/stores/menuStore'

const menuStore = useMenuStore()

const showCreateForm = ref(false)

const creatingSection = ref(false)

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