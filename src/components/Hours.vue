<template>
  <div class="hours-display" :class="{ expanded: showAll }">
    <div v-for="day in displayedHours" :key="day.day" class="day-row" :class="getDayClass(day)">
      <strong class="day-name">
        {{ showAll ? `${day.name}:` : day.name }}
      </strong>

      <span v-if="!showAll && isToday(day)" class="current-status" :class="`is-${restaurantStatus.state}`">
        {{ restaurantStatus.label }}
      </span>

      <span class="day-hours">
        {{ formatHours(day) }}
      </span>

      <span v-if="day.note" class="hours-note">
        {{ day.note }}
      </span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

import { useHoursStore } from '@/stores/hoursStore'
import { useTimeStore } from '@/stores/timeStore'
import { useRestaurantHours } from '@/composables/useRestaurantHours'


// -----------------------------------------------------------------------------
// Component options
//
// Compact mode shows only today's hours.
// Expanded mode shows the full seven-day schedule.
// -----------------------------------------------------------------------------

const props = defineProps({
  showAll: {
    type: Boolean,
    default: false
  }
})


// -----------------------------------------------------------------------------
// Stores and restaurant-status helpers
//
// Hours come from hoursStore so this component does not need to know whether
// the schedule came from the API or the static fallback.
// -----------------------------------------------------------------------------

const timeStore = useTimeStore()
const hoursStore = useHoursStore()

const {
  restaurantStatus,
  isToday,
  getDayClass
} = useRestaurantHours()


// -----------------------------------------------------------------------------
// Ordered schedule
//
// Rotates the weekly schedule so today appears first.
//
// Example on Friday:
//
// Friday
// Saturday
// Sunday
// Monday
// Tuesday
// Wednesday
// Thursday
// -----------------------------------------------------------------------------

const orderedHours = computed(() => {
  const currentDayNumber = timeStore.currentTime.day()
  const hours = hoursStore.hours

  const currentDayIndex = hours.findIndex(day => {
    return day.day === currentDayNumber
  })

  if (currentDayIndex === -1) {
    return hours
  }

  return [
    ...hours.slice(currentDayIndex),
    ...hours.slice(0, currentDayIndex)
  ]
})


// -----------------------------------------------------------------------------
// Displayed schedule
//
// Expanded mode returns the full ordered week.
// Compact mode returns only today's row.
// -----------------------------------------------------------------------------

const displayedHours = computed(() => {
  if (props.showAll) {
    return orderedHours.value
  }

  return orderedHours.value.slice(0, 1)
})


// -----------------------------------------------------------------------------
// Display formatting
//
// Stored/API times use 24-hour "HH:mm" strings.
//
// Examples:
// "15:00" -> "3 PM"
// "15:30" -> "3:30 PM"
//
// The human-readable hours string is derived here instead of being stored
// separately so displayed hours cannot drift out of sync with the real times.
// -----------------------------------------------------------------------------

function formatTime(time) {
  if (!time) {
    return ""
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

function formatHours(day) {
  if (day.closed) {
    return "Closed"
  }

  return `${formatTime(day.openTime)} - ${formatTime(day.closeTime)}`
}
</script>

<style scoped>
/* ==========================================================
   HOURS DISPLAY
   ========================================================== */

.hours-display {
  font-family: system-ui, 'Segoe UI', Roboto, sans-serif;
}


/* Compact current-day display */

.hours-display:not(.expanded) .day-row {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}





/* ==========================================================
   INDIVIDUAL DAY
   ========================================================== */

.day-row {
  white-space: nowrap;
}

.hours-display.expanded .day-row {
  display: grid;
  grid-template-columns: 6.5rem auto;
  align-items: baseline;

  padding: 0.1rem 0;
}

.day-name {
  font-weight: 700;
}

.day-hours {
  white-space: nowrap;
}

.hours-note {
  display: block;
  margin-top: 0.15rem;

  font-size: 0.8rem;
  font-style: italic;
  opacity: 0.8;
}


/* ==========================================================
   CURRENT STATUS
   ========================================================== */

.current-status {
  display: inline-block;
  margin: 0.15rem 0;

  font-size: 0.8rem;
  font-weight: 800;
  letter-spacing: 0.05em;
  text-transform: uppercase;
}

.current-status.is-open {
  color: var(--status-open, #4caf50);
}

.current-status.is-opening-soon {
  color: var(--status-opening-soon);
  animation: status-pulse 1.5s ease-in-out infinite;
}

.current-status.is-opening-later {
  color: var(--status-opening-later);
}

.current-status.is-closing-soon {
  color: var(--status-closing-soon);
  animation: status-pulse 1.5s ease-in-out infinite;
}

.current-status.is-closed {
  color: var(--status-closed);
}


/* ==========================================================
   STATUS ANIMATION
   ========================================================== */

@keyframes status-pulse {

  0%,
  100% {
    opacity: 1;
  }

  50% {
    opacity: 0.55;
  }
}


/* ==========================================================
   MOBILE
   ========================================================== */

@media (max-width: 700px) {
  .hours-display.expanded {
    max-height: 40vh;
    overflow-y: auto;
  }

  .hours-display.expanded .day-row {
    display: flex;
    flex-direction: column;
  }
}
</style>