// Import Vue's computed() so values automatically update whenever
// the underlying time in the store changes.
import { computed } from 'vue'
import { useTimeStore } from '@/stores/timeStore'


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
  // Export the helper functions.
  //
  // Any Vue component that imports this composable can use
  // these functions.
  // -------------------------------------------------------

  return {
    isToday,
    isOpenNow,
    getDayClass
  }
}