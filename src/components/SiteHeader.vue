<template>
  <header class="header">
    <nav class="nav">
      <RouterLink to="/">Home</RouterLink>
      <RouterLink to="/menu">Menu</RouterLink>
      <RouterLink to="/hours">Hours</RouterLink>
      <RouterLink to="/about">About</RouterLink>
    </nav>
  </header>
</template>

<script setup>
import { RouterLink } from 'vue-router'
</script>

<style scoped>
.header {
  position: sticky;
  top: 0;
  z-index: 1000;
  background: rgb(164, 154, 154);
  padding: 1rem;
}

.nav {
  display: flex;
  justify-content: space-evenly;
  align-items: center;
}

.nav a {
  text-decoration: none;
  font-weight: bold;
  color: #333;
  transition: color 0.3s ease;
}

.nav a:hover {
  color: #b22222;
}
</style>