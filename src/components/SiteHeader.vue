<template>
  <header class="header">
    <RouterLink to="/" class="logo-link">
      <img src="@/assets/hero-logo.jpg" alt="Coccia House logo" class="logo" />
    </RouterLink>

    <nav class="nav">
      <RouterLink to="/">Home</RouterLink>
      <RouterLink to="/menu">Menu</RouterLink>
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
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 90px;
  padding: 0.75rem 1.5rem;
  background-image:
  linear-gradient(rgba(0, 0, 0, 0.55), rgba(0, 0, 0, 0.55)),
  url('@/assets/dinner.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  box-shadow: 0 8px 24px rgb(0, 0, 0, 0.35);
}

.logo {
  height: 70px;
  width: auto;
  display: block;
}

.nav {
  flex: 1;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-evenly;
  align-items: center;
  margin-left: 2rem;
}

.nav a {
  font-family: system-ui, 'Segoe UI', Roboto, sans-serif;
  text-decoration: none;
  font-weight: bold;
  color: white;

  background-color: #ad5915;
  padding: 0.65rem 1.4rem;
  border-radius: 4px;

  transition: all 0.3s ease;
}

.nav a:hover {
  background-color: #812a04;
  transform: translateY(-2px);
}
</style>