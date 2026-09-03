<template>
    <main class="admin-page">
        <div class="admin-page-container">

            <header class="admin-shell-header">
                <div>
                    <p class="admin-eyebrow">
                        Coccia House
                    </p>

                    <h1 class="admin-shell-title">
                        Admin
                    </h1>
                </div>

                <div class="admin-shell-actions">
                    <span
                        v-if="auth.displayName"
                        class="admin-user"
                    >
                        {{ auth.displayName }}
                    </span>

                    <button
                        type="button"
                        @click="handleLogout"
                    >
                        Log Out
                    </button>
                </div>
            </header>

            <nav
                class="admin-nav"
                aria-label="Admin navigation"
            >
                <RouterLink to="/admin">
                    Home
                </RouterLink>

                <RouterLink to="/admin/weekly-offerings">
                    Weekly Features
                </RouterLink>

                <RouterLink to="/admin/menu">
                    Menu
                </RouterLink>

                <RouterLink to="/admin/recipes">
                    Recipes
                </RouterLink>

                <RouterLink to="/admin/hours">
                    Hours
                </RouterLink>

                <RouterLink to="/admin/announcements">
                    Announcements
                </RouterLink>

                <RouterLink
                    v-if="auth.isAdmin"
                    to="/admin/users"
                >
                    Users
                </RouterLink>
            </nav>

            <section class="admin-shell-content">
                <RouterView />
            </section>

        </div>
    </main>
</template>

<script setup>
import {
    RouterLink,
    RouterView,
    useRouter
} from 'vue-router'

import { useAuthStore } from '@/stores/authStore'

const router = useRouter()
const auth = useAuthStore()

async function handleLogout() {
    await auth.logout()
    await router.push('/admin/login')
}
</script>

<style scoped>
.admin-shell-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 1rem;

    margin-bottom: 1rem;
}

.admin-shell-title {
    margin: 0;
}

.admin-shell-actions {
    display: flex;
    align-items: center;
    gap: 1rem;
}

.admin-user {
    font-size: 0.9rem;
    opacity: 0.8;
}

.admin-nav {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;

    margin-bottom: 1.5rem;
    padding-bottom: 1rem;

    border-bottom: 1px solid var(--bronze-color);
}

.admin-nav a {
    padding: 0.65rem 0.9rem;

    color: var(--text-primary);
    text-decoration: none;

    border: 1px solid transparent;
    border-radius: 0.35rem;
}

.admin-nav a:hover {
    border-color: var(--bronze-hover);
}

.admin-nav a.router-link-exact-active {
    background: var(--bronze-bold);
    border-color: var(--bronze-hover);
}

.admin-shell-content {
    min-width: 0;
}

@media (max-width: 700px) {
    .admin-shell-header {
        align-items: flex-start;
        flex-direction: column;
    }

    .admin-shell-actions {
        width: 100%;
        justify-content: space-between;
    }

    .admin-nav {
        overflow-x: auto;
        flex-wrap: nowrap;

        padding-bottom: 0.75rem;
    }

    .admin-nav a {
        flex: 0 0 auto;
    }
}
</style>