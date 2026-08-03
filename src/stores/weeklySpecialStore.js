import { defineStore } from "pinia"
import { computed, ref } from "vue"
import { loadCurrentWeeklySpecial } from "@/services/weeklySpecialService"

export const useWeeklySpecialStore = defineStore("weeklySpecial", () => {
  const special = ref(null)
  const source = ref(null)
  const loading = ref(false)
  const error = ref(null)
  const hasLoaded = ref(false)

  const isVisible = computed(() => {
    return Boolean(
      special.value &&
        special.value.active &&
        special.value.title &&
        special.value.description
    )
  })

  const load = async ({ force = false } = {}) => {
    if (hasLoaded.value && !force) {
      return
    }

    loading.value = true
    error.value = null

    try {
      const result = await loadCurrentWeeklySpecial()

      special.value = result.special
      source.value = result.source
      error.value = result.error
      hasLoaded.value = true
    } catch (loadError) {
      error.value = loadError
      special.value = null
    } finally {
      loading.value = false
    }
  }

  const refresh = () => load({ force: true })

  return {
    special,
    source,
    loading,
    error,
    hasLoaded,
    isVisible,
    load,
    refresh
  }
})
// todo: useWeeklySpecialStore.refresh
