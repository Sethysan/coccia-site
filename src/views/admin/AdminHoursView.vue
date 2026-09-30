<template>
    <section>

        <header class="page-header">
            <div>
                <h1>Store Hours</h1>

                <p class="admin-subtext">
                    Manage the hours customers see on the website.
                </p>
            </div>
        </header>

        <p v-if="hoursStore.loading" class="state-message">
            Loading store hours...
        </p>

        <div v-if="hoursStore.error" class="error-message" role="alert">
            {{ hoursStore.error.message }}
        </div>

        <section v-if="!hoursStore.loading" class="hours-container">

            <Transition name="day-expand" mode="out-in">

                <!-- WEEK VIEW -->

                <div v-if="expandedDayId === null" key="week-grid" class="hours-grid">
                    <button v-for="day in editingHours" :key="day.day" type="button" class="day-tile"
                        @click="openDayWorkspace(day)">
                        <span class="day-tile__name">
                            {{ day.name }}
                        </span>

                        <span class="day-tile__hours" :class="{ closed: day.closed }">
                            {{
                                day.closed
                                    ? 'Closed'
                                    : formatHours(day)
                            }}
                        </span>

                        <span v-if="day.note" class="day-tile__note">
                            {{ day.note }}
                        </span>
                    </button>
                </div>

                <!-- DAY WORKSPACE -->

                <article v-else-if="expandedDay" :key="expandedDay.day"
                    class="admin-card hours-card hours-card--workspace">

                    <div class="day-workspace-header">
                        <div>
                            <span class="day-workspace-eyebrow">
                                Store Hours
                            </span>

                            <h2>{{ expandedDay.name }}</h2>
                        </div>

                        <button type="button" class="day-collapse-button" @click="collapseDayWorkspace">
                            Collapse Day
                        </button>
                    </div>

                    <label class="closed-control">
                        <input v-model="expandedDay.closed" type="checkbox" @change="handleClosedChange(expandedDay)">

                        Closed
                    </label>

                    <div v-if="!expandedDay.closed" class="time-fields">
                        <label>
                            Opens

                            <input v-model="expandedDay.openTime" type="time" required>
                        </label>

                        <label>
                            Closes

                            <input v-model="expandedDay.closeTime" type="time" required>
                        </label>
                    </div>

                    <label class="note-field">
                        Note

                        <input v-model="expandedDay.note" type="text" maxlength="255" placeholder="Optional">
                    </label>

                    <p v-if="dayErrors[expandedDay.day]" class="day-error" role="alert">
                        {{ dayErrors[expandedDay.day] }}
                    </p>

                    <div class="save-row">
                        <button type="button" :disabled="savingDay !== null" @click="saveDay(expandedDay)">
                            {{
                                savingDay === expandedDay.day
                                    ? 'Saving...'
                                    : `Save ${expandedDay.name}`
                            }}
                        </button>

                        <span v-if="savedDay === expandedDay.day" class="save-success" role="status">
                            ✓ Saved
                        </span>
                    </div>

                </article>

            </Transition>

        </section>

    </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useHoursStore } from '@/stores/hoursStore'

const editingHours = ref([])
const savingDay = ref(null)
const savedDay = ref(null)
const dayErrors = ref({})

const expandedDayId = ref(null)

const expandedDay = computed(() =>
    editingHours.value.find(
        day => day.day === expandedDayId.value
    ) ?? null
)

const DEFAULT_OPEN_TIME = '15:00'
const DEFAULT_CLOSE_TIME = '21:00'

const hoursStore = useHoursStore()

onMounted(async () => {
    await hoursStore.loadHours()

    editingHours.value = hoursStore.hours.map(day => ({
        ...day
    }))
})

function openDayWorkspace(day) {
    expandedDayId.value = day.day
    savedDay.value = null

    hoursStore.clearError()
}

function collapseDayWorkspace() {
    expandedDayId.value = null
    savedDay.value = null

    hoursStore.clearError()
}

function formatTime(time) {
    if (!time) {
        return ''
    }

    const [hours, minutes] = time
        .split(':')
        .map(Number)

    const suffix = hours >= 12 ? 'PM' : 'AM'
    const displayHour = hours % 12 || 12

    return minutes === 0
        ? `${displayHour} ${suffix}`
        : `${displayHour}:${String(minutes).padStart(2, '0')} ${suffix}`
}

function handleClosedChange(day) {
    if (!day.closed) {
        day.openTime ||= DEFAULT_OPEN_TIME
        day.closeTime ||= DEFAULT_CLOSE_TIME
    }
}

function formatHours(day) {
    return `${formatTime(day.openTime)} - ${formatTime(day.closeTime)}`
}

async function saveDay(day) {
    savingDay.value = day.day
    savedDay.value = null
    dayErrors.value[day.day] = ""
    hoursStore.clearError()
    if (!day.closed) {
        if (!day.openTime || !day.closeTime) {
            dayErrors.value[day.day] =
                `${day.name} needs both an opening and closing time. Times were reset to the default.`

            day.openTime = DEFAULT_OPEN_TIME
            day.closeTime = DEFAULT_CLOSE_TIME

            savingDay.value = null
            return
        }

        if (day.closeTime <= day.openTime) {
            dayErrors.value[day.day] =
                "Closing time must be after opening time. Times were reset to the default."

            day.openTime = DEFAULT_OPEN_TIME
            day.closeTime = DEFAULT_CLOSE_TIME

            savingDay.value = null
            return
        }

    }

    try {
        await hoursStore.updateHours(day)

        savedDay.value = day.day

        setTimeout(() => {
            if (savedDay.value === day.day) {
                savedDay.value = null
            }
        }, 3000)
    } catch (error) {
        dayErrors.value[day.day] =
            error.message || `Unable to save ${day.name}.`

        hoursStore.clearError()

    } finally {
        savingDay.value = null
    }
}

</script>

<style scoped>
.day-error {
    margin: 0;

    color: var(--status-closed, #b3261e);

    font-size: 0.9rem;
    font-weight: 700;
}

.hours-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit,
            minmax(150px, 1fr));
    gap: 0.75rem;
}

.day-tile {
    min-height: 105px;

    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 0.35rem;

    text-align: center;
}

.day-tile__name {
    font-size: 1.05rem;
}

.day-tile__hours {
    font-size: 0.85rem;
    opacity: 0.8;
}

.day-tile__hours.closed {
    opacity: 0.55;
}

.day-tile__note {
    max-width: 100%;

    font-size: 0.75rem;
    font-style: italic;
    opacity: 0.65;

    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.hours-card--workspace {
    width: 100%;
    box-sizing: border-box;
}

.day-workspace-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 1rem;

    padding-bottom: 1rem;

    border-bottom: 1px solid rgba(255, 255, 255, 0.12);
}

.day-workspace-header h2 {
    margin: 0.2rem 0 0;
}

.day-workspace-eyebrow {
    font-size: 0.75rem;
    text-transform: uppercase;
    letter-spacing: 0.08em;
    opacity: 0.65;
}

.day-collapse-button {
    flex-shrink: 0;
}

.hours-card {
    display: grid;
    gap: 0.75rem;
}

.hours-card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 1rem;
}

.hours-card-header h2 {
    margin: 0;
}

.hours-card p {
    margin: 0;
}

.hours-note {
    font-style: italic;
    opacity: 0.8;
}

.closed-control {
    display: flex;
    align-items: center;
    gap: 0.5rem;

    font-weight: 700;
}

.closed-control input {
    width: auto;
}

.time-fields {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 1rem;
}

.time-fields label,
.note-field {
    display: grid;
    gap: 0.4rem;

    font-weight: 700;
}

.time-fields input,
.note-field input {
    width: 100%;
}

.hours-card button {
    justify-self: start;
}

.save-row {
    display: flex;
    align-items: center;
    gap: 0.75rem;
}

.save-success {
    font-weight: 700;
    color: var(--status-open, #4caf50);
}

/* ==========================================================
   DAY EXPAND / COLLAPSE
   ========================================================== */

.day-expand-enter-active,
.day-expand-leave-active {
    transition:
        opacity 120ms ease,
        transform 120ms ease;
}

.day-expand-enter-from,
.day-expand-leave-to {
    opacity: 0;
    transform: scale(0.985) translateY(4px);
}

.day-expand-enter-to,
.day-expand-leave-from {
    opacity: 1;
    transform: scale(1) translateY(0);
}

@media (max-width: 500px) {
    .hours-grid {
        grid-template-columns: repeat(2,
                minmax(0, 1fr));
        gap: 0.55rem;
    }

    .day-tile {
        min-height: 88px;
        padding: 0.7rem 0.5rem;
    }

    .day-tile__name {
        font-size: 0.9rem;
    }

    .day-workspace-header {
        flex-direction: column;
        align-items: stretch;
    }

    .day-collapse-button {
        align-self: flex-start;
        width: auto;
        padding: 0.45rem 0.65rem;
        font-size: 0.78rem;
    }

    .time-fields {
        grid-template-columns: 1fr;
    }
}
</style>