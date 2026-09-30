<template>
  <section
    v-if="announcementStore.bannerAnnouncements.length"
    class="announcement-banner-group"
    aria-label="Current announcements"
  >
    <article
      v-for="announcement in announcementStore.bannerAnnouncements"
      :key="announcement.id"
      class="site-announcement"
      :class="`is-${announcement.type}`"
    >
      <strong>{{ announcement.title }}:</strong>
      {{ announcement.message }}
    </article>
  </section>
</template>

<script setup>
import { useAnnouncementStore } from '@/stores/announcementStore'

const announcementStore = useAnnouncementStore()
</script>

<style scoped>
.announcement-banner-group {
  width: 100%;
}

.site-announcement {
  padding: 0.85rem 1rem;
  text-align: center;

  background-color: var(--announcement-warning);
  color: var(--text-primary);

  font-size: 0.95rem;
  line-height: 1.4;
}

.site-announcement strong {
  margin-right: 0.25rem;
}
</style>