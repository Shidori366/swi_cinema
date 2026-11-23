import { createRouter, createWebHistory } from 'vue-router'
import Home from './views/Home.vue'
import Reservations from './views/Reservations.vue'
import Admin from './views/Admin.vue'
import ScreeningDetail from './views/ScreeningDetail.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/reservations', component: Reservations },
  { path: '/admin', component: Admin },
  { path: '/screenings/:id', component: ScreeningDetail },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
