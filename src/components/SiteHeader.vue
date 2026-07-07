<template>
  <header class="header">
    <div class="viewButtons">
      <RouterLink to="/" class="logo-link">
        <img src="@/assets/hero-logo.jpg" alt="Coccia House logo" class="logo" />
      </RouterLink>

      <nav class="nav">
        <RouterLink to="/">Home</RouterLink>
        <RouterLink to="/menu">Menu</RouterLink>
        <RouterLink to="/about">About</RouterLink>
      </nav>
    </div>

    <!-- hours display section -->
    <section class="hours-menu" @mouseenter="showAllHours = true" @mouseleave="showAllHours = false">
      <button class="hours-trigger" type="button" :aria-expanded="showAllHours"
        aria-label="View weekly restaurant hours" @click="showAllHours = !showAllHours">
        <Hours :show-all="false" />

        <span class="hours-arrow" :class="{ expanded: showAllHours }">
          ▼
        </span>
      </button>

      <Transition name="hours-dropdown">
        <div v-show="showAllHours" class="weekly-hours-dropdown">
          <Hours :show-all="true" />
        </div>
      </Transition>

    </section>
  </header>
</template>

<script setup>

import { ref } from 'vue'
import { RouterLink } from 'vue-router'
import Hours from './Hours.vue'
const showAllHours = ref(false)
</script>

<style scoped>
.header {
  position: sticky;
  top: 0;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 90px;
  padding: 0.75rem 1.5rem;
  background-image:
    linear-gradient(rgba(0, 0, 0, 0.55), rgba(0, 0, 0, 0.55)),
    url('@/assets/dinner.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  box-shadow: 0 8px 24px rgb(0, 0, 0, 0.35);
}

.logo {
  height: 70px;
  width: auto;
  display: block;
}

.viewButtons {
  display: flex;

}

.nav {
  flex: 1;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-evenly;
  align-items: center;
}

.nav a {
  position: relative;
  font-family: 'Grand Hotel', cursive;
  font-size: 2.5rem;
  text-decoration: none;
  font-weight: 350;
  color: white;
  background-color: transparent;
  padding: 0.65rem 1.6rem;
  border-radius: 4px;
  transition:
    background-color 0.3s ease,
    transform 0.3s ease;
}

.nav a::before {
  content: "";
  position: absolute;
  left: 50%;
  bottom: 0.35rem;
  ;
  width: 0;
  height: 2px;
  background-color: white;
  border-radius: 999px;
  opacity: 0;
  transform: translateX(-50%);
  transition:
    width 0.3s ease,
    opacity 0.3s ease;
}

.nav a:hover::before {
  width: 65%;
  opacity: 1;
}

.nav a:hover {
  transform: translateY(-2px);
}

/* ==========================================================
   HOURS MENU
   ========================================================== */

.hours-menu {
  position: relative;
  align-self: flex-start;
  margin-top: 0.75rem;
}


/* ==========================================================
   COMPACT CURRENT-DAY TRIGGER
   ========================================================== */

.hours-trigger {
  display: flex;
  align-items: flex-end;
  gap: 0.4rem;
  padding: 0.5rem 0.75rem;
  border: none;
  border-radius: 5px;
  background-color: rgba(255, 255, 255, 0.94);
  color: #111;
  text-align: left;
  cursor: pointer;
}

.hours-trigger:hover,
.hours-trigger:focus-visible {
  background-color: white;
}

.hours-arrow {
  display: inline-block;
  margin-bottom: 0.15rem;
  font-size: 0.65rem;
  line-height: 1;
  transition: transform 0.25s ease;
}

.hours-arrow.expanded {
  transform: rotate(180deg);
}


/* ==========================================================
   WEEKLY HOURS POPOVER
   ========================================================== */

.weekly-hours-dropdown {
  position: absolute;
  top: calc(100% + 0.25rem);
  left: 0;
  z-index: 1100;
  width: max-content;
  padding: 0.75rem 1rem;
  background-color: white;
  color: #111;
  border: 1px solid rgba(0, 0, 0, 0.2);
  border-radius: 5px;
  box-shadow: 0 30px 30px rgb(0 0 0 / 40%);
  transform-origin: top left;
}

/* ==========================================================
   DROPDOWN ENTER AND LEAVE TRANSITIONS
   ========================================================== */

/*
  These are the resting states while the dropdown is visible
  and while it finishes entering.
*/

.hours-dropdown-enter-active,
.hours-dropdown-leave-active {
  transition:
    opacity 0.2s ease-in-out,
    transform 0.35s ease;
}


/*
  Before entering and after leaving, the dropdown is:
  - transparent
  - slightly raised
  - slightly compressed vertically
*/

.hours-dropdown-enter-from,
.hours-dropdown-leave-to {
  opacity: 0;

  transform:
    translateY(-0.95rem)
    scaleY(0.32);
}


/*
  While visible, it returns to its normal position and size.
*/

.hours-dropdown-enter-to,
.hours-dropdown-leave-from {
  opacity: 1;

  transform:
    translateY(0)
    scaleY(1);
}
</style>