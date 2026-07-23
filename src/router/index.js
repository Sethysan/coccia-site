import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '../views/HomeView.vue'
import MenuView from '../views/MenuView.vue'
import AboutView from '../views/AboutView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: HomeView },
    { path: '/menu', component: MenuView },
    { path: '/about', component: AboutView }
  ],
  
  scrollBehavior(to, from, savedPosition) {
    // Browser back/forward buttons
    if (savedPosition) {
      return savedPosition
    }

    // Always start new pages at the top
    return {
      top: 0,
      left: 0,
      behavior: 'smooth'
    }
  }
})

export default router