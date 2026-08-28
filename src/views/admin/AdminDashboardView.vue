<template>
    <main class="admin-page">
        <div class="admin-page-container">

            <header class="page-header">
                <div>
                    <p class="admin-eyebrow">
                        Coccia House
                    </p>

                    <h1>
                        Admin Dashboard
                    </h1>

                    <p class="admin-subtext">
                        Welcome{{ auth.displayName ? `, ${auth.displayName}` : '' }}.
                        Manage the content that appears on the public website.
                    </p>
                </div>

                <button type="button" @click="handleLogout">
                    Log Out
                </button>
            </header>

            <section class="dashboard-grid">

                <RouterLink to="/admin/weekly-offerings" class="admin-card dashboard-card">
                    <h2>Weekly Features</h2>

                    <p>
                        Create, edit, schedule, and archive weekly dinner,
                        soup, and dessert features.
                    </p>
                </RouterLink>

                <article class="admin-card dashboard-card is-coming-soon">
                    <h2>Menu</h2>

                    <p>
                        Manage menu sections, items, descriptions, and prices.
                    </p>

                    <span>Coming soon</span>
                </article>

                <RouterLink to="/admin/hours" class="admin-card dashboard-card">
                    <h2>Store Hours</h2>

                    <p>
                        Manage regular hours and the public open/closed status.
                    </p>
                </RouterLink>

                <RouterLink to="/admin/announcements" class="admin-card dashboard-card">
                    <h2>Announcements</h2>

                    <p>
                        Schedule website notices, closures, and special messages.
                    </p>
                </RouterLink>

                <RouterLink to="/admin/recipes" class="admin-card dashboard-card">
                    <h2>Recipes</h2>

                    <p>
                        Manage reusable dishes used by weekly features.
                    </p>
                </RouterLink>
                
                <RouterLink v-if="auth.isAdmin" to="/admin/users" class="admin-card dashboard-card">
                    <h2>Users & Access</h2>

                    <p>
                        Manage website users, roles, and access.
                    </p>
                </RouterLink>

            </section>

        </div>
    </main>
</template>

<script setup>
import { RouterLink, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const router = useRouter()
const auth = useAuthStore()

async function handleLogout() {
    await auth.logout()
    await router.push('/admin/login')
}
</script>

<style scoped>
.dashboard-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 1rem;
}

.dashboard-card {
    display: block;

    color: var(--text-primary);
    text-decoration: none;

    transition:
        transform 180ms ease,
        border-color 180ms ease;
}

.dashboard-card:hover {
    transform: translateY(-2px);
    border-color: var(--bronze-hover);
}

.dashboard-card h2 {
    margin: 0 0 0.75rem;
}

.dashboard-card p {
    margin: 0;

    line-height: 1.6;
}

.is-coming-soon {
    opacity: 0.7;
}

.is-coming-soon span {
    display: inline-block;

    margin-top: 1rem;

    color: var(--bronze-bold);

    font-size: 0.75rem;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
}

@media (max-width: 700px) {
    .dashboard-grid {
        grid-template-columns: 1fr;
    }
}
</style>