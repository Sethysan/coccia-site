<template>
    <section>
        <header class="page-header">
            <div>
                <h1>
                    Welcome{{ auth.displayName ? `, ${auth.displayName}` : '' }}
                </h1>

                <p class="admin-subtext">
                    What would you like to work on?
                </p>
            </div>
        </header>

        <section class="dashboard-grid">

            <RouterLink to="/admin/weekly-offerings" class="admin-card dashboard-card">
                <h2>Weekly Features</h2>

                <p>
                    Create, edit, schedule, and archive weekly dinner,
                    soup, and dessert features.
                </p>
            </RouterLink>

            <RouterLink to="/admin/menu" class="admin-card dashboard-card">
                <h2>Menu</h2>

                <p>
                    Manage menu sections, dishes, prices, and visibility.
                </p>
            </RouterLink>

            <RouterLink to="/admin/recipes" class="admin-card dashboard-card">
                <h2>Recipes</h2>

                <p>
                    Manage reusable dishes used throughout the website.
                </p>
            </RouterLink>

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

            <RouterLink v-if="auth.isAdmin" to="/admin/users" class="admin-card dashboard-card">
                <h2>Users & Access</h2>

                <p>
                    Manage website users, roles, and access.
                </p>
            </RouterLink>

        </section>
    </section>
</template>

<script setup>
import { RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const auth = useAuthStore()
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

@media (max-width: 700px) {
    .dashboard-grid {
        grid-template-columns: 1fr;
    }
}
</style>