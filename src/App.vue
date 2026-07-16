<template>
  <SiteHeader />
  
  <section
  v-for="announcement in announcements"
  :key="announcement.id"
  class="site-announcement"
>
  <strong>{{ announcement.title }}:</strong>
  {{ announcement.message }}
</section>

  <main class="content">
    <RouterView />
  </main>
</template>

<script setup>
import { onMounted } from 'vue'
import { RouterView } from 'vue-router'
import { announcements } from '@/data/announcements'

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
.site-announcement {
  padding: 0.85rem 1rem;
  text-align: center;

  background-color: #7a1f1f;
  color: #fffaf1;

  font-size: 0.95rem;
  line-height: 1.4;
}

.site-announcement strong {
  margin-right: 0.25rem;
}

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