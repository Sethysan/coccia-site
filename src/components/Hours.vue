<template>

  <!-- =======================================================
       HOURS HEADER

       Loop through each day in the restaurant's weekly schedule
       and display its current status.

       Each card asks the composable:
       - Should I be highlighted?
       - Am I currently open?
       - Which CSS class should I receive?

       The component itself contains almost no business logic.
       ======================================================= -->

  <header class="header">

    <div
      v-for="day in hours"
      :key="day.day"
      class="day-card"
      :class="getDayClass(day)"
    >

      <!-- Name of the day -->
      <strong>{{ day.name }}</strong>

      <!--
        Display "OPEN NOW" only when the composable determines
        that this is today's card AND the restaurant is currently open.
      -->
      <span
        v-if="isOpenNow(day)"
        class="open-now"
      >
        OPEN NOW
      </span>

      <!-- Display the restaurant hours for this day -->
      <span>{{ day.hours }}</span>

    </div>

  </header>

</template>

<script setup>

/* ==========================================================
   IMPORTS

   hours
     Static restaurant schedule stored in /data.

   useRestaurantHours()
     Contains all business logic for determining today's
     status, open/closed state, and CSS classes.

   This keeps this component focused on DISPLAY ONLY.
   ========================================================== */

import { hours } from '@/data/hours'
import { useRestaurantHours } from '@/composables/useRestaurantHours'

/*
  Extract only the helper functions this component needs.

  The composable handles all of the decision making.
  The component simply asks questions like:

      isOpenNow(day)

      getDayClass(day)

  without knowing HOW those answers are calculated.
*/
const { isOpenNow, getDayClass } = useRestaurantHours()

</script>

<style scoped>

/* ==========================================================
   LAYOUT
   ========================================================== */

/* Container holding all seven day cards */

.header {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-around;
  align-items: center;
  padding: .5rem 1rem;
}

/* Individual day card */

.day-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  border-style: double;
  padding: 5px;
  background-color: rgba(219, 233, 245, 0.788);
  z-index: 1001;
}



/* ==========================================================
   OPEN NOW INDICATOR
   ========================================================== */

/*
  The blinking indicator draws attention to today's status.

  Eventually this will likely become part of the larger
  restaurant status system (Open Now, Opening Soon,
  Closing Soon, Closed, etc.)
*/

.open-now {
  color: limegreen;
  font-weight: bold;
  animation: blink 1s infinite;
}


/* ==========================================================
   ANIMATIONS
   ========================================================== */

/*
  Fade the text instead of making it disappear completely.
  This tends to be easier on the eyes while still attracting
  attention.
*/

@keyframes blink {

  0% {
    opacity: 1;
  }

  50% {
    opacity: .25;
  }

  100% {
    opacity: 1;
  }

}

</style>