<template>
    <main class="admin-page">
        <div class="admin-page-container">

            <header class="page-header">
                <div>
                    <p class="admin-eyebrow">
                        Coccia House Admin
                    </p>

                    <h1>Announcements</h1>

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

                <button v-if="!showCreateForm" type="button" class="primary-button" @click="showCreateForm = true">
                    + Create Announcement
                </button>
            </div>

            <section v-if="showCreateForm" class="admin-card create-form-card">
                <p class="admin-eyebrow">
                    New Announcement
                </p>

                <h2>Create Draft</h2>

                <AnnouncementForm :saving="creatingAnnouncement" submit-label="Create Draft"
                    @submit="handleCreateAnnouncement" @cancel="showCreateForm = false" />
            </section>

            <p v-if="announcementStore.loading" class="state-message">
                Loading announcements...
            </p>

            <div v-if="announcementStore.error" class="error-message" role="alert">
                {{ announcementStore.error }}
            </div>

            <p v-else-if="
                announcementStore.adminAnnouncements.length === 0
            " class="state-message">
                No announcements found.
            </p>

            <section v-else class="announcement-grid">
                <article v-for="announcement in announcementStore.adminAnnouncements" :key="announcement.id"
                    class="admin-card announcement-card">
                    <div class="announcement-card-header">
                        <div>
                            <p class="announcement-placement">
                                {{ announcement.placement }}
                            </p>

                            <h2>
                                {{ announcement.title }}
                            </h2>
                        </div>

                        <span class="status-badge" :class="`status-${announcement.status.toLowerCase()}`
                            ">
                            {{ announcement.status }}
                        </span>
                    </div>

                    <p class="announcement-message">
                        {{ announcement.message }}
                    </p>

                    <div class="announcement-meta">
                        <span>
                            Type: {{ announcement.type }}
                        </span>

                        <span>
                            Order: {{ announcement.displayOrder }}
                        </span>
                    </div>
                    <div class="announcement-actions">

                        <button v-if="announcement.status === 'draft'" type="button"
                            @click="handleSchedule(announcement.id)">
                            Schedule
                        </button>

                        <button v-if="announcement.status === 'draft'" type="button"
                            @click="editingAnnouncement = announcement">
                            Edit
                        </button>

                        <button v-if="announcement.status === 'scheduled'" type="button"
                            @click="handleArchive(announcement.id)">
                            Archive
                        </button>

                        <button v-if="announcement.status === 'draft'" type="button" class="danger-button"
                            @click="handleDelete(announcement.id)">
                            Delete
                        </button>

                        <button v-if="announcement.status === 'archived'" type="button"
                            @click="handleDuplicate(announcement)">
                            Duplicate as Draft
                        </button>

                    </div>

                    <AnnouncementForm v-if="editingAnnouncement?.id === announcement.id" :announcement="announcement"
                        :saving="updatingAnnouncement" submit-label="Save Changes" @submit="data => handleUpdateAnnouncement(
                            announcement.id,
                            data
                        )" @cancel="editingAnnouncement = null" />

                </article>
            </section>

        </div>
    </main>
</template>

<script setup>
import { onMounted, ref } from "vue"
import AnnouncementForm from "@/components/admin/AnnouncementForm.vue"
import { useRouter } from "vue-router"

import { useAuthStore } from "@/stores/authStore"
import { useAnnouncementStore } from "@/stores/announcementStore"

const router = useRouter()
const auth = useAuthStore()
const announcementStore = useAnnouncementStore()
const showCreateForm = ref(false)
const creatingAnnouncement = ref(false)
const editingAnnouncement = ref(null)
const updatingAnnouncement = ref(false)

onMounted(() => {
    announcementStore.loadAdminAnnouncements()
})

async function handleLogout() {
    await auth.logout()
    await router.push("/admin/login")
}

async function handleCreateAnnouncement(data) {
    creatingAnnouncement.value = true
    announcementStore.clearError()

    try {
        const created =
            await announcementStore.createAnnouncement(data)

        if (created) {
            showCreateForm.value = false
        }

    } finally {
        creatingAnnouncement.value = false
    }
}

async function handleSchedule(id) {
    const success =
        await announcementStore.scheduleAnnouncement(id)

    if (success) {
        await announcementStore.loadAdminAnnouncements()
    }
}

async function handleArchive(id) {
    const success =
        await announcementStore.archiveAnnouncement(id)

    if (success) {
        await announcementStore.loadAdminAnnouncements()
    }
}

async function handleDelete(id) {
    const confirmed = window.confirm(
        "Delete this draft announcement?"
    )

    if (!confirmed) {
        return
    }

    await announcementStore.deleteAnnouncement(id)
}

async function handleUpdateAnnouncement(id, data) {
    updatingAnnouncement.value = true
    announcementStore.clearError()

    try {
        const success =
            await announcementStore.updateAnnouncement(
                id,
                data
            )

        if (success) {
            editingAnnouncement.value = null
        }

    } finally {
        updatingAnnouncement.value = false
    }
}

async function handleDuplicate(announcement) {
    announcementStore.clearError()

    const duplicated =
        await announcementStore.createAnnouncement({
            title: announcement.title,
            message: announcement.message,
            placement: announcement.placement.toUpperCase(),
            type: announcement.type.toUpperCase(),

            // Intentionally require new dates.
            startDateTime: null,
            endDateTime: null,

            displayOrder: announcement.displayOrder
        })

    if (duplicated) {
        editingAnnouncement.value = duplicated
    }
}

</script>

<style scoped>
.announcement-grid {
    display: grid;
    gap: 1rem;
}

.announcement-card {
    display: grid;
    gap: 1rem;
}

.announcement-card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 1rem;
}

.announcement-card-header h2 {
    margin: 0;
}

.announcement-placement {
    margin: 0 0 0.4rem;

    color: var(--bronze-bold);

    font-size: 0.75rem;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
}

.announcement-message {
    margin: 0;

    line-height: 1.6;
}

.announcement-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 1rem;

    font-size: 0.85rem;
    opacity: 0.8;
}

.status-badge {
    flex-shrink: 0;

    padding: 0.35rem 0.6rem;

    border-radius: 999px;

    font-size: 0.75rem;
    font-weight: 700;
    text-transform: uppercase;
}

.announcement-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 0.75rem;

    padding-top: 0.5rem;
    border-top: 1px solid rgba(255, 255, 255, 0.12);
}

.danger-button {
    margin-left: auto;
}

@media (max-width: 600px) {
    .announcement-card-header {
        flex-direction: column;
    }
}
</style>