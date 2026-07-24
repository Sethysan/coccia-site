<template>
  <header ref="headerRef" class="header">
    <!-- DESKTOP HEADER -->
    <div class="desktop-header">
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
      <div class="visitor-info">

        <!-- Hours dropdown -->
        <section class="hours-menu" @mouseenter="showAllHours = true" @mouseleave="showAllHours = false"
          @focusin="showAllHours = true" @focusout="showAllHours = false">
          <button class="hours-trigger" type="button" :aria-expanded="showAllHours"
            aria-label="View weekly restaurant hours" @click="toggleDesktopHours">
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
        <a class="visitor-link visitor-phone" href="tel:+13302627136" @click="trackPhoneClick('desktopHeader')"
          aria-label="Call Coccia House at 330-262-7136">
          <span class="visitor-icon" aria-hidden="true">
            ☎
          </span>

          <span>
            (330) 262-7136
          </span>
        </a>


        <!-- Google Maps directions -->
        <a class="visitor-link visitor-directions"
          href="https://www.google.com/maps/search/?api=1&query=Coccia+House+Wooster+Ohio" target="_blank"
          rel="noopener noreferrer" @click="trackDirectionsClick('desktopHeader')"
          aria-label="Get directions to Coccia House in Google Maps">
          <span class="visitor-icon" aria-hidden="true">
            ◉
          </span>

          <span>
            Get Directions
          </span>
        </a>
      </div>
    </div>

    <!-- MOBILE HEADER -->
    <div class="mobile-header">
      <div class="mobile-top-row">
        <a href="tel:+13302627136" @click="trackPhoneClick('mobileHeader')" class="mobile-phone">
          Phone
        </a>

        <RouterLink to="/" class="mobile-logo-link">
          <img src="@/assets/coccia-logo.png" alt="Coccia House logo" class="mobile-logo" />
        </RouterLink>
        <a href="https://www.google.com/maps/search/?api=1&query=Coccia+House+Wooster+Ohio" target="_blank"
          rel="noopener" @click="trackDirectionsClick('mobileHeader')" class="mobile-directions">
          Directions
        </a>
      </div>

      <nav class="mobile-nav" aria-label="Main navigation">
        <RouterLink to="/">Home</RouterLink>
        <RouterLink to="/menu">Menu</RouterLink>
        <RouterLink to="/about">About</RouterLink>
      </nav>

      <section class="mobile-hours">
        <button type="button" class="mobile-hours-trigger" :aria-expanded="showMobileHours" @click="toggleMobileHours">
          <span class="mobile-hours-today">
            View Weekly Hours
          </span>

          <span class="mobile-hours-arrow" :class="{ rotated: showMobileHours }" aria-hidden="true">
            ▼
          </span>
        </button>
        <Transition name="mobile-hours">
          <div v-if="showMobileHours" class="mobile-hours-dropdown">

            <h3 class="mobile-hours-title">
              This Week at Coccia House
            </h3>

            <Hours :show-all="true" />

          </div>
        </Transition>
      </section>
    </div>
    <!-- <ScrollworkDivider /> -->
  </header>
</template>

<script setup>
import {
  ref,
  onMounted,
  onBeforeUnmount,
  nextTick
} from "vue";
import { RouterLink } from 'vue-router'
import Hours from './Hours.vue'
import { useRestaurantHours } from '@/composables/useRestaurantHours'
import {
  trackPhoneClick,
  trackDirectionsClick,
  trackHoursOpened
} from "@/utils/analytics"
// import ScrollworkDivider from './ScrollworkDivider.vue'

// -----------------------------------------------------------------------------
// determining header height and creating variable to set view top values
// -----------------------------------------------------------------------------

const headerRef = ref(null);
//  Measure
function updateHeaderHeight() {
  if (!headerRef.value) return;

  document.documentElement.style.setProperty(
    "--header-height",
    `${headerRef.value.offsetHeight}px`
  );
}

// constant observation and adjustment

onMounted(async () => {
  await nextTick();

  updateHeaderHeight();

  window.addEventListener("resize", updateHeaderHeight);
});

onBeforeUnmount(() => {
  window.removeEventListener("resize", updateHeaderHeight);
});

// -----------------------------------------------------------------------------
// Dropdown state
// -----------------------------------------------------------------------------

const showMobileHours = ref(false)
const showAllHours = ref(false)

const toggleDesktopHours = () => {
  showAllHours.value = !showAllHours.value

  if (showAllHours.value) {
    trackHoursOpened("desktopHeader")
  }
}

const toggleMobileHours = () => {
  showMobileHours.value = !showMobileHours.value

  if (showMobileHours.value) {
    trackHoursOpened("mobileHeader")
  }
}

// -----------------------------------------------------------------------------
// Restaurant-hours helpers
//
// compactHoursMessage is displayed inside the mobile hours trigger.
// -----------------------------------------------------------------------------
const {
  compactHoursMessage
} = useRestaurantHours()

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

.desktop-header {
  display: block;
}

.mobile-header {
  display: none;
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
  left: 52%;
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

  /* width: 1.4rem; */
  height: 1.4rem;

  color: var(--bronze-bold);

  font-size: 1.15rem;
  line-height: 1;
}

.visitor-link::after {
  content: "";

  position: absolute;
  left: 51%;
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

.mobile-hours-enter-active,
.mobile-hours-leave-active {
  transition:
    opacity 0.2s ease-in-out,
    transform 0.35s ease;
}

.mobile-hours-enter-from,
.mobile-hours-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(-8px);
}

.mobile-hours-enter-to,
.mobile-hours-leave-from {
  opacity: 1;
  transform: translateX(-50%) translateY(0);
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
  .desktop-header {
    display: none;
  }

  .mobile-header {
    display: block;
  }

  .home-page {
    padding-top: 0;
  }

  .header {
    position: sticky;
    top: 0;
    z-index: 1100;
    width: 100%;
  }

  .mobile-header {
    position: relative;
    width: 100%;
    background: var(--background-dark-trans);
    border-bottom: 1px solid var(--bronze-color);
  }

  .mobile-top-row {
    display: grid;
    grid-template-columns: 1fr auto 1fr;
    align-items: center;
    min-height: 54px;
    padding: 0.25rem 0.65rem;
  }

  .mobile-phone,
  .mobile-directions {
    color: var(--default-color);
    font-size: 0.72rem;
    text-decoration: none;
    white-space: nowrap;
  }

  .mobile-phone {
    justify-self: start;
  }

  .mobile-directions {
    justify-self: end;
    text-align: right;
  }

  .mobile-logo-link {
    justify-self: center;
  }

  .mobile-logo {
    display: block;
    width: clamp(90px, 25vw, 115px);
    height: auto;
  }

  .mobile-nav {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    border-top: 1px solid var(--bronze-color);
    border-bottom: 1px solid var(--bronze-color);
  }

  .mobile-nav a {
    padding: 0.55rem 0.25rem;
    color: var(--default-color);
    text-align: center;
    text-decoration: none;
  }

  .mobile-hours {
    position: relative;
  }

  .mobile-hours-trigger {
    width: 100%;
    padding: .55rem .75rem;

    display: flex;
    justify-content: center;
    align-items: center;
    gap: .35rem;
    font: inherit;
    background: rgba(20, 15, 12, .94);
    color: var(--default-color);
    border: none;
    cursor: pointer;
    font-size: .9rem;
    font-weight: 600;
    transition:
      background .25s,
      color .25s;
  }

  .mobile-hours-today {
    white-space: nowrap;
  }

  .mobile-hours-trigger:hover {
    background: rgba(35, 28, 22, .98);
  }

  .mobile-hours-trigger:focus-visible {
    outline: 2px solid var(--bronze-bold);
    outline-offset: -2px;
  }

  .mobile-hours-arrow {
    font-size: 0.65rem;
    transition: transform 0.2s ease;
  }

  .mobile-hours-arrow.rotated {
    transform: rotate(180deg);
  }

  .mobile-hours-dropdown {
    position: absolute;
    top: 100%;
    left: 50%;
    z-index: 1200;
    width: min(92vw, 340px);
    max-height: 65vh;
    overflow-y: auto;
    transform: translateX(-50%);
    padding: 0.85rem;
    background: var(--default-color);
    color: #1f1712;
    border-top: 1px solid rgba(0, 0, 0, .08);
    border-bottom: 2px solid var(--bronze-bold);
    box-shadow:
      0 8px 20px rgba(0, 0, 0, .28);
    /* animation: slideHours .5s ease; */
  }

  .mobile-hours-row {

    display: flex;
    justify-content: space-between;
    align-items: flex-start;

    padding: .65rem 1rem;

    border-bottom: 1px solid rgba(0, 0, 0, .06);

  }

  .mobile-hours-row.today {

    background: #fff6e7;

  }

  .mobile-day {

    font-weight: 700;

    color: #2d241d;

  }

  .mobile-time {

    color: #5d5045;

  }

  .mobile-note {

    margin-top: .15rem;

    font-size: .78rem;

    color: #8a6a32;

    font-style: italic;

  }

  .mobile-hours-title {

    padding: .85rem;

    text-align: center;

    font-size: .95rem;

    font-weight: 700;

    color: var(--bronze-bold);

    border-bottom: 1px solid rgba(0, 0, 0, .08);

  }
}

@media (max-width: 450px) {
  .header {
    gap: 0.3rem;
    padding: 0.35rem 0.4rem;
  }

  .logo {
    height: 52px;
  }

  .visitor-link {
    font-size: 0.62rem;
  }

  .visitor-icon {
    display: none;
  }

  .nav {
    gap: 0;
  }

  .nav a {
    padding: 0.2rem 0.35rem;
    font-size: 1.3rem;
  }

  .hours-trigger {
    padding: 0.25rem 0.35rem;
    font-size: 0.68rem;
  }
}


@media (max-width: 375px) {
  .logo[data-v-a8a65ae7] {
    height: 44px;
  }
}

/* @keyframes slideHours {

  from {
    opacity: 0;
    transform: translateY(-8px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }

} */
</style>