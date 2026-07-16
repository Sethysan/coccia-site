<template>
  <SiteHeader />
  
  <main class="content">
    <RouterView />
  </main>
</template>

<script setup>
import { onMounted } from 'vue'
import { RouterView } from 'vue-router'

import SiteHeader from './components/SiteHeader.vue'

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
    linear-gradient(rgba(0, 0, 0, 0.58), rgba(0, 0, 0, 0.62)),
    url('./assets/hero-pizza.jpg');

  background-size: cover;
  background-color: #111;
  background-position: center;
  background-repeat: no-repeat;
}

</style>