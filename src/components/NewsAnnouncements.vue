<template>
  <section v-if="announcementStore.newsAnnouncements.length" class="news-card" aria-labelledby="news-title">
    <h2 id="news-title">
      Latest News
    </h2>

    <Swiper :modules="swiperModules" :slides-per-view="1" :space-between="24" :navigation="hasMultipleAnnouncements"
      :pagination="hasMultipleAnnouncements
        ? { clickable: true }
        : false
        " :keyboard="{ enabled: true }" :grab-cursor="hasMultipleAnnouncements" class="news-swiper">
      <SwiperSlide v-for="announcement in announcementStore.newsAnnouncements" :key="announcement.id">
        <article class="news-item">
          <h3>
            {{ announcement.title }}
          </h3>

          <img v-if="announcement.imageUrl" class="news-flyer" :src="announcement.imageUrl" :alt="announcement.imageAlt ||
            announcement.title
            " />

          <div class="news-message">
            <p :class="{
              'is-collapsed':
                isLongAnnouncement(announcement)
            }">
              {{ announcement.message }}
            </p>

            <button v-if="isLongAnnouncement(announcement)" type="button" class="news-read-more"
              @click="openAnnouncement(announcement)">
              Read More
            </button>
          </div>

        </article>
      </SwiperSlide>
    </Swiper>
  </section>

  <Teleport to="body">
    <div v-if="selectedAnnouncement" class="news-reader" role="dialog" aria-modal="true"
      :aria-labelledby="`news-reader-title-${selectedAnnouncement.id}`" @click.self="closeAnnouncement">
      <article class="news-reader-card">
        <button type="button" class="news-reader-close" aria-label="Close announcement" @click="closeAnnouncement">
          ×
        </button>

        <h2 :id="`news-reader-title-${selectedAnnouncement.id}`">
          {{ selectedAnnouncement.title }}
        </h2>

        <img v-if="selectedAnnouncement.imageUrl" class="news-reader-image" :src="selectedAnnouncement.imageUrl" :alt="selectedAnnouncement.imageAlt ||
          selectedAnnouncement.title
          " />

        <p>
          {{ selectedAnnouncement.message }}
        </p>
      </article>
    </div>
  </Teleport>
</template>

<script setup>
import {
  computed,
  onBeforeUnmount,
  ref
} from 'vue'

import { Swiper, SwiperSlide } from 'swiper/vue'
import {
  Keyboard,
  Navigation,
  Pagination
} from 'swiper/modules'

import 'swiper/css'
import 'swiper/css/navigation'
import 'swiper/css/pagination'

import { useAnnouncementStore } from '@/stores/announcementStore'

const announcementStore = useAnnouncementStore()

const swiperModules = [
  Navigation,
  Pagination,
  Keyboard
]

const hasMultipleAnnouncements = computed(
  () =>
    announcementStore.newsAnnouncements.length > 1
)

function isLongAnnouncement(announcement) {
  return announcement.message.length > 300
}

const selectedAnnouncement = ref(null)

function openAnnouncement(announcement) {
  selectedAnnouncement.value = announcement
  document.body.classList.add('news-reader-open')
}

function closeAnnouncement() {
  selectedAnnouncement.value = null
  document.body.classList.remove('news-reader-open')
}

function handleKeydown(event) {
  if (
    event.key === 'Escape' &&
    selectedAnnouncement.value
  ) {
    closeAnnouncement()
  }
}

window.addEventListener('keydown', handleKeydown)

onBeforeUnmount(() => {
  window.removeEventListener(
    'keydown',
    handleKeydown
  )

  document.body.classList.remove(
    'news-reader-open'
  )
})

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

.news-card>h2 {
  margin: 0 0 1.5rem;
  text-align: center;
}

.news-swiper {
  width: 100%;
}

.news-swiper:has(.swiper-pagination) {
  padding-bottom: 2.5rem;
}

.news-swiper :deep(.swiper-button-prev),
.news-swiper :deep(.swiper-button-next) {
  color: var(--bronze-bold);
}

.news-swiper :deep(.swiper-pagination-bullet) {
  background-color: var(--default-color);
}

.news-swiper :deep(.swiper-pagination-bullet-active) {
  background-color: var(--bronze-bold);
  opacity: 1;
}

.news-item h3 {
  margin: 0 0 0.6rem;

  color: var(--bronze-bold);
}

.news-item p {
  margin: 0;

  line-height: 1.65;
}

.news-item p+p {
  margin-top: 0.9rem;
}

.news-message {
  display: grid;
  gap: 0.75rem;
}

.news-message p.is-collapsed {
  display: -webkit-box;

  overflow: hidden;

  -webkit-box-orient: vertical;
  -webkit-line-clamp: 6;
}

.news-read-more {
  justify-self: center;

  padding: 0.45rem 0.85rem;

  color: var(--bronze-bold);
  background: transparent;

  border: 0;

  font: inherit;
  font-weight: 700;

  cursor: pointer;
}

.news-read-more:hover,
.news-read-more:focus-visible {
  color: var(--default-color);
  text-decoration: underline;
}

.news-flyer {
  display: block;
  width: 100%;
  max-width: 420px;
  margin: 1rem auto 1.25rem;
  border-radius: 10px;
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.25);
}

.news-reader {
  position: fixed;
  inset: 0;
  z-index: 5000;

  display: flex;
  align-items: center;
  justify-content: center;

  padding: 2rem;

  background-color: rgba(8, 6, 5, 0.94);
}

.news-reader-card {
  position: relative;

  width: min(100%, 700px);
  max-height: calc(100vh - 4rem);
  padding: 2.5rem;

  overflow-y: auto;

  color: var(--text-primary);
  background-color: var(--background-dark);

  border: 1px solid var(--bronze-color);
  border-radius: 0.5rem;

  text-align: left;
}

.news-reader-card h2 {
  margin: 0 3rem 1.5rem 0;

  color: var(--bronze-bold);
}

.news-reader-card p {
  margin: 1.5rem 0 0;

  line-height: 1.75;
}

.news-reader-image {
  display: block;

  width: 100%;
  max-height: 55vh;

  object-fit: contain;

  border-radius: 0.5rem;
}

.news-reader-close {
  position: absolute;
  top: 0.75rem;
  right: 0.75rem;

  display: flex;
  align-items: center;
  justify-content: center;

  width: 2.75rem;
  height: 2.75rem;
  padding: 0;

  color: var(--default-color);
  background-color: var(--background-dark-trans);

  border: 1px solid var(--bronze-color);
  border-radius: 50%;

  font-size: 1.75rem;
  line-height: 1;

  cursor: pointer;
}

.news-reader-close:hover,
.news-reader-close:focus-visible {
  background-color: var(--bronze-bold);
}

:global(body.news-reader-open) {
  overflow: hidden;
}

@media (max-width: 600px) {
  .news-card {
    margin-bottom: 4rem;
    padding: 1.25rem;

    text-align: left;
  }
}
</style>