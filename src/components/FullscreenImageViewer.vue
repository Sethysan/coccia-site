<template>

  <Teleport to="body">

    <div v-if="open" class="image-viewer" role="dialog" aria-modal="true" :aria-label="alt">

      <div class="image-viewer-toolbar">

        <div class="image-viewer-zoom-controls">

          <button type="button" class="image-viewer-button" :disabled="zoomScale <= minimumZoom" aria-label="Zoom out"
            @click="zoomOut">
            −
          </button>

          <button type="button" class="image-viewer-button zoom-level" aria-label="Reset image zoom" @click="resetZoom">
            {{ Math.round(zoomScale * 100) }}%
          </button>

          <button type="button" class="image-viewer-button" :disabled="zoomScale >= maximumZoom" aria-label="Zoom in"
            @click="zoomIn">
            +
          </button>
        </div>

        <button type="button" class="image-viewer-button" @click="emit('close')">
          × Close
        </button>
      </div>

      <div ref="viewer" class="image-viewer-stage branded-scrollbar" :class="{ 'is-zoomed': zoomScale > minimumZoom }"
        v-drag-scroll="zoomScale > minimumZoom" @wheel="handleWheel" @touchstart="handleTouchStart"
        @touchmove="handleTouchMove" @touchend="handleTouchEnd" @touchcancel="handleTouchEnd">

        <div class="image-viewer-content" :style="imageStyle">
          <img :src="src" :alt="alt" draggable="false" />
        </div>

      </div>

      <p v-if="caption" class="image-viewer-caption">
        {{ caption }}
      </p>

    </div>
  </Teleport>
</template>

<script setup>
import {
  computed,
  nextTick,
  onBeforeUnmount,
  onMounted,
  ref,
  watch
} from 'vue'

const props = defineProps({
  open: {
    type: Boolean,
    required: true
  },
  src: {
    type: String,
    required: true
  },
  alt: {
    type: String,
    default: 'Menu item photo'
  },
  caption: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['close'])

const viewer = ref(null)

const minimumZoom = 1
const maximumZoom = 3
const zoomIncrement = 0.25
const wheelZoomIncrement = 0.15

const zoomScale = ref(minimumZoom)

const pinchStartDistance = ref(0)
const pinchStartScale = ref(minimumZoom)
const pinchStartContentX = ref(0)
const pinchStartContentY = ref(0)
const isPinching = ref(false)

const imageStyle = computed(() => {
  if (zoomScale.value <= minimumZoom) {
    return undefined
  }

  const scaledSize = `${zoomScale.value * 100}%`

  return {
    width: scaledSize,
    height: scaledSize
  }
})

function clampZoom(value) {
  return Math.min(
    maximumZoom,
    Math.max(minimumZoom, value)
  )
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

  viewer.value?.scrollTo({
    left: 0,
    top: 0
  })
}

function handleKeydown(event) {
  if (event.key === 'Escape' && props.open) {
    emit('close')
  }
}

async function handleWheel(event) {
  if (!props.open) return

  event.preventDefault()

  const container = viewer.value
  if (!container) return

  const oldZoom = zoomScale.value
  const direction = event.deltaY < 0 ? 1 : -1

  const newZoom = clampZoom(
    oldZoom + direction * wheelZoomIncrement
  )

  if (newZoom === oldZoom) return

  const rect = container.getBoundingClientRect()

  const mouseX = event.clientX - rect.left
  const mouseY = event.clientY - rect.top

  const contentX = container.scrollLeft + mouseX
  const contentY = container.scrollTop + mouseY

  const zoomRatio = newZoom / oldZoom

  zoomScale.value = newZoom

  await nextTick()

  container.scrollLeft =
    contentX * zoomRatio - mouseX

  container.scrollTop =
    contentY * zoomRatio - mouseY
}

function getTouchDistance(touches) {
  return Math.hypot(
    touches[1].clientX - touches[0].clientX,
    touches[1].clientY - touches[0].clientY
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

function handleTouchStart(event) {
  if (event.touches.length !== 2) return

  const container = viewer.value
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

  pinchStartContentX.value =
    container.scrollLeft + midpoint.x

  pinchStartContentY.value =
    container.scrollTop + midpoint.y
}

async function handleTouchMove(event) {
  if (
    !isPinching.value ||
    event.touches.length !== 2
  ) {
    return
  }

  event.preventDefault()

  const container = viewer.value
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

function handleTouchEnd(event) {
  if (event.touches.length >= 2) return

  isPinching.value = false
  pinchStartDistance.value = 0
  pinchStartContentX.value = 0
  pinchStartContentY.value = 0
}

watch(
  () => props.open,
  async (isOpen) => {
    document.body.style.overflow =
      isOpen ? 'hidden' : ''

    if (isOpen) {
      await resetZoom()
    }
  }
)

onMounted(() => {
  window.addEventListener('keydown', handleKeydown)
})

onBeforeUnmount(() => {
  window.removeEventListener('keydown', handleKeydown)
  document.body.style.overflow = ''
})
</script>

<style scoped>

.image-viewer {
  position: fixed;
  inset: 0;
  z-index: 2147483647;

  display: grid;
  grid-template-rows: auto minmax(0, 1fr) auto;

  width: 100vw;
  height: 100vh;
  height: 100dvh;

  padding:
    max(0.5rem, env(safe-area-inset-top)) max(0.5rem, env(safe-area-inset-right)) max(0.5rem, env(safe-area-inset-bottom)) max(0.5rem, env(safe-area-inset-left));

  background:
    radial-gradient(circle at center,
      #5a351f,
      #2d180f 65%,
      #160c08);
}

.image-viewer-toolbar {
  position: relative;
  z-index: 2;

  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.75rem;

  width: min(100%, 60rem);
  margin: 0 auto 0.5rem;
}

.image-viewer-zoom-controls {
  display: flex;
  align-items: center;
  gap: 0.35rem;
}

.image-viewer-button {
  min-height: 2.5rem;
  padding: 0.45rem 0.7rem;

  border: 1px solid rgba(214, 185, 140, 0.6);
  border-radius: 4px;

  background: rgba(255, 250, 241, 0.08);
  color: #fff2d7;

  font: inherit;
  font-weight: 700;

  cursor: pointer;
}

.image-viewer-button:hover:not(:disabled) {
  background: rgba(255, 250, 241, 0.16);
}

.image-viewer-button:disabled {
  opacity: 0.35;
  cursor: default;
}

.zoom-level {
  min-width: 4rem;
}

.image-viewer-caption {
  position: relative;
  z-index: 2;

  max-width: 60rem;
  margin: 0.5rem auto 0;
  padding: 0 0.5rem;

  color: #fff2d7;

  font-size: 0.85rem;
  font-style: italic;
  line-height: 1.4;
  text-align: center;
}

.image-viewer-stage {
  align-self: center;
  justify-self: center;

  width: 100%;
  height: 100%;

  overflow: auto;

  touch-action: none;
  overscroll-behavior: contain;
  -webkit-overflow-scrolling: touch;
}

.image-viewer-content {
  display: flex;
  align-items: center;
  justify-content: center;

  width: 100%;
  height: 100%;
  min-width: 100%;
  min-height: 100%;

  transform-origin: top left;
}

.image-viewer-content img {
  display: block;

  max-width: 100%;
  max-height: 100%;

  object-fit: contain;

  user-select: none;
  -webkit-user-drag: none;
}

.image-viewer-stage.is-zoomed .image-viewer-content {
  align-items: flex-start;
  justify-content: flex-start;
}

.image-viewer-stage.is-zoomed .image-viewer-content img {
  width: 100%;
  height: 100%;

  max-width: none;
  max-height: none;

  object-fit: contain;
}

@media (max-width: 700px) {
  .image-viewer {
    padding:
      max(0.35rem, env(safe-area-inset-top)) max(0.35rem, env(safe-area-inset-right)) max(0.35rem, env(safe-area-inset-bottom)) max(0.35rem, env(safe-area-inset-left));
  }

  .image-viewer-toolbar {
    gap: 0.35rem;
  }

  .image-viewer-zoom-controls {
    gap: 0.2rem;
  }

  .image-viewer-button {
    padding: 0.4rem 0.6rem;
    font-size: 0.78rem;
  }

  .zoom-level {
    min-width: 3.5rem;
  }
}
</style>