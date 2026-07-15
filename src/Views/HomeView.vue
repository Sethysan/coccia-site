<template>
  <div>
    <h1 class="news
    ">NOW OPEN FOR DINING IN!!</h1>
  </div>
  <div>
  <h1>Authentic Italian Cuisine</h1>
  <p>True to their original recipes...</p>
  </div>
</template>

<style scoped>
.news {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #d8d4a0;
  text-shadow: 0 0 10px #666120, 0 0 20px #ffd900b5, 0 0 20px black;
  animation: blink 1.85s infinite;

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