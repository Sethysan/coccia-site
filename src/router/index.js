import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '../views/HomeView.vue'
import MenuView from '../views/MenuView.vue'
import AboutView from '../views/AboutView.vue'
import AdminLoginView from '../views/admin/AdminLoginView.vue'
import AdminDashboardView from '../views/admin/AdminDashboardView.vue'
import AdminWeeklyOfferingsView from '../views/admin/AdminWeeklyOfferingsView.vue'
import { useLoadingStore } from "@/stores/loadingStore"
import { useAuthStore } from '@/stores/authStore'
import AdminWeeklyOfferingDetailView
  from '../views/admin/AdminWeeklyOfferingDetailView.vue'
import AdminAnnouncementsView
  from '../views/admin/AdminAnnouncementsView.vue'
import AdminHoursView
  from '../views/admin/AdminHoursView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: HomeView },
    { path: '/menu', component: MenuView },
    { path: '/about', component: AboutView },
    { path: '/admin/login', component: AdminLoginView },
    {
      path: '/admin',
      component: AdminDashboardView,
      meta: { requiresAuth: true }
    },
    {
      path: '/admin/weekly-offerings',
      component: AdminWeeklyOfferingsView, meta: { requiresAuth: true }
    },
    {
      path: '/admin/weekly-offerings/:id',
      component: AdminWeeklyOfferingDetailView, meta: { requiresAuth: true }
    },
    {
      path: '/admin/announcements',
      component: AdminAnnouncementsView,
      meta: { requiresAuth: true }
    },
    {
      path: '/admin/hours',
      component: AdminHoursView,
      meta: { requiresAuth: true }
    }
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


router.beforeEach(async (to) => {
  const loading = useLoadingStore()

  await loading.play()

  if (to.meta.requiresAuth) {
    const auth = useAuthStore()

    if (!auth.sessionChecked) {
      await auth.checkSession()
    }

    if (!auth.authenticated) {
      return {
        path: '/admin/login',
        query: {
          redirect: to.fullPath
        }
      }
    }
  }

  return true
})
export default router