<template>
    <main>
        <header>
            <div>
                <h1>Weekly Offerings</h1>

                <p v-if="auth.username">
                    Signed in as {{ auth.username }}
                </p>
            </div>

            <button @click="handleLogout">
                Log Out
            </button>
        </header>

        <p v-if="weeklyOfferings.loading">
            Loading weekly offerings...
        </p>

        <p v-else-if="weeklyOfferings.error">
            {{ weeklyOfferings.error }}
        </p>

        <p v-else-if="weeklyOfferings.offerings.length === 0">
            No weekly offerings found.
        </p>

        <section v-else>
            <article v-for="offering in weeklyOfferings.offerings" :key="offering.id">
                <h2>
                    {{ offering.startDate }} – {{ offering.endDate }}
                </h2>

                <p>
                    Status: {{ offering.status }}
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
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useWeeklyOfferingStore } from '@/stores/weeklyOfferingStore'

const router = useRouter()
const auth = useAuthStore()
const weeklyOfferings = useWeeklyOfferingStore()

onMounted(() => {
    weeklyOfferings.fetchOfferings()
})

async function handleLogout() {
    await auth.logout()
    await router.push('/admin/login')
}
</script>

<style scoped></style>