<template>
    <section>

        <header class="page-header">
            <div>
                <h1>Announcements</h1>

                <p class="admin-subtext">
                    Manage website announcements and customer updates.
                </p>
            </div>
        </header>

        <div class="page-actions">
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

        <div class="announcement-filters">
            <button type="button" :class="{ active: selectedFilter === 'active' }" @click="selectedFilter = 'active'">
                Active ({{ announcementCounts.active }})
            </button>

            <button type="button" :class="{ active: selectedFilter === 'drafts' }" @click="selectedFilter = 'drafts'">
                Drafts ({{ announcementCounts.drafts }})
            </button>

            <button type="button" :class="{ active: selectedFilter === 'archived' }"
                @click="selectedFilter = 'archived'">
                Archived ({{ announcementCounts.archived }})
            </button>
        </div>

        <p v-if="announcementStore.loading" class="state-message">
            Loading announcements...
        </p>

        <div v-if="announcementStore.error" class="error-message" role="alert">
            {{ announcementStore.error }}
        </div>

        <p v-else-if="
            filteredAnnouncements.length === 0
        " class="state-message">
            No {{ emptyFilterLabel }} announcements.
        </p>

        <section v-else class="announcement-grid">

            <article v-for="announcement in filteredAnnouncements" :key="announcement.id"
                class="admin-card announcement-card">
                <div class="announcement-card-header">
                    <div>
                        <p class="announcement-placement">
                            {{ formatLabel(announcement.placement) }}
                        </p>

                        <h2>
                            {{ announcement.title }}
                        </h2>
                    </div>

                    <span class="status-badge" :class="`status-${announcement.status.toLowerCase()}`
                        ">
                        {{ formatLabel(announcement.status) }}
                    </span>
                </div>

                <p class="announcement-message">
                    {{ announcement.message }}
                </p>

                <div class="announcement-meta">
                    <span>
                        <strong>Type:</strong>
                        {{ formatLabel(announcement.type) }}
                    </span>

                    <span>
                        <strong>Starts:</strong>
                        {{ formatDateTime(announcement.startDateTime) }}
                    </span>

                    <span>
                        <strong>Ends:</strong>
                        {{
                            announcement.endDateTime
                                ? formatDateTime(announcement.endDateTime)
                                : "No end date"
                        }}
                    </span>

                    <span>
                        <strong>Display order:</strong>
                        {{ announcement.displayOrder }}
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

    </section>
</template>

<script setup>
import { computed, onMounted, ref } from "vue"
import AnnouncementForm from "@/components/admin/AnnouncementForm.vue"
import { useAnnouncementStore } from "@/stores/announcementStore"

const announcementStore = useAnnouncementStore()
const showCreateForm = ref(false)
const creatingAnnouncement = ref(false)
const editingAnnouncement = ref(null)
const updatingAnnouncement = ref(false)

onMounted(() => {
    announcementStore.loadAdminAnnouncements()
})

async function handleCreateAnnouncement(data) {
    creatingAnnouncement.value = true
    announcementStore.clearError()

    try {
        const created =
            await announcementStore.createAnnouncement(data)

        if (created) {

            selectedFilter.value = "drafts"
            showCreateForm.value = false
        }

    } finally {
        creatingAnnouncement.value = false
    }
}

async function handleSchedule(id) {
    const confirmed = window.confirm(
        "Schedule this announcement? It will appear on the website when its start date and time arrives."
    )

    if (!confirmed) {
        return
    }

    const success =
        await announcementStore.scheduleAnnouncement(id)

    if (success) {
        await announcementStore.loadAdminAnnouncements()
    }
}

async function handleArchive(id) {
    const confirmed = window.confirm(
        "Archive this announcement? It will no longer appear on the website."
    )

    if (!confirmed) {
        return
    }

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
        selectedFilter.value = "drafts"
        editingAnnouncement.value = duplicated
    }
}

function formatLabel(value) {
    if (!value) {
        return ""
    }

    const labels = {
        banner: "Site Banner",
        news: "Latest News",
        general: "General",
        info: "Information",
        event: "Event",
        closure: "Closure",
        warning: "Warning",
        draft: "Draft",
        scheduled: "Scheduled",
        archived: "Archived"
    }

    return labels[value] ?? value
}

function formatDateTime(value) {
    if (!value) {
        return "No date set"
    }

    return new Intl.DateTimeFormat("en-US", {
        month: "short",
        day: "numeric",
        year: "numeric",
        hour: "numeric",
        minute: "2-digit"
    }).format(new Date(value))
}

const selectedFilter = ref("active")

const filteredAnnouncements = computed(() => {
    return announcementStore.adminAnnouncements.filter(
        announcement => {
            if (selectedFilter.value === "active") {
                return announcement.status === "scheduled"
            }

            if (selectedFilter.value === "drafts") {
                return announcement.status === "draft"
            }

            if (selectedFilter.value === "archived") {
                return announcement.status === "archived"
            }

            return true
        }
    )
})

const announcementCounts = computed(() => {
    return {
        active:
            announcementStore.adminAnnouncements.filter(
                announcement =>
                    announcement.status === "scheduled"
            ).length,

        drafts:
            announcementStore.adminAnnouncements.filter(
                announcement =>
                    announcement.status === "draft"
            ).length,

        archived:
            announcementStore.adminAnnouncements.filter(
                announcement =>
                    announcement.status === "archived"
            ).length
    }
})

const emptyFilterLabel = computed(() => {
    const labels = {
        active: "active",
        drafts: "draft",
        archived: "archived"
    }

    return labels[selectedFilter.value]
})

</script>

<style scoped>
.page-actions {
    display: flex;
    justify-content: flex-end;

    margin-bottom: 1rem;
}

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
    gap: 0.5rem 1.5rem;

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

.announcement-filters {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;

    margin-bottom: 1rem;
}

.announcement-filters button.active {
    font-weight: 700;
    text-decoration: underline;
}

@media (max-width: 600px) {
    .announcement-card-header {
        flex-direction: column;
    }
}
</style>