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
</style>