import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import dragScroll from './directives/dragScroll.js'

import './style.css'
import 'bootstrap-icons/font/bootstrap-icons.css'

const app = createApp(App)

app.use(router)
app.use(createPinia())

app.directive('drag-scroll', dragScroll)

app.mount('#app')