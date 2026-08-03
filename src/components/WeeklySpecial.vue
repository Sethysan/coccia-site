<script setup>
import { onMounted } from "vue"
import { storeToRefs } from "pinia"
import { useWeeklySpecialStore } from "@/stores/weeklySpecialStore"

const weeklySpecialStore = useWeeklySpecialStore()

const {
  special,
  loading,
  error,
  isVisible
} = storeToRefs(weeklySpecialStore)

onMounted(() => {
  weeklySpecialStore.load()
})
/* todo: after weekly special section design add this component to the page.*/
</script>
<template>
  <section
    v-if="loading || isVisible"
    class="weekly-special"
    aria-labelledby="weekly-special-title"
  >
    <p v-if="loading" class="weekly-special__status">
      Loading this week's special…
    </p>

    <article v-else-if="isVisible">
      <div class="weekly-special__content">
        <p class="weekly-special__eyebrow">
          This Week
        </p>

        <h2 id="weekly-special-title">
          {{ special.title }}
        </h2>

        <p>
          {{ special.description }}
        </p>

        <p
          v-if="special.price !== null"
          class="weekly-special__price"
        >
          {{ special.price }}
        </p>
      </div>

      <img
        v-if="special.imageUrl"
        :src="special.imageUrl"
        :alt="special.imageAlt"
        class="weekly-special__image"
      />
    </article>
  </section>

  <!--
    WHITE NOTE:
    Public visitors should not see technical API errors.

    TODO:
    Add development-only error information if useful.
  -->
  <div v-else-if="error" hidden>
    The weekly special could not be loaded.
  </div>
</template>
