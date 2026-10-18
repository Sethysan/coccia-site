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
import AdminUsersView
  from '@/views/admin/AdminUsersView.vue'
import AdminRecipesView
  from '../views/admin/AdminRecipesView.vue'
import AdminMenuView
  from '../views/admin/AdminMenuView.vue'
import AdminPurchasingVendorsView
  from '../views/admin/AdminPurchasingVendorsView.vue'
import AdminPurchasingOrdersView
  from '../views/admin/AdminPurchasingOrdersView.vue'
import AdminPurchasingProductsView
  from '../views/admin/AdminPurchasingProductsView.vue'
import AdminPurchasingDraftOrdersView
  from '../views/admin/AdminPurchasingDraftOrdersView.vue'
import AdminPurchasingReceivingView
  from '../views/admin/AdminPurchasingReceivingView.vue'
import AdminPurchasingCompletedOrdersView
  from '../views/admin/AdminPurchasingCompletedOrdersView.vue'
import AdminLayout
  from '../layouts/AdminLayout.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: HomeView },
    { path: '/menu', component: MenuView },
    { path: '/about', component: AboutView },
    { path: '/admin/login', component: AdminLoginView },
    {
      path: '/admin',
      component: AdminLayout,
      meta: { requiresAuth: true },

      children: [
        {
          path: '',
          component: AdminDashboardView
        },
        {
          path: 'weekly-offerings',
          component: AdminWeeklyOfferingsView
        },
        {
          path: 'weekly-offerings/:id',
          component: AdminWeeklyOfferingDetailView
        },
        {
          path: 'announcements',
          component: AdminAnnouncementsView
        },
        {
          path: 'hours',
          component: AdminHoursView
        },
        {
          path: 'recipes',
          component: AdminRecipesView
        },
        {
          path: 'menu',
          component: AdminMenuView
        },
        {
          path: 'purchasing/vendors',
          component: AdminPurchasingVendorsView
        },
        {
          path: 'purchasing/products',
          component: AdminPurchasingProductsView
        },
        {
          path: 'purchasing/orders',
          name: 'purchasing-build-order',
          component: AdminPurchasingOrdersView
        },
        {
          path: 'purchasing/orders/drafts',
          name: 'purchasing-draft-orders',
          component: AdminPurchasingDraftOrdersView
        },
        {
          path: 'purchasing/orders/receiving',
          name: 'purchasing-receiving',
          component: AdminPurchasingReceivingView
        },
        {
          path: 'purchasing/orders/completed',
          name: 'purchasing-completed-orders',
          component: AdminPurchasingCompletedOrdersView
        },
        {
          path: 'users',
          name: 'admin-users',
          component: AdminUsersView,
          meta: {
            requiresAdmin: true
          }
        }
      ]
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
  const auth = useAuthStore()

  if (!to.path.startsWith('/admin')) {
    loading.start()
  }

  if (to.meta.requiresAuth) {
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

  if (to.meta.requiresAdmin && !auth.isAdmin) {
    return {
      path: '/admin'
    }
  }

  return true
})

export default router