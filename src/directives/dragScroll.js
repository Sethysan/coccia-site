// src/directives/dragScroll.js

const DRAG_STATE = Symbol('dragScroll')

function setCursor(el, enabled, dragging = false) {
  if (!enabled) {
    el.style.cursor = ''
    return
  }

  el.style.cursor = dragging ? 'grabbing' : 'grab'
}

function createDragState(el, binding) {
  const state = {
    enabled: Boolean(binding.value),
    isDragging: false,
    hasMoved: false,
    pointerId: null,
    startX: 0,
    startY: 0,
    startScrollLeft: 0,
    startScrollTop: 0,
    movementThreshold: 5,

    handlePointerDown(event) {
      if (!state.enabled) return

      // Only allow the primary mouse button.
      if (event.pointerType === 'mouse' && event.button !== 0) {
        return
      }

      state.isDragging = true
      state.hasMoved = false
      state.pointerId = event.pointerId

      state.startX = event.clientX
      state.startY = event.clientY
      state.startScrollLeft = el.scrollLeft
      state.startScrollTop = el.scrollTop

      el.setPointerCapture?.(event.pointerId)
      setCursor(el, true, true)
    },

    handlePointerMove(event) {
      if (
        !state.enabled ||
        !state.isDragging ||
        event.pointerId !== state.pointerId
      ) {
        return
      }

      const distanceX = event.clientX - state.startX
      const distanceY = event.clientY - state.startY

      if (
        Math.abs(distanceX) >= state.movementThreshold ||
        Math.abs(distanceY) >= state.movementThreshold
      ) {
        state.hasMoved = true
      }

      if (!state.hasMoved) return

      event.preventDefault()

      el.scrollLeft = state.startScrollLeft - distanceX
      el.scrollTop = state.startScrollTop - distanceY
    },

    handlePointerEnd(event) {
      if (!state.isDragging) return

      if (
        state.pointerId !== null &&
        event.pointerId !== state.pointerId
      ) {
        return
      }

      el.releasePointerCapture?.(event.pointerId)

      state.isDragging = false
      state.pointerId = null

      setCursor(el, state.enabled)
    },

    handleClick(event) {
      if (!state.hasMoved) return

      /*
       * Prevent a drag from also triggering the element's click
       * handler after the pointer is released.
       */
      event.preventDefault()
      event.stopPropagation()

      state.hasMoved = false
    }
  }

  return state
}

function addListeners(el, state) {
  el.addEventListener('pointerdown', state.handlePointerDown)
  el.addEventListener('pointermove', state.handlePointerMove)
  el.addEventListener('pointerup', state.handlePointerEnd)
  el.addEventListener('pointercancel', state.handlePointerEnd)
  el.addEventListener('lostpointercapture', state.handlePointerEnd)

  /*
   * Capture phase lets the directive suppress the click before
   * Vue's @click handler receives it.
   */
  el.addEventListener('click', state.handleClick, true)
}

function removeListeners(el, state) {
  el.removeEventListener('pointerdown', state.handlePointerDown)
  el.removeEventListener('pointermove', state.handlePointerMove)
  el.removeEventListener('pointerup', state.handlePointerEnd)
  el.removeEventListener('pointercancel', state.handlePointerEnd)
  el.removeEventListener(
    'lostpointercapture',
    state.handlePointerEnd
  )

  el.removeEventListener('click', state.handleClick, true)
}

export default {
  mounted(el, binding) {
    const state = createDragState(el, binding)

    el[DRAG_STATE] = state

    addListeners(el, state)
    setCursor(el, state.enabled)
  },

  updated(el, binding) {
    const state = el[DRAG_STATE]

    if (!state) return

    state.enabled = Boolean(binding.value)

    if (!state.enabled) {
      state.isDragging = false
      state.hasMoved = false
      state.pointerId = null
    }

    setCursor(el, state.enabled)
  },

  unmounted(el) {
    const state = el[DRAG_STATE]

    if (!state) return

    removeListeners(el, state)
    delete el[DRAG_STATE]
  }
}