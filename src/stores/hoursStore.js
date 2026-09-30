import { defineStore } from "pinia"
import { ref } from "vue"

import {
  getRestaurantHours,
  updateRestaurantHours
} from "@/api/hoursApi"

import { hoursFallback } from "@/data/fallback/hoursFallback"


export const useHoursStore = defineStore("hours", () => {

  const hours = ref([...hoursFallback])

  const loading = ref(false)
  const error = ref(null)
  const usingFallback = ref(true)


  // ---------------------------------------------------------------------------
  // Normalize API data
  //
  // The backend uses dayOfWeek and Java LocalTime values such as "15:00:00".
  // The frontend hours model uses day and "HH:mm" times.
  //
  // Keeping normalization here means components and composables always receive
  // the same shape whether data came from PostgreSQL or the fallback file.
  // ---------------------------------------------------------------------------

  function normalizeHoursDay(day) {
    return {
      day: day.dayOfWeek,
      name: day.dayName,
      closed: day.closed,
      openTime: normalizeTime(day.openTime),
      closeTime: normalizeTime(day.closeTime),
      note: day.note ?? null
    }
  }

  function normalizeTime(time) {
    if (!time) {
      return null
    }

    return time.slice(0, 5)
  }


  // ---------------------------------------------------------------------------
  // Public hours
  //
  // Database hours are preferred. If the API cannot be reached, the bundled
  // fallback schedule keeps public restaurant information available.
  // ---------------------------------------------------------------------------

  async function loadHours() {
    loading.value = true
    error.value = null

    try {
      const response = await getRestaurantHours()

      hours.value = response.map(normalizeHoursDay)
      usingFallback.value = false

    } catch (err) {
      console.error(
        "Unable to load restaurant hours. Using fallback schedule.",
        err
      )

      error.value = err
      useFallbackHours()

    } finally {
      loading.value = false
    }
  }


  // ---------------------------------------------------------------------------
  // Admin update
  // ---------------------------------------------------------------------------

  async function updateHours(day) {
    error.value = null

    try {
      const request = {
        dayOfWeek: day.day,
        closed: day.closed,
        openTime: day.closed ? null : day.openTime,
        closeTime: day.closed ? null : day.closeTime,
        note: day.note || null
      }

      const response = await updateRestaurantHours(
        day.day,
        request
      )

      const updatedDay = normalizeHoursDay(response)

      hours.value = hours.value.map(existingDay =>
        existingDay.day === updatedDay.day
          ? updatedDay
          : existingDay
      )

      usingFallback.value = false

      return updatedDay

    } catch (err) {
      error.value = err
      throw err
    }
  }


  // ---------------------------------------------------------------------------
  // Fallback
  // ---------------------------------------------------------------------------

  function useFallbackHours() {
    hours.value = [...hoursFallback]
    usingFallback.value = true
  }

  function clearError() {
    error.value = null
  }


  return {
    hours,
    loading,
    error,
    usingFallback,
    loadHours,
    updateHours,
    useFallbackHours,
    clearError
  }
})