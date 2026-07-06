<template>
  <div id="about">
    <div class="header">
      <h2 >About</h2>
    </div>
    <div class="body">
      <p>{{ cocciaStory }}</p>
    </div>
  </div>
</template>

<script setup>
import { cocciaStory } from '@/content/cocciaStory'
</script>

<style scoped>


#about {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.header {
  font-size: 3em ;
}

.body{
  color: rgb(23, 22, 22);
  background-color: #ed962cdf;;
}
</style>