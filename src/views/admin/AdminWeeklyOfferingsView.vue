<template>
    <main>
        <header>
            <div>
                <h1>
                    {{ viewingArchived ? 'Archived Weekly Offerings' : 'Weekly Offerings' }}
                </h1>

                <p v-if="auth.username">
                    Signed in as {{ auth.username }}
                </p>
            </div>

            <button @click="handleLogout">
                Log Out
            </button>
        </header>

        <button type="button" @click="toggleArchived">
            {{ viewingArchived ? '← Back to Weekly Offerings' : 'View Archived Offerings' }}
        </button>

        <button v-if="!showCreateForm && !viewingArchived" @click="showCreateForm = true">
            Create Weekly Offering
        </button>

        <WeeklyOfferingForm v-if="showCreateForm" :saving="creatingOffering" submit-label="Create Draft"
            @submit="createOffering" @cancel="showCreateForm = false" />

        <p v-if="weeklyOfferings.loading">
            Loading weekly offerings...
        </p>

        <p v-else-if="weeklyOfferings.error">
            {{ weeklyOfferings.error }}
        </p>

        <p v-else-if="weeklyOfferings.offerings.length === 0">
            {{ viewingArchived ? 'No archived weekly offerings.' : 'No weekly offerings found.' }}
        </p>

        <section v-else>
            <article v-for="offering in weeklyOfferings.offerings" :key="offering.id">
                <h2>
                    {{ offering.startDate }} – {{ offering.endDate }}
                </h2>

                <p>
                    Status: {{ getDisplayStatus(offering) }}
                </p>

                <p>
                    {{ offering.items.length }}
                    {{ offering.items.length === 1 ? 'item' : 'items' }}
                </p>
                <button @click="
                    router.push(`/admin/weekly-offerings/${offering.id}`)
                    ">
                    {{ offering.status === 'DRAFT' ? 'Continue Editing' : 'View' }}
                </button>
            </article>
        </section>
    </main>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useWeeklyOfferingStore } from '@/stores/weeklyOfferingStore'
import WeeklyOfferingForm from '@/components/admin/WeeklyOfferingForm.vue'

const router = useRouter()
const auth = useAuthStore()
const weeklyOfferings = useWeeklyOfferingStore()
const showCreateForm = ref(false)
const creatingOffering = ref(false)
const viewingArchived = ref(false)

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

</script>

<style scoped></style>