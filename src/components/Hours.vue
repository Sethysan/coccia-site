<template>
  <div
    class="hours-display"
    :class="{ expanded: showAll }"
  >
    <div
      v-for="day in displayedHours"
      :key="day.day"
      class="day-row"
      :class="getDayClass(day)"
    >
      <strong class="day-name">
        {{ showAll ? `${day.name}:` : day.name }}
      </strong>

      <span
        v-if="!showAll && isOpenNow(day)"
        class="open-now"
      >
        OPEN NOW
      </span>

      <span class="day-hours">
        {{ day.hours }}
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
  isOpenNow,
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


/* ==========================================================
   OPEN NOW
   ========================================================== */

.open-now {
  color: limegreen;
  font-weight: bold;
  animation: blink 1s infinite;
}


/* ==========================================================
   ANIMATION
   ========================================================== */

@keyframes blink {
  0% {
    opacity: 1;
  }

  50% {
    opacity: 0.25;
  }

  100% {
    opacity: 1;
  }
}
</style>