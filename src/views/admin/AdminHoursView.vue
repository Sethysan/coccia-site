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

        <section v-if="!hoursStore.loading" class="hours-grid">
            <article v-for="day in editingHours" :key="day.day" class="admin-card hours-card">
                <div class="hours-card-header">
                    <h2>{{ day.name }}</h2>

                    <label class="closed-control">
                        <input v-model="day.closed" type="checkbox" @change="handleClosedChange(day)">

                        Closed
                    </label>
                </div>

                <div v-if="!day.closed" class="time-fields">
                    <label>
                        Opens

                        <input v-model="day.openTime" type="time" required>
                    </label>

                    <label>
                        Closes

                        <input v-model="day.closeTime" type="time" required>
                    </label>
                </div>

                <label class="note-field">
                    Note

                    <input v-model="day.note" type="text" maxlength="255" placeholder="Optional">
                </label>

                <p v-if="dayErrors[day.day]" class="day-error" role="alert">
                    {{ dayErrors[day.day] }}
                </p>

                <div class="save-row">
                    <button type="button" :disabled="savingDay !== null" @click="saveDay(day)">
                        {{
                            savingDay === day.day
                                ? 'Saving...'
                                : `Save ${day.name}`
                        }}
                    </button>

                    <span v-if="savedDay === day.day" class="save-success" role="status">
                        ✓ Saved
                    </span>
                </div>
            </article>
        </section>

    </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useHoursStore } from '@/stores/hoursStore'

const editingHours = ref([])
const savingDay = ref(null)
const savedDay = ref(null)
const dayErrors = ref({})
const DEFAULT_OPEN_TIME = '15:00'
const DEFAULT_CLOSE_TIME = '21:00'

const hoursStore = useHoursStore()

onMounted(async () => {
    await hoursStore.loadHours()

    editingHours.value = hoursStore.hours.map(day => ({
        ...day
    }))
})

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
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 1rem;
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

@media (max-width: 500px) {
    .time-fields {
        grid-template-columns: 1fr;
    }
}

@media (max-width: 700px) {
    .hours-grid {
        grid-template-columns: 1fr;
    }
}
</style>