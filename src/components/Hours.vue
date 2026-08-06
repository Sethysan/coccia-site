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
        {{ day.hours }}
      </span>
      <span v-if="day.note" class="hours-note">
        {{ day.note }}
      </span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

import { hours } from '@/data/hours'
import { useTimeStore } from '@/stores/timeStore'
import { useRestaurantHours } from '@/composables/useRestaurantHours'


// -----------------------------------------------------------------------------
// Component options
// -----------------------------------------------------------------------------

const props = defineProps({
  showAll: {
    type: Boolean,
    default: false
  }
})


// -----------------------------------------------------------------------------
// Restaurant helpers
// -----------------------------------------------------------------------------

const {
  restaurantStatus,
  activeClosure,
  isScheduledClosure,
  isToday,
  getDayClass
} = useRestaurantHours()

const timeStore = useTimeStore()


// -----------------------------------------------------------------------------
// Put today first, followed by the remaining days in calendar order
//
// Example when today is Friday:
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
// Choose the compact or complete schedule
// -----------------------------------------------------------------------------

const displayedHours = computed(() => {
  if (props.showAll) {
    return orderedHours.value
  }

  return orderedHours.value.slice(0, 1)
})
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


/* Weekly dropdown list */

.hours-display.expanded {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
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
   OPEN NOW

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

.current-status.is-temporarily-closed {
  color: var(--status-closed);
}

.current-status.is-closing-soon {
  color: var(--status-closing-soon);
  animation: status-pulse 1.5s ease-in-out infinite;
}

.current-status.is-closed {
  color: var(--status-closed);
}

/* ==========================================================
   ANIMATION
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
