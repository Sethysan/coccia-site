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
  background:#a42e03;
  padding: 1rem;
  box-shadow: 0 40px 80px rgb(0, 0, 0);
}

.nav {
  display: flex;
  justify-content: space-evenly;
  align-items: center;
}

.nav a {
  font-family: system-ui, 'Segoe UI', Roboto, sans-serif;
  text-decoration: none;
  font-weight: bold;
  color: #ed962c;
  transition: color 0.3s ease;
}

.nav a:hover {
  color: black;
}
</style>