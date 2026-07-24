<template>
  <main class="menu-page">
    <nav class="menu-destination" aria-label="Menu categories">
      <button v-for="menu in menuPages" :key="menu.id" type="button" :class="{ active: selectedMenu === menu.id }"
        @click="selectMenu(menu.id)">
        {{ menu.label }}
      </button>
    </nav>

    <section class="menu-panel">
      <Teleport to="body" :disabled="!isFullscreen">
        <div class="vintage-menu" :class="{ 'is-fullscreen': isFullscreen }">
          <div class="menu-toolbar">
            <button type="button" class="fullscreen-button"
              :aria-label="isFullscreen ? 'Exit full screen menu' : 'Open full screen menu'" @click="toggleFullscreen">
              <span aria-hidden="true">
                {{ isFullscreen ? '×' : '⛶' }}
              </span>

              {{ isFullscreen ? 'Close' : 'Full Screen' }}
            </button>
          </div>
          <div class="menu-binding" aria-hidden="true"></div>

          <div class="menu-book">
            <Transition :name="transitionName">
              <div :key="selectedMenu" class="menu-page-sheet">
                <img :src="currentMenu.image" :alt="currentMenu.alt" class="menu-image" />

                <div class="paper-shading" aria-hidden="true"></div>
              </div>
            </Transition>
          </div>

          <div class="menu-controls">
            <button type="button" class="page-control" :disabled="currentIndex === 0" @click="previousPage">
              <span aria-hidden="true">←</span>
              Previous
            </button>

            <p class="page-number">
              Page {{ currentIndex + 1 }} of {{ menuPages.length }}
            </p>

            <button type="button" class="page-control" :disabled="currentIndex === menuPages.length - 1"
              @click="nextPage">
              Next
              <span aria-hidden="true">→</span>
            </button>
          </div>
        </div>
      </Teleport>
    </section>
  </main>
</template>

<script setup>
import {
  computed,
  ref,
  watch,
  onMounted,
  onBeforeUnmount
} from 'vue'

import starters from '@/assets/menu/starters-salads.png'
import pizza from '@/assets/menu/pizza-pasta.png'
import favorites from '@/assets/menu/house-favorites.png'
import sandwiches from '@/assets/menu/sandwiches.png'
import desserts from '@/assets/menu/desserts-drinks.png'
import { trackMenuSectionClick } from "@/utils/analytics"

const menuPages = [
  {
    id: 'starters',
    label: 'Starters',
    image: starters,
    alt: 'Starters and salads menu'
  },
  {
    id: 'pizza',
    label: 'Pizza & Pasta',
    image: pizza,
    alt: 'Pizza and pasta menu'
  },
  {
    id: 'favorites',
    label: 'House Favorites',
    image: favorites,
    alt: 'House favorites menu'
  },
  {
    id: 'sandwiches',
    label: 'Sandwiches',
    image: sandwiches,
    alt: 'Sandwiches menu'
  },
  {
    id: 'desserts',
    label: 'Desserts & Drinks',
    image: desserts,
    alt: 'Desserts and drinks menu'
  }
]

const selectedMenu = ref('starters')
const transitionName = ref('turn-forward')
const isFullscreen = ref(false)

const currentIndex = computed(() => {
  return menuPages.findIndex(
    (menu) => menu.id === selectedMenu.value
  )
})

const currentMenu = computed(() => {
  return menuPages[currentIndex.value]
})

function selectMenu(id) {
  const nextIndex = menuPages.findIndex(
    (menu) => menu.id === id
  )

  if (nextIndex === currentIndex.value) return

  trackMenuSectionClick(id)

  transitionName.value =
    nextIndex > currentIndex.value
      ? 'turn-forward'
      : 'turn-backward'

  selectedMenu.value = id
}

function nextPage() {
  if (currentIndex.value >= menuPages.length - 1) return

  const nextMenu = menuPages[currentIndex.value + 1]

  trackMenuSectionClick(nextMenu.id)

  transitionName.value = 'turn-forward'
  selectedMenu.value = nextMenu.id
}

function previousPage() {
  if (currentIndex.value <= 0) return

  const previousMenu = menuPages[currentIndex.value - 1]

  trackMenuSectionClick(previousMenu.id)

  transitionName.value = 'turn-backward'
  selectedMenu.value = previousMenu.id
}

function toggleFullscreen() {
  isFullscreen.value = !isFullscreen.value
}

function closeFullscreen() {
  isFullscreen.value = false
}

function handleFullscreenKeydown(event) {
  if (event.key === 'Escape' && isFullscreen.value) {
    closeFullscreen()
  }
}

watch(isFullscreen, (isOpen) => {
  document.body.style.overflow = isOpen ? 'hidden' : ''
})

onMounted(() => {
  window.addEventListener('keydown', handleFullscreenKeydown)
})

onBeforeUnmount(() => {
  window.removeEventListener('keydown', handleFullscreenKeydown)

  // Restore scrolling if the component is removed while fullscreen is open.
  document.body.style.overflow = ''
})

</script>

<style scoped>
.menu-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: calc(100vh - var(--header-height, 0px));
  padding: 1rem;
}

/* =========================================================
   CATEGORY TABS
   ========================================================= */

.menu-destination {
  position: sticky;
  top: var(--header-height);
  z-index: 20;

  display: flex;
  justify-content: center;
  gap: 0.5rem;

  width: 100%;
  padding: 0.75rem;
  overflow-x: auto;

  background: rgba(255, 248, 235, 0.96);
  border-bottom: 1px solid #d6b98c;
}

.menu-destination button {
  flex-shrink: 0;
  padding: 0.55rem 0.9rem;

  border: 1px solid transparent;
  border-radius: 4px;

  background: transparent;
  color: #4a2414;

  font: inherit;
  font-weight: 700;
  cursor: pointer;

  transition:
    background-color 0.2s ease,
    border-color 0.2s ease,
    transform 0.2s ease;
}

.menu-destination button:hover {
  border-color: #a87f45;
  background: rgba(138, 106, 50, 0.08);
}

.menu-destination button.active {
  color: #fffaf1;
  background: #6f421f;
  border-color: #8a6a32;
  transform: translateY(2px);
}

/* =========================================================
   OUTER MENU BOOK
   ========================================================= */

.menu-panel {
  width: min(900px, 100%);
  padding: 1.25rem 0;
  display: flex;
  justify-content: center;
  perspective: 2200px;
}

.vintage-menu {
  position: relative;
  width: min(100%, 650px);
  padding: clamp(0.6rem, 1.5vw, 1rem);

  background:
    linear-gradient(135deg,
      #4d2c18,
      #2d180f 48%,
      #5a351f);

  border: 3px solid #8a6a32;
  border-radius: 8px;

  box-shadow:
    0 20px 40px rgba(20, 10, 5, 0.35),
    inset 0 0 0 2px rgba(255, 224, 163, 0.16),
    inset 0 0 25px rgba(0, 0, 0, 0.5);
}

/* Decorative inner line */

.vintage-menu::before {
  content: "";
  position: absolute;
  inset: 0.45rem;

  border: 1px solid rgba(214, 185, 140, 0.45);
  border-radius: 4px;

  pointer-events: none;
}

/* =========================================================
   FULLSCREEN MENU
   ========================================================= */

.vintage-menu.is-fullscreen {
  position: fixed;
  inset: 0;

  z-index: 2147483647;

  display: grid;
  grid-template-rows: auto minmax(0, 1fr) auto;

  width: 100vw;
  max-width: none;
  height: 100vh;
  height: 100dvh;

  margin: 0;
  padding:
    max(0.5rem, env(safe-area-inset-top)) max(0.5rem, env(safe-area-inset-right)) max(0.5rem, env(safe-area-inset-bottom)) max(0.5rem, env(safe-area-inset-left));

  border: none;
  border-radius: 0;

  overflow: hidden;

  background:
    radial-gradient(circle at center,
      #5a351f,
      #2d180f 65%,
      #160c08);
}

.vintage-menu.is-fullscreen .menu-book {
  align-self: center;
  justify-self: center;

  width: auto;
  height: 100%;

  max-width: 100%;
  max-height: 100%;

  aspect-ratio: 8.5 / 11;

  margin: 0;
}

.vintage-menu.is-fullscreen .menu-toolbar {
  width: min(100%, 48rem);
  margin: 0 auto 0.4rem;
}

.vintage-menu.is-fullscreen .menu-controls {
  width: min(100%, 48rem);
  margin: 0 auto;

  padding: 0.55rem 0.25rem 0.1rem;
}

.vintage-menu.is-fullscreen .menu-binding {
  display: none;
}

/* =========================================================
   MENU BINDING
   ========================================================= */

.menu-binding {
  position: absolute;
  top: 1.2rem;
  bottom: 4.9rem;
  left: 1.2rem;

  width: 12px;
  z-index: 10;

  border-radius: 10px;

  background:
    linear-gradient(to right,
      rgba(0, 0, 0, 0.75),
      rgba(125, 82, 42, 0.8),
      rgba(255, 222, 170, 0.18),
      rgba(0, 0, 0, 0.75));

  box-shadow:
    3px 0 8px rgba(0, 0, 0, 0.45),
    -2px 0 4px rgba(255, 255, 255, 0.08);
}

/* =========================================================
   PAPER PAGE
   ========================================================= */

.menu-book {
  position: relative;
  overflow: hidden;

  width: 100%;
  aspect-ratio: 8.5 / 11;

  margin-left: 0.35rem;

  background: #efe3c8;
  border-radius: 3px;

  transform-style: preserve-3d;

  box-shadow:
    inset 16px 0 18px rgba(78, 45, 24, 0.18),
    inset -10px 0 18px rgba(78, 45, 24, 0.08);
}

/* =========================================================
   MENU TOOLBAR
   ========================================================= */

.menu-toolbar {
  position: relative;
  z-index: 15;

  display: flex;
  justify-content: flex-end;

  margin-bottom: 0.5rem;
}

.fullscreen-button {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;

  padding: 0.45rem 0.7rem;

  border: 1px solid rgba(214, 185, 140, 0.6);
  border-radius: 4px;

  background: rgba(255, 250, 241, 0.08);
  color: #fff2d7;

  font: inherit;
  font-size: 0.85rem;
  font-weight: 700;

  cursor: pointer;

  transition:
    background-color 0.2s ease,
    transform 0.2s ease;
}

.fullscreen-button:hover {
  background: rgba(255, 250, 241, 0.16);
  transform: translateY(-1px);
}

.fullscreen-button span {
  font-size: 1.1rem;
  line-height: 1;
}

.menu-page-sheet {
  position: absolute;
  inset: 0;
  z-index: 1;
  width: 100%;
  height: 100%;
  background: #fffaf1;
  backface-visibility: hidden;
  -webkit-backface-visibility: hidden;
  /* transform-style: preserve-3d;
  transform: translateZ(0); */
}

.menu-image {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: contain;
  position: relative;
  z-index: 1;
}

/* Gives the page an aged edge and subtle paper depth */

.paper-shading {
  position: absolute;
  inset: 0;
  z-index: 2;
  pointer-events: none;

  background:
    linear-gradient(90deg,
      rgba(82, 45, 20, 0.18),
      transparent 7%,
      transparent 87%,
      rgba(82, 45, 20, 0.1)),
    radial-gradient(circle at center,
      transparent 65%,
      rgba(105, 68, 35, 0.1));

  box-shadow:
    inset 0 0 22px rgba(92, 55, 26, 0.15),
    inset 0 0 2px rgba(57, 31, 13, 0.6);
}

/* =========================================================
   PAGE TURN — SHARED SETUP
   ========================================================= */

.turn-forward-enter-active,
.turn-forward-leave-active,
.turn-backward-enter-active,
.turn-backward-leave-active {
  position: absolute;
  inset: 0;

  transition:
    transform 1.2s cubic-bezier(0.55, 0.05, 0.3, 0.95),
    opacity 1.2s ease,
    filter 1.2s ease,
    box-shadow 1.2s ease;

  will-change: transform, opacity, filter;
  backface-visibility: hidden;
  -webkit-backface-visibility: hidden;
}

/* Forward */

.turn-forward-leave-active,
.turn-forward-leave-to {
  z-index: 3;
  backface-visibility: hidden;
  -webkit-backface-visibility: hidden;
}

/* Backward */

.turn-backward-enter-active,
.turn-backward-enter-to {
  z-index: 3;
  backface-visibility: hidden;
  -webkit-backface-visibility: hidden;
}

/* =========================================================
FORWARD PAGE TURN
========================================================= */

.turn-forward-leave-active {
  transform-origin: left center;

  /* Keeps the page in its final turned position */
  transform: rotateY(-92deg) translateX(-2%) scaleX(0.96);
  opacity: 0;
  filter: brightness(0.55);
}

.turn-forward-leave-from {
  transform: rotateY(0deg) translateX(0) scaleX(1);
  opacity: 1;
  filter: brightness(1);
}

.turn-forward-leave-to {
  transform: rotateY(-92deg) translateX(-2%) scaleX(0.96);
  opacity: 0;
  filter: brightness(0.55);

  box-shadow:
    -30px 0 35px rgba(0, 0, 0, 0.55);
}



/* =========================================================
   BACKWARD PAGE TURN
   ========================================================= */

.turn-backward-enter-active,
.turn-backward-leave-active {
  transform-origin: left center;
}


/* Old page remains underneath */

.turn-backward-leave-from {
  transform: rotateY(0deg);
  opacity: 1;
  filter: brightness(1);
}

.turn-backward-leave-to {
  transform: rotateY(0deg);
  opacity: 1;
  filter: brightness(0.82);
}

/* New page begins edge-on at the left and opens across the book */

.turn-backward-enter-from {
  transform: rotateY(-92deg);
  opacity: 0;
  filter: brightness(0.55);

  box-shadow:
    30px 0 35px rgba(0, 0, 0, 0.55);
}

.turn-backward-enter-to {
  transform: rotateY(0deg);
  opacity: 1;
  filter: brightness(1);

  box-shadow:
    0 0 0 rgba(0, 0, 0, 0);
}

/* =========================================================
   BOTTOM CONTROLS
   ========================================================= */

.menu-controls {
  position: relative;
  z-index: 5;

  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  gap: 1rem;

  padding: 1rem 0.35rem 0.1rem;
}

.page-control {
  padding: 0.55rem 0.8rem;

  border: 1px solid rgba(214, 185, 140, 0.55);
  border-radius: 4px;

  background: rgba(255, 250, 241, 0.08);
  color: #fff2d7;

  font: inherit;
  cursor: pointer;

  transition:
    background-color 0.2s ease,
    transform 0.2s ease,
    opacity 0.2s ease;
}

.page-control:first-child {
  justify-self: start;
}

.page-control:last-child {
  justify-self: end;
}

.page-control:hover:not(:disabled) {
  background: rgba(255, 250, 241, 0.15);
  transform: translateY(-1px);
}

.page-control:disabled {
  opacity: 0.3;
  cursor: default;
}

.page-number {
  margin: 0;
  color: #e4cda6;

  font-size: 0.85rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

/* =========================================================
   MOBILE
   ========================================================= */

@media (max-width: 700px) {

  .menu-destination {
    flex-direction: column;
    align-items: stretch;
    gap: .2rem;
    position: sticky;
    max-height: 220px;
    padding: .35rem .5rem;
    overflow-y: auto;
  }

  .menu-destination button {

    width: 100%;

    text-align: center;

    padding: .55rem .75rem;

    font-size: .9rem;
    line-height: 1.2;
  }

  .menu-destination button.active {

    padding-top: .55rem;
    padding-bottom: .55rem;

    border-radius: 6px;
  }

  .menu-page {
    padding-inline: 0;
  }

  .menu-panel {
    padding-top: .25rem;
  }

  .vintage-menu {

    width: 100%;

    max-width: 540px;

    margin: auto;
  }

  @media (max-width: 700px) {
    .menu-toolbar {
      margin-bottom: 0.35rem;
    }

    .fullscreen-button {
      padding: 0.4rem 0.6rem;
      font-size: 0.78rem;
    }

    .vintage-menu.is-fullscreen {
      padding:
        max(0.35rem, env(safe-area-inset-top)) max(0.35rem, env(safe-area-inset-right)) max(0.35rem, env(safe-area-inset-bottom)) max(0.35rem, env(safe-area-inset-left));
    }

    .vintage-menu.is-fullscreen .menu-controls {
      display: grid;
      grid-template-columns: 1fr auto 1fr;
      gap: 0.35rem;

      padding-top: 0.4rem;
    }

    .vintage-menu.is-fullscreen .page-control {
      min-height: 2.5rem;
    }
  }

  .menu-binding {
    top: 0.9rem;
    bottom: 4.6rem;
    left: 0.55rem;
    width: 8px;
  }

  .menu-book {
    margin-left: 0.3rem;
  }

  .menu-controls {

    display: flex;

    flex-direction: column;

    gap: .5rem;
  }

  .page-control {
    padding: 0.5rem;
    font-size: 0.82rem;
  }

  .page-number {
    font-size: 0.7rem;
    order: -1;
  }
}

/*For visitors who reduce motion */

@media (prefers-reduced-motion: reduce) {

  .turn-forward-enter-active,
  .turn-forward-leave-active,
  .turn-backward-enter-active,
  .turn-backward-leave-active {
    transition: opacity 0.2s ease;
  }

  .turn-forward-enter-from,
  .turn-forward-leave-to,
  .turn-backward-enter-from,
  .turn-backward-leave-to {
    transform: none;
  }
}
</style>