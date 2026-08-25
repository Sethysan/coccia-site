<template>
  <SiteHeader />

    <SiteAnnouncementBanner />

  <main class="content">
    <LoadingOverlay :visible="loading.visible" :frame="loading.frame" />
    <RouterView />
  </main>
  <SiteFooter />
</template>

<script setup>
import { onMounted } from 'vue'
import { RouterView } from 'vue-router'
import SiteHeader from './components/SiteHeader.vue'
import SiteFooter from './components/SiteFooter.vue'

import { useTimeStore } from '@/stores/timeStore'

import LoadingOverlay from "@/components/LoadingOverlay.vue"
import { useLoadingStore } from "@/stores/loadingStore"

const loading = useLoadingStore()
import SiteAnnouncementBanner from './components/SiteAnnouncementBanner.vue'
import { useAnnouncementStore } from '@/stores/announcementStore'

// -----------------------------------------------------------------------------
// Start the site clock
// -----------------------------------------------------------------------------

const timeStore = useTimeStore()
const announcementStore = useAnnouncementStore()

onMounted(() => {
  timeStore.startClock()
  announcementStore.loadAnnouncements()
})
</script>

<style scoped>

.content {
  min-height: 100vh;
  margin: 0;

  background-image:
    linear-gradient(rgba(0, 0, 0, 0.58), rgba(0, 0, 0, 0.62)),
    url('./assets/hero-pizza.jpg');

  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
  background-attachment: fixed;
}

.app-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.site-main {
  flex: 1;
}
</style>