<template>
  <div>
    <h1 class="news
    ">DINING ROOM NOW OPEN!!</h1>
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
  color: #fff;
  text-shadow: 0 0 10px #00ffff, 0 0 20px #00ffff, 0 0 20px black;
  animation: blink 1s infinite;

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