import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '../views/HomeView.vue'
import MenuView from '../views/MenuView.vue'
import HoursView from '../views/HoursView.vue'
import AboutView from '../views/AboutView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: HomeView },
    { path: '/menu', component: MenuView },
    { path: '/hours', component: HoursView },
    { path: '/about', component: AboutView }
  ]
})

export default router