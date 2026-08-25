<template>
  <main class="home-page">

    <!-- ========================================================
         TODAY AT COCCIA HOUSE
         ======================================================== -->

    <section class="today-card" aria-labelledby="today-title">
      <h2 id="today-title">Today at Coccia House</h2>

      <p class="today-status" :class="`is-${restaurantStatus.state}`">
        <span class="status-dot" aria-hidden="true"></span>
        {{ restaurantStatus.label }}
      </p>

      <p class="today-day">
        {{ todayHours.name }}
      </p>

      <p class="today-hours">
        {{ todayHours.hours }}
      </p>
      <!-- todo -->
      <!-- <p class="today-message">
        {{ todayHours.note }}
      </p> -->

      <p class="today-message">
        {{ restaurantStatus.message }}
      </p>
    </section>


    <!-- ========================================================
         Weekly Offering
         ======================================================== -->

    <WeeklyOffering />

    <!-- ========================================================
         QUICK ACTIONS
         ======================================================== -->

    <nav class="home-actions" aria-label="Restaurant actions">
      <RouterLink to="/menu" class="home-action primary-action">
        View Menu
      </RouterLink>

      <a href="tel:13302627136" @click="trackPhoneClick('homeView')" class="home-action">
        Call for Carryout
      </a>

      <a href="https://www.google.com/maps/search/?api=1&query=Coccia+House+Wooster+Ohio" target="_blank"
        rel="noopener noreferrer" @click="trackDirectionsClick('homeView')" class="home-action">
        Get Directions
      </a>
    </nav>


    <!-- ========================================================
         WELCOME
         ======================================================== -->

    <section class="home-intro">
      <p class="section-eyebrow">
        {{ homeContent.intro.kicker }}
      </p>

      <h1>
        {{ homeContent.intro.title }}
      </h1>

      <p class="home-intro-text">
        {{ homeContent.intro.text }}
      </p>
    </section>


    <!-- ========================================================
         STORY PREVIEW
         ======================================================== -->

    <section class="story-preview" aria-labelledby="story-title">
      <!-- <p class="section-eyebrow">
        {{ homeContent.story.eyebrow }}
      </p> -->

      <h2 class="section-eyebrow">
        {{ homeContent.story.title }}
      </h2>

      <p class="story-text">
        {{ homeContent.story.text }}
      </p>

      <RouterLink to="/about" class="story-link">
        {{ homeContent.story.buttonText }}
      </RouterLink>
    </section>


    <!-- ========================================================
         HISTORIC PHOTOGRAPHS
         ======================================================== -->

    <section class="family-album" aria-labelledby="album-title">
      <header class="family-album-heading">
        <h2 id="album-title">
          {{ homeContent.gallery.title }}
        </h2>
      </header>

      <HomeGallery />
    </section>


    <!-- ========================================================
         LATEST NEWS
         ======================================================== -->

      <NewsAnnouncements />


    <!-- ========================================================
         MISSION
         ======================================================== -->

    <section class="mission-section" aria-labelledby="mission-title">
      <h2 id="mission-title">
        {{ homeContent.mission.title }}
      </h2>

      <p>
        {{ homeContent.mission.text }}
      </p>
    </section>


    <!-- ========================================================
         SLOGAN
         ======================================================== -->

    <footer class="home-closing">
      <p class="home-slogan">
        {{ homeContent.slogan }}
      </p>
    </footer>

  </main>
</template>

<script setup>

import { RouterLink } from 'vue-router'
import { useRestaurantHours } from '@/composables/useRestaurantHours'
import { homeContent } from '@/content/homeContent.js'
import WeeklyOffering from '@/components/WeeklyOffering.vue'
import HomeGallery from '@/components/HomeGallery.vue'
import { trackPhoneClick } from "@/utils/analytics"
import { trackDirectionsClick } from "@/utils/analytics"
import NewsAnnouncements from '@/components/NewsAnnouncements.vue'

const {
  todayHours,
  restaurantStatus
} = useRestaurantHours()

</script>

<style scoped>
/* ==========================================================
   HOME PAGE
   ========================================================== */


.home-page {
  width: 100%;
  padding: 3rem 1.5rem 4rem;
  color: var(--text-primary);
  text-align: center;
}


/* ==========================================================
   SHARED TYPOGRAPHY
   ========================================================== */

.section-eyebrow {
  margin: 0 0 0.5rem;
  color: var(--bronze-bold);
  /* font-size: 0.8rem; */
  font-weight: 700;
  letter-spacing: 0.14em;
  text-transform: uppercase;
}

.home-page h1,
.home-page h2,
.home-page h3,
.home-page p {
  overflow-wrap: break-word;
}


/* ==========================================================
   TODAY CARD
   ========================================================== */

.today-card {
  width: min(100%, 500px);
  margin: 0 auto;
  padding: 1.5rem;

  background-color: var(--background-dark-trans);

  border: 1px solid var(--bronze-color);
  border-radius: 0.5rem;
}

.today-card h2 {
  margin: 0 0 1rem;
}

.today-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;

  margin: 0 0 0.55rem;

  font-size: 1.2rem;
  font-weight: 700;
}

.status-dot {
  flex: 0 0 auto;

  width: 0.7rem;
  height: 0.7rem;

  border-radius: 50%;
}

.today-status.is-open .status-dot {
  background-color: #4caf50;
  box-shadow: 0 0 8px rgba(76, 175, 80, 0.65);
}

.today-status.is-opening-soon .status-dot,
.today-status.is-opening-later .status-dot {
  background-color: #d59a3a;
  box-shadow: 0 0 8px rgba(213, 154, 58, 0.55);
}

.today-status.is-closing-soon .status-dot {
  background-color: #dc7c38;
  box-shadow: 0 0 8px rgba(220, 124, 56, 0.55);
}

.today-status.is-closed .status-dot {
  background-color: #b84b43;
  box-shadow: 0 0 6px rgba(184, 75, 67, 0.4);
}

.today-day,
.today-hours {
  margin: 0;
}

.today-day {
  font-size: 1.05rem;
}

.today-hours {
  margin-top: 0.25rem;
  font-size: 1.05rem;
  font-weight: 700;
}

.today-message {
  max-width: 380px;
  margin: 0.85rem auto 0;

  color: var(--text-primary);

  line-height: 1.5;
}


/* ==========================================================
   QUICK ACTIONS
   ========================================================== */

.home-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 0.85rem;

  width: min(100%, 650px);
  margin: 1rem auto 0;
}

.home-action {
  display: inline-flex;
  align-items: center;
  justify-content: center;

  min-width: 160px;
  padding: 0.8rem 1.25rem;

  color: var(--default-color);
  background-color: var(--background-dark-trans);

  border: 1px solid var(--bronze-bold);
  border-radius: 0.35rem;

  font-weight: 700;
  text-decoration: none;

  transition:
    background-color 200ms ease,
    border-color 200ms ease,
    transform 200ms ease;
}

.home-action:hover,
.home-action:focus-visible {
  background-color: var(--bronze-bold);
  border-color: var(--bronze-hover);
  transform: translateY(-2px);
}

.primary-action {
  background-color: var(--bronze-bold);
}

.primary-action:hover,
.primary-action:focus-visible {
  background-color: var(--bronze-hover);
}


/* ==========================================================
   WELCOME
   ========================================================== */

.home-intro {
  width: min(900px, 94%);
  margin: 7rem auto;
  padding: 0 1rem;
}

.home-intro h1 {
  margin: 0;

  font-size: clamp(2.4rem, 6vw, 4.5rem);
  line-height: 1.05;
}

.home-intro-text {
  max-width: 650px;
  margin: 1.25rem auto 0;

  font-size: clamp(1.05rem, 2vw, 1.25rem);
  line-height: 1.7;
}


/* ==========================================================
   STORY PREVIEW
   ========================================================== */

.story-preview {
  width: min(100%, 600px);
  margin: 0 auto 4rem;
  padding: 2rem;

  background-color: var(--background-dark-trans);

  border: 1px solid var(--bronze-color);
  border-radius: 0.5rem;
}

.story-preview h2 {
  margin: 0 0 1rem;
}

.story-text {
  margin: 0 auto;

  line-height: 1.7;
}

.story-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;

  margin-top: 1.5rem;
  padding: 0.7rem 1.2rem;

  color: var(--default-color);
  background-color: var(--bronze-bold);

  border: 1px solid var(--bronze-bold);
  border-radius: 0.35rem;

  font-weight: 700;
  text-decoration: none;

  transition:
    background-color 200ms ease,
    border-color 200ms ease,
    transform 200ms ease;
}

.story-link:hover,
.story-link:focus-visible {
  background-color: var(--bronze-hover);
  border-color: var(--bronze-hover);
  transform: translateY(-2px);
}


/* ==========================================================
   FAMILY ALBUM
   ========================================================== */

.family-album {
  width: min(100%, 1100px);
  margin: 0 auto 6rem;
}

.family-album-heading {
  margin-bottom: 1.5rem;
}

.family-album-heading h2 {
  margin: 0;

  font-size: clamp(1.7rem, 4vw, 2.7rem);
}

/* ==========================================================
   MISSION
   ========================================================== */

.mission-section {
  width: min(100%, 680px);
  margin: 0 auto;
  /* padding: 3rem 1rem 0; */
  padding-top: 5rem;


  border-top: 1px solid var(--bronze-color);
}

.mission-section h2 {
  margin: 0 0 0.85rem;
}

.mission-section p {
  max-width: 610px;
  margin: 0 auto;

  font-size: 1.05rem;
  line-height: 1.75;
}


/* ==========================================================
   CLOSING SLOGAN
   ========================================================== */

.home-closing {
  .home-closing {
    width: min(100%, 760px);
    margin: 2rem auto 0;
  }
}

.home-slogan {
  margin: 0;

  color: var(--bronze-bold);

  font-size: clamp(1.4rem, 4vw, 2.2rem);
  font-style: italic;
  font-weight: 700;
  line-height: 1.4;
}


/* ==========================================================
   MOBILE
   ========================================================== */

@media (max-width: 600px) {
  .home-page {
    padding: 2rem 0.8rem 3rem;
  }

  .today-card,
  .story-preview {
    padding: 1.25rem;
  }

  .home-actions {
    flex-direction: column;
    align-items: stretch;

    width: min(100%, 500px);
  }

  .home-action {
    width: 100%;
    min-width: 0;
  }

  .home-intro {
    margin: 4rem auto;
    padding: 0 0.5rem;
  }

  .story-preview,
  .family-album {
    margin-bottom: 4rem;
  }

  .family-album {
    width: 100%;
  }

  .mission-section {
    padding-top: 2.25rem;
  }
}
</style>