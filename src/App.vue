<template>
  <SiteHeader />
  <main class="content">
    <RouterView />
  </main>
</template>

<script setup>
import SiteHeader from './components/SiteHeader.vue'
import { RouterView } from 'vue-router'
</script>

<style scoped >
.content {
  padding-top: 90px;
  min-height: 75vh;
  background-image: url('./assets/pizza.jpg');
  background-size: contain;
  background-color: #111;
  background-position: center;
  background-repeat: no-repeat;
}
div {
  font-family: system-ui, 'Segoe UI', Roboto, sans-serif;

  
}


</style>