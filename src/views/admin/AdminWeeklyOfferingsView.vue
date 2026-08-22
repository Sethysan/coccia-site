<template>
    <main class="admin-page">
        <div class="admin-page-container">

            <header class="page-header">
                <div>
                    <p class="admin-eyebrow">
                        Coccia House Admin
                    </p>

                    <h1>
                        {{
                            viewingArchived
                                ? 'Archived Weekly Offerings'
                                : 'Weekly Offerings'
                        }}
                    </h1>

                    <p v-if="auth.username" class="admin-subtext">
                        Signed in as {{ auth.username }}
                    </p>
                </div>

                <button type="button" @click="handleLogout">
                    Log Out
                </button>
            </header>

            <div class="page-actions">
                <button type="button" @click="router.push('/admin')">
                    ← Dashboard
                </button>

                <button type="button" @click="toggleArchived">
                    {{
                        viewingArchived
                            ? '← Back to Weekly Offerings'
                            : 'View Archived Offerings'
                    }}
                </button>

                <button v-if="!showCreateForm && !viewingArchived" type="button" class="primary-button"
                    @click="showCreateForm = true">
                    + Create Weekly Offering
                </button>
            </div>

            <div v-if="viewingArchived" class="archive-search">
                <label for="recipe-search">
                    Search archived offerings by recipe
                </label>

                <input id="recipe-search" v-model="recipeSearch" type="search" placeholder="Start typing a recipe..."
                    autocomplete="off" @input="handleRecipeSearchInput" />

                <div v-if="
                    recipeSearch.trim()
                    && !selectedRecipeName
                    && recipeSuggestions.length
                " class="recipe-suggestion-area">
                    <p class="recipe-suggestion-label">
                        Select a recipe
                    </p>

                    <ul class="recipe-suggestions">
                        <li v-for="recipeName in recipeSuggestions" :key="recipeName">
                            <button type="button" @click="selectRecipe(recipeName)">
                                {{ recipeName }}
                            </button>
                        </li>
                    </ul>
                </div>
            </div>

            <section v-if="recipeSearchSummary" class="recipe-search-summary">
                <div>
                    <p class="admin-eyebrow">
                        Recipe History
                    </p>

                    <h2>
                        {{ recipeSearchSummary.recipeName }}
                    </h2>
                </div>

                <div class="recipe-summary-stats">
                    <div class="recipe-stat">
                        <span class="summary-label">
                            Last Featured
                        </span>

                        <strong>
                            {{
                                formatDateRange(
                                    recipeSearchSummary.lastStartDate,
                                    recipeSearchSummary.lastEndDate
                                )
                            }}
                        </strong>
                    </div>

                    <div class="recipe-stat times-featured">
                        <span class="summary-label">
                            Times Featured
                        </span>

                        <strong>
                            {{ recipeSearchSummary.count }}
                        </strong>
                    </div>
                </div>
            </section>

            <section v-if="showCreateForm" class="admin-card create-form-card">
                <p class="admin-eyebrow">
                    New Weekly Offering
                </p>

                <h2>Create Draft</h2>

                <WeeklyOfferingForm :saving="creatingOffering" submit-label="Create Draft" @submit="createOffering"
                    @cancel="showCreateForm = false" />
            </section>

            <p v-if="weeklyOfferings.loading" class="state-message">
                Loading weekly offerings...
            </p>

            <div v-else-if="weeklyOfferings.error" class="error-message" role="alert">
                {{ weeklyOfferings.error }}
            </div>

            <p v-else-if="weeklyOfferings.offerings.length === 0" class="state-message">
                {{
                    viewingArchived
                        ? 'No archived weekly offerings.'
                        : 'No weekly offerings found.'
                }}
            </p>

            <p v-if="
                viewingArchived
                && recipeSearch.trim()
                && !selectedRecipeName
                && recipeSuggestions.length === 0
            " class="state-message">
                No archived recipes match
                “{{ recipeSearch.trim() }}”.
            </p>

            <section v-else class="offering-grid">
                <article v-for="offering in filteredOfferings" :key="offering.id" class="admin-card offering-card">
                    <div class="offering-card-header">
                        <div>
                            <p class="offering-label">
                                Weekly Offering
                            </p>

                            <h2>
                                {{ formatDateRange(
                                    offering.startDate,
                                    offering.endDate
                                ) }}
                            </h2>
                        </div>

                        <span class="status-badge" :class="`status-${getDisplayStatus(offering).toLowerCase()}`">
                            {{ getDisplayStatus(offering) }}
                        </span>
                    </div>

                    <div class="offering-meta">
                        <span>
                            {{ offering.items.length }}
                            {{
                                offering.items.length === 1
                                    ? 'item'
                                    : 'items'
                            }}
                        </span>
                    </div>

                    <ul v-if="viewingArchived && offering.items.length" class="recipe-history-list">

                        <li v-for="item in offering.items" :key="item.id">
                            {{ item.recipeName }}
                        </li>

                    </ul>

                    <button type="button" class="card-action" @click="
                        router.push(
                            `/admin/weekly-offerings/${offering.id}`
                        )
                        ">
                        {{
                            offering.status === 'DRAFT'
                                ? 'Continue Editing'
                                : 'View Offering'
                        }}
                    </button>
                </article>
            </section>
        </div>
    </main>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useWeeklyOfferingStore } from '@/stores/weeklyOfferingStore'
import WeeklyOfferingForm from '@/components/admin/WeeklyOfferingForm.vue'
import { formatDateRange } from '@/utils/dateFormat'

const router = useRouter()
const auth = useAuthStore()
const weeklyOfferings = useWeeklyOfferingStore()
const showCreateForm = ref(false)
const creatingOffering = ref(false)
const viewingArchived = ref(false)
const recipeSearch = ref('')
const selectedRecipeName = ref(null)

onMounted(() => {
    weeklyOfferings.fetchOfferings()
})

async function handleLogout() {
    await auth.logout()
    await router.push('/admin/login')
}

async function toggleArchived() {
    viewingArchived.value = !viewingArchived.value

    if (viewingArchived.value) {
        await weeklyOfferings.fetchOfferings('ARCHIVED')
    } else {
        await weeklyOfferings.fetchOfferings()
    }
}

async function createOffering(formData) {
    creatingOffering.value = true

    try {
        const created =
            await weeklyOfferings.createOffering(
                formData.startDate,
                formData.endDate
            )

        if (!created) {
            window.alert(
                weeklyOfferings.error
                ?? 'Unable to create weekly offering.'
            )

            weeklyOfferings.clearError()

            return
        }

        showCreateForm.value = false

        await router.push(
            `/admin/weekly-offerings/${created.id}`
        )

    } finally {
        creatingOffering.value = false
    }
}

function getDisplayStatus(offering) {
    if (offering.status === 'ARCHIVED') {
        return 'ARCHIVED'
    }

    if (offering.status === 'DRAFT') {
        return 'DRAFT'
    }

    const today = new Date()
        .toISOString()
        .slice(0, 10)

    if (
        offering.startDate <= today
        && offering.endDate >= today
    ) {
        return 'CURRENT'
    }

    return 'SCHEDULED'
}

const archivedRecipeNames = computed(() => {
    const names = weeklyOfferings.offerings
        .flatMap(offering =>
            offering.items.map(item => item.recipeName)
        )
        .filter(Boolean)

    return [...new Set(names)]
        .sort((a, b) => a.localeCompare(b))
})

const recipeSuggestions = computed(() => {
    const search = recipeSearch.value
        .trim()
        .toLowerCase()

    if (!viewingArchived.value || !search) {
        return []
    }

    return archivedRecipeNames.value.filter(
        name =>
            name.toLowerCase().includes(search)
    )
})

const filteredOfferings = computed(() => {
    if (!viewingArchived.value) {
        return weeklyOfferings.offerings
    }

    if (!recipeSearch.value.trim()) {
        return weeklyOfferings.offerings
    }

    if (!selectedRecipeName.value) {
        return []
    }

    return weeklyOfferings.offerings.filter(
        offering =>
            offering.items.some(
                item =>
                    item.recipeName === selectedRecipeName.value
            )
    )
})

const recipeSearchSummary = computed(() => {
    if (!selectedRecipeName.value) {
        return null
    }

    const matches = weeklyOfferings.offerings
        .filter(offering =>
            offering.items.some(
                item =>
                    item.recipeName === selectedRecipeName.value
            )
        )
        .sort(
            (a, b) =>
                b.startDate.localeCompare(a.startDate)
        )

    if (!matches.length) {
        return null
    }

    return {
        recipeName: selectedRecipeName.value,
        count: matches.length,
        lastStartDate: matches[0].startDate,
        lastEndDate: matches[0].endDate
    }
})

function selectRecipe(recipeName) {
    selectedRecipeName.value = recipeName
    recipeSearch.value = recipeName
}

function handleRecipeSearchInput() {
    if (
        selectedRecipeName.value
        && recipeSearch.value !== selectedRecipeName.value
    ) {
        selectedRecipeName.value = null
    }

    if (!recipeSearch.value.trim()) {
        selectedRecipeName.value = null
    }
}

</script>

<style scoped>
/* ==========================================================
   HEADER
   ========================================================== */


.offering-label {
    margin: 0;

    color: var(--bronze-bold);

    font-size: 0.75rem;
    font-weight: 700;
    letter-spacing: 0.12em;
    text-transform: uppercase;
}

/* ==========================================================
   CREATE FORM
   ========================================================== */

.create-form-card {
    margin-bottom: 1.5rem;
    padding: 1.5rem;

    background: var(--background-dark-trans);

    border: 1px solid var(--bronze-color);
    border-radius: 0.6rem;
}

.create-form-card h2 {
    margin: 0.25rem 0 1rem;
}


/* ==========================================================
   OFFERING GRID
   ========================================================== */

.offering-grid {
    display: grid;
    grid-template-columns:
        repeat(2, minmax(0, 1fr));

    gap: 1rem;
}

.offering-card {
    display: grid;
    gap: 1rem;

    padding: 1.25rem;

    background: var(--background-dark-trans);

    border: 1px solid var(--bronze-color);
    border-radius: 0.6rem;

    box-shadow: var(--shadow-soft);
}

.offering-card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 1rem;
}

.offering-card h2 {
    margin: 0.25rem 0 0;

    font-size: 1.25rem;
}

.offering-meta {
    color: var(--text-secondary);

    font-size: 0.9rem;
}

.card-action {
    width: fit-content;
}

.recipe-history-list {
    margin: 0;
    padding-left: 1.25rem;

    color: var(--text-primary);
}

.recipe-history-list li {
    margin: 0.25rem 0;

    line-height: 1.4;
}

/* ==========================================================
   ARCHIVE SEARCH
   ========================================================== */

.archive-search {
    display: grid;
    gap: 0.4rem;

    max-width: 420px;
    margin-bottom: 1.5rem;
}

.archive-search label {
    color: var(--text-secondary);

    font-size: 0.85rem;
    font-weight: 700;
}

.archive-search input {
    width: 100%;
    padding: 0.7rem 0.85rem;

    color: var(--text-primary);
    background: var(--background-dark-trans);

    border: 1px solid var(--bronze-color);
    border-radius: 0.35rem;

    font: inherit;
}

.recipe-search-summary {
    display: flex;
    justify-content: space-between;
    gap: 2rem;
    align-items: center;

    margin-bottom: 1.5rem;
    padding: 1.25rem;

    background: var(--background-dark-trans);

    border: 1px solid var(--bronze-color);
    border-radius: 0.6rem;
}

.recipe-search-summary h2 {
    margin: 0.25rem 0 0;
}

.recipe-summary-stats {
    display: flex;
    gap: 2rem;
}

.recipe-summary-stats>div {
    display: grid;
    gap: 0.3rem;
}

.summary-label {
    color: var(--text-secondary);

    font-size: 0.75rem;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
}

.recipe-suggestions {
    display: grid;
    gap: 0.35rem;

    margin: 0;
    padding: 0.4rem;

    background: var(--background-dark-trans);

    border: 1px solid var(--bronze-color);
    border-radius: 0.4rem;

    list-style: none;
}

.recipe-suggestions button {
    width: 100%;
    text-align: left;
}

.recipe-summary-stats {
    display: flex;
    gap: 2rem;
    align-items: flex-start;
}

.recipe-stat {
    display: grid;
    gap: 0.5rem;
}

.summary-label {
    color: var(--text-secondary);

    font-size: 0.75rem;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
}

.recipe-stat strong {
    margin: 0;
}

.times-featured {
    text-align: center;
}

/* ==========================================================
   RESPONSIVE
   ========================================================== */

@media (max-width: 800px) {
    .offering-grid {
        grid-template-columns: 1fr;
    }
}

@media (max-width: 600px) {

    .card-action {
        width: 100%;
    }

    .offering-card-header {
        flex-direction: column;
    }
}

@media (max-width: 700px) {
    .recipe-search-summary {
        align-items: flex-start;
        flex-direction: column;
    }

    .recipe-summary-stats {
        width: 100%;
        justify-content: space-between;
    }
}
</style>