<template>
   <main class="menu-page">
     <nav class="menu-nav">
       <button @click="scrollToSection('starters')">Starters</button>
       <button @click="scrollToSection('pizza')">Pizza & Pasta</button>
       <button @click="scrollToSection('favorites')">House Favorites</button>
       <button @click="scrollToSection('sandwiches')">Sandwiches</button>
       <button @click="scrollToSection('desserts')">Desserts and Drinks</button>
     </nav>
    <section class="menu-panel" ref="menuPanel">

      <section id="starters" class="menu-section">
        <img src="../assets/menu/starters-salads.png" alt="Starters and salads menu" />
      </section>

      <section id="pizza" class="menu-section">
        <img src="../assets/menu/pizza-pasta.png" alt="Pizza and pasta menu" />
      </section>

      <section id="favorites" class="menu-section">
        <img src="../assets/menu/house-favorites.png" alt="House favorites menu" />
      </section>

      <section id="sandwiches" class="menu-section">
        <img src="../assets/menu/sandwiches.png" alt="Sandwiches menu" />
      </section>

      <section id="desserts" class="menu-section">
        <img src="../assets/menu/desserts-drinks.png" alt="Desserts and drinks menu" />
      </section>
    </section>
  </main>
</template>

<script setup>
import pizza from '@/assets/menu/pizza-pasta.png'
import favorites from '@/assets/menu/house-favorites.png'
import starters from '@/assets/menu/starters-salads.png'
import sandwiches from '@/assets/menu/sandwiches.png'
import desserts from '@/assets/menu/desserts-drinks.png'
import { ref } from 'vue'

const menuPanel = ref(null)

function scrollToSection(id) {
  const container = menuPanel.value
  const section = document.getElementById(id)

  const top =
    section.getBoundingClientRect().top -
    container.getBoundingClientRect().top +
    container.scrollTop

  container.scrollTo({
    top,
    behavior: 'smooth'
  })
}

</script>

<style scoped>

.menu-page{
    display:flex;
    flex-direction:column;
    height:100vh;
    align-items: center;
}

.menu-nav{
    flex-shrink:0;
}

.menu-panel{
    flex:1;
    overflow-y:auto;
}

.menu-page {
  min-height: 90vh;
  /* background-image: url("../assets/menu/story.jpg"); */
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  /* padding: 110px 1rem 2rem; */
}

.menu-panel {
  flex: 1;
  overflow-y: auto;
  max-width: 900px;
  width: 70%;
  padding-bottom: 30vh;   /* or 400px */
}

.menu-section:last-child {
  padding-bottom: 20vh;
}

.menu-nav {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  gap: 0.75rem;
  overflow-x: auto;
  padding: 0.75rem;
  background: rgba(255, 248, 235, 0.96);
  border-bottom: 1px solid #d6b98c;
}

.menu-nav a {
  white-space: nowrap;
  text-decoration: none;
  color: #4a2414;
  font-weight: bold;
}

.menu-section {
  margin-bottom: 2rem;
}

.menu-section img {
  width: 100%;
  display: block;
  border-radius: 12px;
}
</style>