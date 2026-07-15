<template>
  <SiteHeader />
  <ScrollworkDivider />
  <main class="content">
    <RouterView />
  </main>
</template>

<script setup>
import { onMounted } from 'vue'
import { RouterView } from 'vue-router'

import SiteHeader from './components/SiteHeader.vue'
import ScrollworkDivider from './components/ScrollworkDivider.vue'
import { useTimeStore } from '@/stores/timeStore'

// -----------------------------------------------------------------------------
// Start the site clock
// -----------------------------------------------------------------------------

const timeStore = useTimeStore()

onMounted(() => {
  timeStore.startClock()
})
</script>

<style scoped>
.content {
  padding-top: 2rem;
  min-height: 75vh;

  background-image:
    linear-gradient(
      rgba(0, 0, 0, 0.55),
      rgba(0, 0, 0, 0.55)
    ),
    url('./assets/hero-pizza.jpg');

  background-size: cover;
  background-color: #111;
  background-position: center;
  background-repeat: no-repeat;
}

.scrollwork-divider {
  position: relative;
  z-index: 2000;

  width: 100%;
  height: 42px;

  margin-top: -21px;
  margin-bottom: -21px;

  overflow: visible;
  pointer-events: none;
}
</style>