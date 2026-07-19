// Import Vue's computed() so values automatically update whenever
// the underlying time in the store changes.
import { computed } from 'vue'
import { useTimeStore } from '@/stores/timeStore'
import { hours } from '@/data/hours'

// =======================================================
// Restaurant Hours Composable
//
// Purpose:
// Contains ALL business logic related to determining whether
// the restaurant is open, closed, or what today's status is.
//
// This keeps the Vue component focused ONLY on displaying data.
// =======================================================

export function useRestaurantHours() {

  // Get access to the global time store.
  const timeStore = useTimeStore()


  // -------------------------------------------------------
  // CURRENT DAY
  //
  // Returns today's day number.
  //
  // JavaScript / DayJS numbering:
  //
  // 0 = Sunday
  // 1 = Monday
  // 2 = Tuesday
  // ...
  // 6 = Saturday
  //
  // This value automatically updates whenever the store updates.
  // -------------------------------------------------------

  const currentDay = computed(() => timeStore.currentTime.day())


  // -------------------------------------------------------
  // CURRENT TIME
  //
  // Converts the current time into "minutes since midnight."
  //
  // Why?
  //
  // Comparing minutes is much easier than comparing separate
  // hours and minutes.
  //
  // Example:
  //
  // 3:30 PM
  //
  // becomes
  //
  // 15 * 60 + 30 = 930 minutes
  //
  // Now opening and closing comparisons become simple numbers.
  // -------------------------------------------------------

  const currentMinutes = computed(() => {
    return timeStore.currentTime.hour() * 60 +
      timeStore.currentTime.minute()
  })

  // -------------------------------------------------------
  // TODAY'S HOURS
  //
  // Returns the hours object for the current day.
  //
  // Example:
  //
  // {
  //   day: 4,
  //   name: "Thursday",
  //   hours: "3 PM - 9 PM",
  //   ...
  // }
  //
  // Components can use this instead of searching the
  // hours array themselves.
  // -------------------------------------------------------

  const todayHours = computed(() => {
    return hours.find(day => day.day === currentDay.value)
  })

  // -------------------------------------------------------
  // Is this card representing TODAY?
  //
  // Returns true if the day from the hours array matches
  // today's day.
  //
  // Example:
  //
  // Wednesday card
  // Wednesday today
  //
  // returns true
  // -------------------------------------------------------

  function isToday(day) {
    return day.day === currentDay.value
  }


  // -------------------------------------------------------
  // Is the restaurant open RIGHT NOW?
  //
  // Returns true only when:
  //
  // 1. Today is an operating day.
  // 2. The current time falls between opening and closing.
  //
  // Example:
  //
  // Wednesday
  // Open: 3 PM
  // Close: 9 PM
  // Current: 5:15 PM
  //
  // returns true
  // -------------------------------------------------------

  function isOpenNow(day) {

    // Restaurant closed all day.
    if (day.closed) return false

    // Ignore every day except today.
    if (!isToday(day)) return false

    // Convert opening and closing times into minutes.
    const openMinutes = day.open * 60
    const closeMinutes = day.close * 60

    // Is the current time between open and close?
    return currentMinutes.value >= openMinutes &&
      currentMinutes.value < closeMinutes
  }


  // -------------------------------------------------------
  // Determines which CSS class should be applied.
  //
  // The Vue component doesn't need to know WHY.
  // It simply asks this function.
  //
  // Returns:
  //
  // "normal"
  // "closed"
  // "open"
  // -------------------------------------------------------

  function getDayClass(day) {

    // Not today's card.
    if (!isToday(day)) return 'normal'

    // Today, but restaurant is closed all day.
    if (day.closed) return 'closed'

    // Today and currently open.
    if (isOpenNow(day)) return 'open'

    // Today, but before opening or after closing.
    return 'closed'
  }

  // -------------------------------------------------------
  // FORMAT HOUR
  //
  // Converts a 24-hour value into a user-friendly time.
  //
  // Example:
  //
  // 15
  //
  // becomes
  //
  // 3 PM
  //
  // This helper keeps all displayed times consistent
  // throughout the website.
  // -------------------------------------------------------

  function formatHour(hour) {
    const suffix = hour >= 12 ? 'PM' : 'AM'
    const displayHour = hour % 12 || 12

    return `${displayHour} ${suffix}`
  }

  // -------------------------------------------------------
  // CURRENT RESTAURANT STATUS
  //
  // Determines the restaurant's current operating state.
  //
  // This is the primary source of truth used throughout
  // the website.
  //
  // Possible states:
  //
  // open
  // opening-soon
  // opening-later
  // closing-soon
  // closed
  //
  // Returns:
  //
  // {
  //   state,
  //   label,
  //   message
  // }
  //
  // Components should use this instead of duplicating
  // business logic.
  // -------------------------------------------------------
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

    const openMinutes = day.open * 60
    const closeMinutes = day.close * 60
    const openingSoonMinutes = openMinutes - 90
    const closingSoonMinutes = closeMinutes - 90

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
            ? `Sunday carryout begins at ${formatHour(day.open)}.`
            : `We open at ${formatHour(day.open)}.`
      }
    }

    if (isOpenNow(day) && closingSoon) {
      return {
        state: 'closing-soon',
        label: 'Closing Soon',
        message:
          day.day === 0
            ? `Sunday carryout ends at ${formatHour(day.close)}.`
            : `We close at ${formatHour(day.close)}.`
      }
    }

    if (isOpenNow(day)) {
      return {
        state: 'open',
        label: 'Open Now',
        message:
          day.day === 0
            ? `Sunday carryout is available until ${formatHour(day.close)}.`
            : `Dine in or order carryout until ${formatHour(day.close)}.`
      }
    }

    if (currentMinutes.value < openMinutes) {
      return {
        state: 'opening-later',
        label: 'Opens Today',
        message:
          day.day === 0
            ? `Sunday carryout begins at ${formatHour(day.open)}.`
            : `We open at ${formatHour(day.open)}.`
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
  }
  )

   // -------------------------------------------------------
  // COMPACT HEADER STATUS
  //
  // Returns a short version of the restaurant status
  // for use in compact UI elements such as the mobile
  // header hours button.
  //
  // Examples:
  //
  // Open Now
  // Opening Soon
  // Closing Soon
  // Opens 3 PM - 9 PM
  // Closed Today
  //
  // This intentionally omits the longer explanatory
  // messages used elsewhere on the website.
  // -------------------------------------------------------

  const compactHoursMessage = computed(() => {
    switch (restaurantStatus.value.state) {
      case "open":
        return "Open Now"

      case "opening-soon":
        return "Opening Soon"

      case "closing-soon":
        return "Closing Soon"

      case "opening-later":
        return `Opens at ${formatHour(todayHours.value.open)}`

      case "closed":
        return "Closed Today"

      default:
        return "Restaurant Hours"
    }
  })

   // -------------------------------------------------------
  // PUBLIC API
  //
  // Export everything components need.
  //
  // Components should rely on these helpers rather than
  // implementing restaurant-hour logic themselves.
  // -------------------------------------------------------
  
  return {
    todayHours,
    restaurantStatus,
    isToday,
    isOpenNow,
    getDayClass,
    compactHoursMessage
  }
}