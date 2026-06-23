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

<style scoped>
.content {
  padding-top: 90px;
}

div {
  font-family: system-ui, 'Segoe UI', Roboto, sans-serif;
}

</style>