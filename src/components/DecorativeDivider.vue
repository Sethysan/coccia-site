<template>
  <div
    class="decorative-divider"
    :class="{
      'decorative-divider-bottom': bottom
    }"
    aria-hidden="true"
  >
    <span></span>

    <span class="decorative-divider-mark">
      ◆
    </span>

    <span></span>
  </div>
</template>

<script setup>
defineProps({
  bottom: {
    type: Boolean,
    default: false
  }
})
</script>

<style scoped>
.decorative-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.9rem;

  width: min(100%, 560px);
  margin: 0 auto 2.5rem;

  color: var(--bronze-bold);
}

.decorative-divider-bottom {
  margin-top: 2.5rem;
  margin-bottom: 0;
}

.decorative-divider > span:not(.decorative-divider-mark) {
  width: 100%;
  height: 1px;

  background: linear-gradient(
    to right,
    transparent,
    var(--bronze-color)
  );
}

.decorative-divider > span:last-child {
  background: linear-gradient(
    to left,
    transparent,
    var(--bronze-color)
  );
}

.decorative-divider-mark {
  flex: 0 0 auto;

  font-size: 0.65rem;
}
</style>