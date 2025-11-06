import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import TicketQuery from '../views/TicketQuery.vue'
import BuyTicket from '../views/BuyTicket.vue'
import MyOrders from '../views/MyOrders.vue'
import RouteQuery from '../views/RouteQuery.vue'
import TrainManagement from '../views/TrainManagement.vue'
import TicketManagement from '../views/TicketManagement.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/ticket-query',
    name: 'TicketQuery',
    component: TicketQuery
  },
  {
    path: '/buy-ticket',
    name: 'BuyTicket',
    component: BuyTicket
  },
  {
    path: '/my-orders',
    name: 'MyOrders',
    component: MyOrders
  },
  {
    path: '/route-query',
    name: 'RouteQuery',
    component: RouteQuery
  },
  {
    path: '/train-management',
    name: 'TrainManagement',
    component: TrainManagement
  },
  {
    path: '/ticket-management',
    name: 'TicketManagement',
    component: TicketManagement
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const sessionId = localStorage.getItem('sessionId')
  const publicPages = ['/login', '/register']
  const authRequired = !publicPages.includes(to.path)
  
  if (authRequired && !sessionId) {
    next('/login')
  } else {
    next()
  }
})

export default router

