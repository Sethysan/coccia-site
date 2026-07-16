<template>
  <header class="header">
    <div class="viewButtons">
      <RouterLink to="/" class="logo-link">
        <img src="@/assets/coccia-logo.png" alt="Coccia House logo" class="logo" />
      </RouterLink>

      <nav class="nav">
        <RouterLink to="/">Home</RouterLink>
        <RouterLink to="/menu">Menu</RouterLink>
        <RouterLink to="/about">About</RouterLink>
      </nav>
    </div>

    <!-- ========================================================
     VISITOR INFORMATION

     This row contains the three pieces of information most
     visitors are likely to need immediately:

     - Today's hours
     - Phone number
     - Directions
     ======================================================== -->

    <div class="visitor-info">

      <!-- Hours dropdown -->
      <section class="hours-menu" @mouseenter="showAllHours = true" @mouseleave="showAllHours = false"
        @focusin="showAllHours = true" @focusout="showAllHours = false">
        <button class="hours-trigger" type="button" :aria-expanded="showAllHours"
          aria-label="View weekly restaurant hours" @click="showAllHours = !showAllHours">
          <Hours :show-all="false" />

          <span class="hours-arrow" :class="{ expanded: showAllHours }" aria-hidden="true">
            ▼
          </span>
        </button>

        <Transition name="hours-dropdown">
          <div v-show="showAllHours" class="weekly-hours-dropdown">
            <Hours :show-all="true" />
          </div>
        </Transition>
      </section>


      <!-- Phone number -->
      <a class="visitor-link" href="tel:+13302645475" aria-label="Call Coccia House at 330-264-5475">
        <span class="visitor-icon" aria-hidden="true">
          ☎
        </span>

        <span>
          (330) 264-5475
        </span>
      </a>


      <!-- Google Maps directions -->
      <a class="visitor-link" href="https://www.google.com/maps/search/?api=1&query=Coccia+House+Wooster+Ohio"
        target="_blank" rel="noopener noreferrer" aria-label="Get directions to Coccia House in Google Maps">
        <span class="visitor-icon" aria-hidden="true">
          ◉
        </span>

        <span>
          Get Directions
        </span>
      </a>

    </div>
    <!-- <ScrollworkDivider /> -->
  </header>
</template>

<script setup>
import ScrollworkDivider from './ScrollworkDivider.vue'
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
  min-height: 60px;
  padding: 0.75rem 1.0rem;
  background-image:
    linear-gradient(rgba(0, 0, 0, 0.58), rgba(0, 0, 0, 0.842)),
    url('@/assets/dinner.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  border-bottom: 2px solid var(--bronze-color);
  box-shadow:
    inset 0 -1px 0 rgba(255, 255, 255, 0.11),
    0 4px 8px rgb(0, 0, 0),
    0 14px 28px rgba(0, 0, 0, 0.363);
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
  color: var(--text-primary);
  background-color: transparent;
  padding: 0.65rem 2rem;
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
  width: 0;
  height: 2px;
  background-color: var(--default-color);
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
  flex-shrink: 0;
}

/* ==========================================================
   COMPACT CURRENT-DAY TRIGGER
   ========================================================== */

.hours-trigger {
  display: flex;
  align-items: flex-end;
  gap: 0.4rem;
  padding: 0.5rem 0.75rem;
  border: 1px solid rgb(185 133 63 / 45%);
  border-radius: 5px;
  background-color: var(--default-dark);
  color: #21170f;
  text-align: left;
  cursor: pointer;
  box-shadow:
    0 4px 12px rgb(0 0 0 / 25%);
  transition: all .25s ease;

}

.hours-trigger:hover,
.hours-trigger:focus-visible {
  background-color: var(--default-color);
  border-color: #b9853f;

  box-shadow:
    0 6px 16px rgb(0 0 0 / 35%),
    0 0 8px rgb(185 133 63 / 18%);
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
  background-color: var(--background-light-trans);
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

.hours-dropdown-enter-from,
.hours-dropdown-leave-to {
  opacity: 0;

  transform:
    translateY(-0.95rem) scaleY(0.32);
}

.hours-dropdown-enter-to,
.hours-dropdown-leave-from {
  opacity: 1;

  transform:
    translateY(0) scaleY(1);
}

/* ==========================================================
   VISITOR INFORMATION ROW
   ========================================================== */

.visitor-info {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 1.5rem;
  justify-content: space-between;
  align-items: flex-end;

  margin-top: 0.75rem;
}

/* ==========================================================
   PHONE AND DIRECTIONS LINKS
   ========================================================== */

.visitor-link {
  position: relative;

  display: inline-flex;
  align-items: center;
  gap: 0.55rem;

  padding: 0.45rem 0;

  color: var(--text-secondary);

  font-family: system-ui, 'Segoe UI', Roboto, sans-serif;
  font-size: 1rem;
  font-weight: 600;
  text-decoration: none;

  transition:
    color 0.25s ease,
    text-shadow 0.25s ease;
}

.visitor-link:hover,
.visitor-link:focus-visible {
  color: #d5a75d;
  text-shadow:
    0 0 8px rgb(213 167 93 / 45%);
}

.visitor-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;

  width: 1.4rem;
  height: 1.4rem;

  color: var(--bronze-bold);

  font-size: 1.15rem;
  line-height: 1;
}

.visitor-link::after {
  content: "";

  position: absolute;
  left: 50%;
  bottom: 0;

  width: 0;
  height: 1px;

  background-color: #b9853f;

  opacity: 0;

  transform: translateX(-50%);

  transition:
    width 0.25s ease,
    opacity 0.25s ease;
}

.visitor-link:hover::after,
.visitor-link:focus-visible::after {
  width: 100%;
  opacity: 1;
}

/* .scrollwork-divider {
  position: relative;
  z-index: 2000;

  width: calc(100% + 3rem);
  height: 42px;
  margin-left: -1.5rem;
  margin-top: -8px;
  margin-bottom: -38px;

  overflow: visible;
  pointer-events: none;
} */

@media (max-width: 700px) {
  .visitor-info {
    gap: 0.75rem 1.25rem;
    flex-direction: column-reverse;
  }

  .visitor-link {
    font-size: 0.9rem;
  }
  .header{
    flex-direction: row;
  }
}
</style>