<template>
  <SiteHeader />

  <main class="content">
    <RouterView />
  </main>
</template>

<script>
import SiteHeader from './components/SiteHeader.vue'
</script>

<style scoped>

.content {
  padding-top: 90px;
}

.page {
  min-height: 100vh;
  font-family: Arial, sans-serif;
  background: #f7f1e8;
  color: #2b1a12;
}

.hero {
  padding: 80px 24px;
  text-align: center;
}

h1 {
  font-size: 56px;
  margin-bottom: 16px;
}

.tagline {
  font-size: 22px;
}

.info {
  margin-top: 24px;
  font-weight: bold;
}
</style>