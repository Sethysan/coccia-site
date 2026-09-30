import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import dragScroll from './directives/dragScroll.js'

import { useAuthStore } from '@/stores/authStore'
import { setUnauthorizedHandler } from '@/api/apiClient'

import './assets/style.css'
import './assets/admin.css'
import 'bootstrap-icons/font/bootstrap-icons.css'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

const auth = useAuthStore(pinia)

setUnauthorizedHandler(() => {
    auth.clearSession()

    if (router.currentRoute.value.path !== '/admin/login') {
        router.replace({
            path: '/admin/login',
            query: {
                redirect: router.currentRoute.value.fullPath
            }
        })
    }
})

app.directive('drag-scroll', dragScroll)

app.mount('#app')