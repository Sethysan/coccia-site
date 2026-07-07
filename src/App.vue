<template>
  <SiteHeader />
  <Hours />
  
  <main class="content">
    <RouterView />
  </main>
</template>

<script setup>
import Hours from './components/Hours.vue'
import SiteHeader from './components/SiteHeader.vue'
import { RouterView } from 'vue-router'
import { useTimeStore } from '@/stores/timeStore'
import { onMounted } from 'vue'

const timeStore = useTimeStore()

onMounted(() => {
  timeStore.startClock()
})
</script>

<style scoped >
.content {
  padding-top: 2rem;
  min-height: 75vh;
  background-image: url('./assets/hero-pizza.jpg');
  background-size: cover;
  background-color: #111;
  background-position: center ;
  background-repeat: no-repeat;
}
div {
  font-family: system-ui, 'Segoe UI', Roboto, sans-serif;

  
}


</style>