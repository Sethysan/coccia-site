import { defineStore } from "pinia"
import { ref } from "vue"

export const useSiteStore = defineStore("site", () => {
  const initialDataLoaded = ref(false)

  function markInitialDataLoaded() {
    initialDataLoaded.value = true
  }

  return {
    initialDataLoaded,
    markInitialDataLoaded
  }
})