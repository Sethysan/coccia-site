<template>
  <section
    v-if="announcementStore.newsAnnouncements.length"
    class="news-card"
    aria-labelledby="news-title"
  >
    <h2 id="news-title">
      Latest News
    </h2>

    <article
      v-for="announcement in announcementStore.newsAnnouncements"
      :key="announcement.id"
      class="news-item"
    >
      <h3>
        {{ announcement.title }}
      </h3>

      <img
        v-if="announcement.imageUrl"
        class="news-flyer"
        :src="announcement.imageUrl"
        :alt="announcement.imageAlt || announcement.title"
      />

      <p>
        {{ announcement.message }}
      </p>
    </article>
  </section>
</template>

<script setup>
import { useAnnouncementStore } from '@/stores/announcementStore'

const announcementStore = useAnnouncementStore()
</script>

<style scoped>
.news-card {
  width: min(100%, 560px);
  margin: 0 auto 6rem;
  padding: 1.75rem;

  background-color: var(--background-dark-trans);

  border: 1px solid var(--bronze-color);
  border-radius: 0.5rem;

  text-align: left;
}

.news-card > h2 {
  margin: 0 0 1.5rem;
  text-align: center;
}

.news-item + .news-item {
  margin-top: 1.75rem;
  padding-top: 1.75rem;

  border-top: 1px solid rgba(255, 255, 255, 0.15);
}

.news-item h3 {
  margin: 0 0 0.6rem;

  color: var(--bronze-bold);
}

.news-item p {
  margin: 0;

  line-height: 1.65;
}

.news-item p + p {
  margin-top: 0.9rem;
}

.news-flyer {
  display: block;
  width: 100%;
  max-width: 420px;
  margin: 1rem auto 1.25rem;
  border-radius: 10px;
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.25);
}

@media (max-width: 600px) {
  .news-card {
    margin-bottom: 4rem;
    padding: 1.25rem;

    text-align: left;
  }
}
</style>