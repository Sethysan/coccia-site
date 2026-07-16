<template>
  <section class="welcome-section">
    <p class="home-slogan">
      {{ homeContent.slogan }}
    </p>

    <h1>{{ homeContent.welcomeTitle }}</h1>

    <p class="welcome-message">
      {{ homeContent.welcomeMessage }}
    </p>

    <div class="home-actions">
      <RouterLink to="/menu" class="home-action primary-action">
        View Menu
      </RouterLink>

      <a href="tel:13302645475" class="home-action">
        Call for Carryout
      </a>

      <a href="https://www.google.com/maps/search/?api=1&query=Coccia+House+Wooster+Ohio" target="_blank" rel="noopener noreferrer" class="home-action">
        Get Directions
      </a>
    </div>

    <section class="today-card">

<!-- Today's information Card -->

  <h2>Today's at Coccia House</h2>

  <p class="today-status">
    {{ isOpenNow(todayHours) ? '🟢 Open Now' : '🔴 Closed' }}
  </p>

  <p class="today-day">
    {{ todayHours.name }}
  </p>

  <p class="today-hours">
    {{ todayHours.hours }}
  </p>

  <p
    v-if="todayHours.note"
    class="today-note"
  >
    {{ todayHours.note }}
  </p>

</section>

    <div class="mission-section">
      <h2>Our Mission</h2>

      <p>
        {{ homeContent.mission }}
      </p>
    </div>
  </section>
</template>

<script setup>

import { useRestaurantHours } from '@/composables/useRestaurantHours'
import { RouterLink } from 'vue-router'
import { homeContent } from '@/content/homeContent'

const { todayHours, isOpenNow } = useRestaurantHours()

</script>
<style scoped>
/* .news {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #d8d4a0;
  text-shadow: 0 0 10px #666120, 0 0 20px #ffd900b5, 0 0 20px black;
  animation: blink 1.85s infinite;

} */

.welcome-section {
  width: min(900px, 90%);
  margin: 0 auto;
  padding: 3rem 1.5rem;

  text-align: center;
  color: var(--text-primary);
}

.home-slogan {
  margin-bottom: 0.75rem;

  font-size: 1.2rem;
  color: var(--bronze-bold);
  font-style: italic;
  letter-spacing: 0.06rem;
}

.welcome-section h1 {
  margin-bottom: 1rem;
  font-size: clamp(2rem, 5vw, 3.5rem);
}

.welcome-message {
  max-width: 700px;
  margin: 0 auto;

  font-size: 1.05rem;
  line-height: 1.7;
}

.home-actions {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 0.85rem;

  margin-top: 2rem;
}

.home-action {
  display: inline-flex;
  align-items: center;
  justify-content: center;

  min-width: 150px;
  padding: 0.8rem 1.2rem;

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

.today-card {
  max-width: 500px;
  margin: 2.5rem auto;

  padding: 1.75rem;

  text-align: center;

  background-color: var(--background-dark-trans);
  border: 1px solid var(--bronze-color);
  border-radius: 0.5rem;
}

.today-card h2 {
  margin-bottom: 1rem;
}

.today-status {
  font-size: 1.25rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
}

.today-day {
  font-size: 1.1rem;
}

.today-hours {
  margin-top: 0.5rem;
  font-size: 1.05rem;
}

.today-note {
  margin-top: 0.75rem;
  font-style: italic;
  color: var(--default-dark);
}

.mission-section {
  max-width: 700px;
  margin: 3rem auto 0;
  padding: 1.5rem;

  background-color: var(--background-dark-trans);
  border: 1px solid var(--bronze-color);
  border-radius: 0.5rem;
}

.mission-section h2 {
  margin-bottom: 0.75rem;
}

.mission-section p {
  line-height: 1.7;
}

@keyframes blink {

  0% {
    opacity: 1;
  }

  50% {
    opacity: .25;
  }

  100% {
    opacity: 1;
  }

}
</style>