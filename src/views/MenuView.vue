<template>
  <main class="menu-page">
    <nav class="menu-destination" aria-label="Menu categories">
      <div class="menu-category-buttons">
        <button v-for="menu in menuPages" :key="menu.id" type="button" class="menu-category-button"
          :class="{ active: selectedMenu === menu.id }" @click="selectMenu(menu.id)">
          {{ menu.label }}
        </button>
      </div>

      <button type="button" class="menu-fullscreen-link" aria-label="View menu in fullscreen"
        @click="openFullscreen('menu_navigation')">
        <span aria-hidden="true">⛶</span>
        View Menu in Fullscreen
      </button>
    </nav>

    <section class="menu-panel">
      <Teleport to="body" :disabled="!isFullscreen">
        <div class="vintage-menu" :class="{ 'is-fullscreen': isFullscreen }">
          <div v-if="isFullscreen" class="menu-toolbar">
            <div class="menu-zoom-controls" aria-label="Menu zoom controls">
              <button type="button" class="fullscreen-button zoom-button" aria-label="Zoom out"
                :disabled="zoomScale <= minimumZoom" @click="zoomOut">
                <span aria-hidden="true">−</span>
              </button>

              <button type="button" class="fullscreen-button zoom-level" aria-label="Reset menu zoom"
                @click="resetZoom">
                {{ Math.round(zoomScale * 100) }}%
              </button>

              <button type="button" class="fullscreen-button zoom-button" aria-label="Zoom in"
                :disabled="zoomScale >= maximumZoom" @click="zoomIn">
                <span aria-hidden="true">+</span>
              </button>
            </div>

            <button type="button" class="fullscreen-button" aria-label="Exit fullscreen menu" @click="closeFullscreen">
              <span aria-hidden="true">×</span>
              Close
            </button>
          </div>
          <div class="menu-binding" aria-hidden="true"></div>

          <div ref="menuBook" class="menu-book" :class="{
            'can-open-fullscreen': !isFullscreen,
            'is-zoomed': isFullscreen && zoomScale > minimumZoom
          }" :role="isFullscreen ? undefined : 'button'" :tabindex="isFullscreen ? undefined : 0" :aria-label="isFullscreen
            ? zoomScale > minimumZoom
              ? `${currentMenu.alt}. Drag to explore the enlarged menu. Use the mouse wheel to zoom.`
              : `${currentMenu.alt}. Swipe to change pages. Pinch or use the mouse wheel to zoom.`
            : `${currentMenu.alt}. Select to view fullscreen.`
            " v-drag-scroll="isFullscreen && zoomScale > minimumZoom" @click="handleMenuBookClick"
            @keydown.enter.prevent="handleMenuBookKeyboardOpen" @keydown.space.prevent="handleMenuBookKeyboardOpen"
            @wheel="handleZoomWheel" @pointerdown="handleSwipeStart" @pointerup="handleSwipeEnd"
            @pointercancel="resetSwipe" @touchstart="handleZoomTouchStart" @touchmove="handleZoomTouchMove"
            @touchend="handleZoomTouchEnd" @touchcancel="handleZoomTouchEnd">

            <Transition :name="transitionName">
              <div :key="currentMenu.id" class="menu-page-sheet" :style="menuPageZoomStyle">
                <img :src="currentMenu.image" :alt="currentMenu.alt" class="menu-image" draggable="false" />

                <div class="paper-shading" aria-hidden="true"></div>
              </div>
            </Transition>
          </div>

          <!-- Controls belong AFTER the closing menu-book div -->
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
  onBeforeUnmount,
  nextTick
} from 'vue'

import starters from '@/assets/menu/starters-salads.png'
import pizza from '@/assets/menu/pizza-pasta.png'
import favorites from '@/assets/menu/house-favorites.png'
import sandwiches from '@/assets/menu/sandwiches.png'
import desserts from '@/assets/menu/desserts-drinks.png'
import { trackMenuSectionClick, trackMenuFullscreenOpen } from "@/utils/analytics"

// Menu Label Section
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

// Full Screen Section

const isFullscreen = ref(false)

const minimumZoom = 1
const maximumZoom = 3
const zoomIncrement = 0.25
const wheelZoomIncrement = 0.15

const zoomScale = ref(minimumZoom)
// Pinch refs
const pinchStartDistance = ref(0)
const pinchStartScale = ref(minimumZoom)
const isPinching = ref(false)
const pinchStartContentX = ref(0)
const pinchStartContentY = ref(0)

const menuBook = ref(null)
const menuPageZoomStyle = computed(() => {
  if (!isFullscreen.value || zoomScale.value <= minimumZoom) {
    return undefined
  }

  const scaledSize = `${zoomScale.value * 100}%`

  return {
    width: scaledSize,
    height: scaledSize
  }
})
// Full Screen Functions

async function openFullscreen(
  source = 'menu_navigation'
) {
  if (isFullscreen.value) return

  trackMenuFullscreenOpen(
    source,
    selectedMenu.value
  )

  isFullscreen.value = true

  await nextTick()
  await resetZoom()
}

function closeFullscreen() {
  isFullscreen.value = false
  zoomScale.value = minimumZoom
}

function handleFullscreenKeydown(event) {
  if (event.key === 'Escape' && isFullscreen.value) {
    closeFullscreen()
  }
}

watch(isFullscreen, (isOpen) => {
  document.body.style.overflow = isOpen ? 'hidden' : ''
})

watch(selectedMenu, () => {
  if (!isFullscreen.value) return

  resetZoom()
})

onMounted(() => {
  window.addEventListener('keydown', handleFullscreenKeydown)

  document.body.classList.add('menu-view-active')
})

onBeforeUnmount(() => {
  window.removeEventListener('keydown', handleFullscreenKeydown)

  // Restore scrolling if the component is removed while fullscreen is open.
  document.body.style.overflow = ''
  document.body.classList.remove('menu-view-active')
})
// Full Screen Click Function
function handleMenuBookClick() {
  if (isFullscreen.value) return

  openFullscreen('menu_image')
}

function handleMenuBookKeyboardOpen() {
  if (isFullscreen.value) return

  openFullscreen('menu_image_keyboard')
}

// Zoom Function Section

function clampZoom(value) {
  return Math.min(maximumZoom, Math.max(minimumZoom, value))
}

function zoomIn() {
  zoomScale.value = clampZoom(
    zoomScale.value + zoomIncrement
  )
}

function zoomOut() {
  zoomScale.value = clampZoom(
    zoomScale.value - zoomIncrement
  )
}

async function resetZoom() {
  zoomScale.value = minimumZoom

  await nextTick()

  menuBook.value?.scrollTo({
    left: 0,
    top: 0
  })
}

function getTouchDistance(touches) {
  const firstTouch = touches[0]
  const secondTouch = touches[1]

  return Math.hypot(
    secondTouch.clientX - firstTouch.clientX,
    secondTouch.clientY - firstTouch.clientY
  )
}

function getTouchMidpoint(touches, container) {
  const rect = container.getBoundingClientRect()

  return {
    x:
      (touches[0].clientX + touches[1].clientX) / 2 -
      rect.left,
    y:
      (touches[0].clientY + touches[1].clientY) / 2 -
      rect.top
  }
}

function handleZoomTouchStart(event) {
  if (
    !isFullscreen.value ||
    event.touches.length !== 2
  ) {
    return
  }

  const container = menuBook.value
  if (!container) return

  event.preventDefault()

  const midpoint = getTouchMidpoint(
    event.touches,
    container
  )

  isPinching.value = true
  pinchStartDistance.value =
    getTouchDistance(event.touches)

  pinchStartScale.value = zoomScale.value

  /*
   * Remember which location in the enlarged menu was
   * beneath the midpoint of the two fingers.
   */
  pinchStartContentX.value =
    container.scrollLeft + midpoint.x

  pinchStartContentY.value =
    container.scrollTop + midpoint.y

  resetSwipe()
}

async function handleZoomTouchMove(event) {
  if (
    !isFullscreen.value ||
    !isPinching.value ||
    event.touches.length !== 2
  ) {
    return
  }

  event.preventDefault()

  const container = menuBook.value
  if (!container) return

  const currentDistance =
    getTouchDistance(event.touches)

  if (pinchStartDistance.value === 0) return

  const scaleChange =
    currentDistance / pinchStartDistance.value

  const newZoom = clampZoom(
    pinchStartScale.value * scaleChange
  )

  const zoomRatio =
    newZoom / pinchStartScale.value

  const midpoint = getTouchMidpoint(
    event.touches,
    container
  )

  zoomScale.value = newZoom

  await nextTick()

  container.scrollLeft =
    pinchStartContentX.value * zoomRatio -
    midpoint.x

  container.scrollTop =
    pinchStartContentY.value * zoomRatio -
    midpoint.y
}

function handleZoomTouchEnd(event) {
  if (event.touches.length >= 2) return

  isPinching.value = false
  pinchStartDistance.value = 0
  pinchStartContentX.value = 0
  pinchStartContentY.value = 0
}

// Menu Page Section
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

async function handleZoomWheel(event) {
  if (!isFullscreen.value) return

  event.preventDefault()

  const container = menuBook.value

  if (!container) return

  const oldZoom = zoomScale.value
  const zoomDirection = event.deltaY < 0 ? 1 : -1

  const newZoom = clampZoom(
    oldZoom + zoomDirection * wheelZoomIncrement
  )

  // Do nothing when already at the zoom boundary.
  if (newZoom === oldZoom) return

  const containerRect = container.getBoundingClientRect()

  /*
   * Mouse position inside the visible scroll container.
   */
  const mouseX = event.clientX - containerRect.left
  const mouseY = event.clientY - containerRect.top

  /*
   * Locate the point beneath the cursor in the old scaled
   * content coordinate system.
   */
  const contentX = container.scrollLeft + mouseX
  const contentY = container.scrollTop + mouseY

  const zoomRatio = newZoom / oldZoom

  zoomScale.value = newZoom

  /*
   * Wait for Vue to resize the menu-page-sheet before setting
   * the new scroll position.
   */
  await nextTick()

  container.scrollLeft =
    contentX * zoomRatio - mouseX

  container.scrollTop =
    contentY * zoomRatio - mouseY
}

// Swipe page navigation

const swipeStartX = ref(0)
const swipeStartY = ref(0)
const swipePointerId = ref(null)

const minimumSwipeDistance = 60

function resetSwipe() {
  swipeStartX.value = 0
  swipeStartY.value = 0
  swipePointerId.value = null
}

function handleSwipeStart(event) {
  /*
   * Once zoomed, the drag-scroll directive owns the pointer
   * interaction. A drag should pan, not change pages.
   */
  if (
    isPinching.value ||
    (isFullscreen.value && zoomScale.value > minimumZoom)
  ) {
    resetSwipe()
    return
  }

  if (
    event.pointerType === 'mouse' &&
    event.button !== 0
  ) {
    return
  }

  swipePointerId.value = event.pointerId
  swipeStartX.value = event.clientX
  swipeStartY.value = event.clientY
}

function handleSwipeEnd(event) {
  if (
    swipePointerId.value === null ||
    event.pointerId !== swipePointerId.value
  ) {
    return
  }

  if (
    isPinching.value ||
    (isFullscreen.value && zoomScale.value > minimumZoom)
  ) {
    resetSwipe()
    return
  }

  const distanceX =
    event.clientX - swipeStartX.value

  const distanceY =
    event.clientY - swipeStartY.value

  const absoluteX = Math.abs(distanceX)
  const absoluteY = Math.abs(distanceY)

  const isHorizontalSwipe =
    absoluteX >= minimumSwipeDistance &&
    absoluteX > absoluteY

  if (!isHorizontalSwipe) {
    resetSwipe()
    return
  }

  if (distanceX < 0) {
    nextPage()
  } else {
    previousPage()
  }

  resetSwipe()
}
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
  top: 0;
  z-index: 20;

  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.65rem;

  width: 100%;
  padding: 0.75rem;

  background: rgba(255, 248, 235, 0.96);
  border-bottom: 1px solid #d6b98c;
}

.menu-category-buttons {
  display: flex;
  justify-content: center;
  gap: 0.5rem;

  width: 100%;
  overflow-x: auto;

  scrollbar-width: thin;
}

.menu-category-button {
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
    color 0.4s ease,
    background-color 0.4s ease,
    border-color 0.4s ease,
    transform 0.4s ease;
}

.menu-fullscreen-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.45rem;

  padding: 0.45rem 0.85rem;

  border: 0;
  border-top: 1px solid rgba(138, 106, 50, 0.35);
  border-radius: 0;

  background: transparent;
  color: #6f421f;

  font: inherit;
  font-size: 0.82rem;
  font-weight: 700;
  letter-spacing: 0.03em;

  cursor: pointer;
  transition:
    color 0.2s ease,
    transform 0.2s ease;
}

.menu-fullscreen-link:hover {
  color: #351b0f;
  transform: translateY(-1px);
}

.menu-fullscreen-link span {
  display: inline-block;
  font-size: 1rem;
  line-height: 1;
  animation: fullscreenHint 5s ease-in-out infinite;
}

.menu-category-button:hover {
  border-color: #a87f45;
  background: rgba(138, 106, 50, 0.08);
}

.menu-category-button.active {
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
  overflow: auto;

  /*
   * The component handles swipe, pinch, and dragging itself.
   * Prevent the browser from claiming the gesture first.
   */
  touch-action: none;

  overscroll-behavior: contain;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: thin;
}

.vintage-menu.is-fullscreen .menu-page-sheet {
  min-width: 100%;
  min-height: 100%;
  transform-origin: top left;
}

.menu-book.can-open-fullscreen {
  cursor: zoom-in;
}

.menu-book.can-open-fullscreen:focus-visible {
  outline: 3px solid #d6b98c;
  outline-offset: 4px;
}

.menu-book.is-zoomed .menu-image {
  pointer-events: none;
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
  cursor: default;
  touch-action: pan-y;
  overscroll-behavior-x: contain;
  user-select: none;
  -webkit-user-select: none;
  -webkit-touch-callout: none;
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
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;

  margin-bottom: 0.5rem;
}

.menu-zoom-controls {
  display: flex;
  align-items: center;
  gap: 0.35rem;
}

.zoom-button {
  justify-content: center;
  min-width: 2.5rem;
  min-height: 2.5rem;
  padding: 0.35rem;
}

.zoom-level {
  justify-content: center;
  min-width: 4rem;
}

.fullscreen-button:disabled {
  opacity: 0.35;
  cursor: default;
  transform: none;
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

.fullscreen-button:hover:not(:disabled) {
  background: rgba(255, 250, 241, 0.16);
  transform: translateY(-1px);
}

.fullscreen-button span {
  font-size: 1.1rem;
  line-height: 1;
  display: inline-block;
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
}

.menu-image {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: contain;
  position: relative;
  z-index: 1;
  -webkit-user-drag: none;
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
}

/* Backward */

.turn-backward-enter-active,
.turn-backward-enter-to {
  z-index: 3;
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
  user-select: none;
}

/* =========================================================
   MOBILE
   ========================================================= */

@media (max-width: 700px) {
  .menu-page {
    padding-inline: 0;
  }

  .menu-destination {
    position: sticky;
    top: 0;

    align-items: stretch;
    gap: 0.35rem;

    max-height: none;
    padding: 0.35rem 0.5rem;
    overflow: visible;
  }

  .menu-category-buttons {
    display: flex;
    flex-direction: column;
    align-items: stretch;
    gap: 0.1rem;

    max-height: 180px;
    overflow-x: hidden;
    overflow-y: auto;
  }

  .menu-category-button {
    width: 100%;
    padding: 0.55rem 0.75rem;

    text-align: center;
    font-size: 0.75rem;
    line-height: 1.2;
  }

  .menu-category-button.active {
    border-radius: 6px;
  }

  .menu-fullscreen-link {
    width: 100%;
    min-height: 2.5rem;
    padding: 0.5rem 0.75rem;

    border: 1px solid rgba(138, 106, 50, 0.4);
    border-radius: 5px;

    background: rgba(138, 106, 50, 0.07);
  }

  .menu-panel {
    padding-top: 0.25rem;
  }

  .vintage-menu {
    width: 100%;
    max-width: 540px;
    margin: auto;
  }

  .vintage-menu.is-fullscreen .menu-toolbar {
    gap: 0.35rem;
  }

  .vintage-menu.is-fullscreen .menu-zoom-controls {
    gap: 0.2rem;
  }

  .vintage-menu.is-fullscreen .zoom-button {
    min-width: 2.35rem;
    min-height: 2.35rem;
  }

  .vintage-menu.is-fullscreen .zoom-level {
    min-width: 3.5rem;
    padding-inline: 0.4rem;
  }

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

  .vintage-menu:not(.is-fullscreen) .menu-controls {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
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

  .page-control {
    padding: 0.5rem;
    font-size: 0.82rem;
  }

  .page-number {
    order: -1;
    font-size: 0.7rem;
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

  .menu-fullscreen-link span {
    animation: none;
  }
}

/* Animation */

@keyframes fullscreenHint {

  0%,
  82%,
  100% {
    transform: scale(1);
  }

  86% {
    transform: scale(1.18);
  }

  90% {
    transform: scale(0.84);
  }

  94% {
    transform: scale(1.12);
  }

  98% {
    transform: scale(1);
  }
}
</style>