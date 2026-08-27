import { computed } from 'vue'

import { useTimeStore } from '@/stores/timeStore'
import { useHoursStore } from '@/stores/hoursStore'


// ==========================================================
// RESTAURANT HOURS
//
// Central business logic for interpreting the restaurant's
// weekly schedule.
//
// Schedule data comes from hoursStore. The store will
// eventually prefer API/database data and fall back to the
// bundled static schedule when the API is unavailable.
//
// Components should use this composable instead of duplicating
// open/closed calculations.
// ==========================================================

export function useRestaurantHours() {

  const timeStore = useTimeStore()
  const hoursStore = useHoursStore()


  // ----------------------------------------------------------
  // Current day and time
  //
  // Day numbers follow JavaScript / DayJS:
  // 0 = Sunday through 6 = Saturday.
  //
  // Minutes since midnight make schedule comparisons simple.
  // Example: 3:30 PM = 930 minutes.
  // ----------------------------------------------------------

  const currentDay = computed(() =>
    timeStore.currentTime.day()
  )

  const currentMinutes = computed(() => {
    return (
      timeStore.currentTime.hour() * 60 +
      timeStore.currentTime.minute()
    )
  })


  // ----------------------------------------------------------
  // Today's schedule
  //
  // Finds today's entry from the current hoursStore schedule,
  // regardless of whether that schedule came from the API or
  // the fallback file.
  // ----------------------------------------------------------

  const todayHours = computed(() => {
    return hoursStore.hours.find(
      day => day.day === currentDay.value
    )
  })


  // ----------------------------------------------------------
  // Time helpers
  //
  // Schedule times use 24-hour "HH:mm" strings.
  //
  // timeToMinutes:
  // "15:30" -> 930
  //
  // formatTime:
  // "15:00" -> "3 PM"
  // "15:30" -> "3:30 PM"
  // ----------------------------------------------------------

  function timeToMinutes(time) {
    if (!time) {
      return null
    }

    const [hours, minutes] = time
      .split(':')
      .map(Number)

    return hours * 60 + minutes
  }

  function formatTime(time) {
    if (!time) {
      return ""
    }

    const [hours, minutes] = time
      .split(':')
      .map(Number)

    const suffix = hours >= 12 ? 'PM' : 'AM'
    const displayHour = hours % 12 || 12

    if (minutes === 0) {
      return `${displayHour} ${suffix}`
    }

    return `${displayHour}:${String(minutes).padStart(2, '0')} ${suffix}`
  }


  // ----------------------------------------------------------
  // Day-state helpers
  // ----------------------------------------------------------

  function isToday(day) {
    return day.day === currentDay.value
  }

  function isOpenNow(day) {
    if (day.closed) {
      return false
    }

    if (!isToday(day)) {
      return false
    }

    const openMinutes =
      timeToMinutes(day.openTime)

    const closeMinutes =
      timeToMinutes(day.closeTime)

    return (
      currentMinutes.value >= openMinutes &&
      currentMinutes.value < closeMinutes
    )
  }


  // ----------------------------------------------------------
  // Day styling state
  //
  // Used by Hours.vue to highlight today's schedule.
  //
  // Returns:
  // normal
  // open
  // closed
  // ----------------------------------------------------------

  function getDayClass(day) {
    if (!isToday(day)) {
      return 'normal'
    }

    if (day.closed) {
      return 'closed'
    }

    if (isOpenNow(day)) {
      return 'open'
    }

    return 'closed'
  }


  // ----------------------------------------------------------
  // Current restaurant status
  //
  // Produces the public-facing operating state used throughout
  // the site.
  //
  // Possible states:
  // open
  // opening-soon
  // opening-later
  // closing-soon
  // closed
  //
  // NOTE:
  // Reopening messages are still temporarily hard-coded to the
  // current Wednesday schedule. These will be made dynamic once
  // database-controlled hours are fully integrated.
  // ----------------------------------------------------------

  const restaurantStatus = computed(() => {
    const day = todayHours.value

    if (!day) {
      return {
        state: 'closed',
        label: 'Hours Unavailable',
        message: 'Please call us for today’s hours.'
      }
    }

    if (day.closed) {
      return {
        state: 'closed',
        label: 'Closed Today',
        message: 'We’ll reopen Wednesday at 3 PM.'
      }
    }

    const openMinutes =
      timeToMinutes(day.openTime)

    const closeMinutes =
      timeToMinutes(day.closeTime)

    const openingSoonMinutes =
      openMinutes - 90

    const closingSoonMinutes =
      closeMinutes - 90

    const openingSoon =
      currentMinutes.value >= openingSoonMinutes &&
      currentMinutes.value < openMinutes

    const closingSoon =
      currentMinutes.value >= closingSoonMinutes &&
      currentMinutes.value < closeMinutes

    if (openingSoon) {
      return {
        state: 'opening-soon',
        label: 'Opening Soon',
        message:
          day.day === 0
            ? `Sunday carryout begins at ${formatTime(day.openTime)}.`
            : `We open at ${formatTime(day.openTime)}.`
      }
    }

    if (isOpenNow(day) && closingSoon) {
      return {
        state: 'closing-soon',
        label: 'Closing Soon',
        message:
          day.day === 0
            ? `Sunday carryout ends at ${formatTime(day.closeTime)}.`
            : `We close at ${formatTime(day.closeTime)}.`
      }
    }

    if (isOpenNow(day)) {
      return {
        state: 'open',
        label: 'Open Now',
        message:
          day.day === 0
            ? `Sunday carryout is available until ${formatTime(day.closeTime)}.`
            : `Dine in or order carryout until ${formatTime(day.closeTime)}.`
      }
    }

    if (currentMinutes.value < openMinutes) {
      return {
        state: 'opening-later',
        label: 'Opens Today',
        message:
          day.day === 0
            ? `Sunday carryout begins at ${formatTime(day.openTime)}.`
            : `We open at ${formatTime(day.openTime)}.`
      }
    }

    return {
      state: 'closed',
      label: 'Closed for Today',
      message:
        day.day === 0
          ? 'We’ll reopen Wednesday at 3 PM.'
          : 'Thank you for visiting. We hope to see you again soon!'
    }
  })


  // ----------------------------------------------------------
  // Compact header status
  //
  // Short status used where the longer restaurantStatus message
  // would not fit, such as the site's compact hours control.
  // ----------------------------------------------------------

  const compactHoursMessage = computed(() => {
    switch (restaurantStatus.value.state) {
      case 'open':
        return 'Open Now'

      case 'opening-soon':
        return 'Opening Soon'

      case 'closing-soon':
        return 'Closing Soon'

      case 'opening-later':
        return `Opens at ${formatTime(
          todayHours.value.openTime
        )}`

      case 'closed':
        return 'Closed Today'

      default:
        return 'Restaurant Hours'
    }
  })


  // ----------------------------------------------------------
  // Public API
  // ----------------------------------------------------------

  return {
    todayHours,
    restaurantStatus,
    isToday,
    isOpenNow,
    getDayClass,
    compactHoursMessage
  }
}