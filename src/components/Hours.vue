<template>
  <header class="header">
      <div v-for="day in hours":key="day.day" class="day-card" :class="getDayClass(day)"> 
        <strong>{{ day.name }} </strong>
        <span v-if="isOpenNow(day)"class="open-now"  >
          OPEN NOW
        </span>
        <span>  {{ day.hours }}</span>
      </div>
  </header>
</template>

<script setup>
import { hours } from '@/data/hours'
import dayjs from 'dayjs'
const currentDay = dayjs().day()      // 0-6
const currentHour = dayjs().hour()    // 0-23
const currentMinute = dayjs().minute()  //0-59


function isOpenNow(day) {  //Are we currently open? for display purposes
  if (day.closed) return false
  if (day.day !== currentDay) return false

  return currentHour >= day.open && currentHour < day.close
}

function getDayClass(day) {  //defining how to classify each day for display purposes

  // Not today
  if (day.day !== currentDay) {
    return "normal"
  }

  // Today, but closed all day
  if (day.closed) {
    return "closed"
  }

  // Today, currently open
  if (currentHour >= day.open && currentHour < day.close) {
    return "open"
  }

  // Today, before opening or after closing
  return "closed"
}
</script>

<style scoped>

.header { 
    display: flex;
    justify-content: space-around;
    align-items: center;
    padding: .5rem 1rem;
}

.day-card {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.open-now {
  color: limegreen;
  font-weight: bold;
  animation: blink 1s infinite;
}

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