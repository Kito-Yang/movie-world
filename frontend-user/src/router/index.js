import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Movies from '@/views/Movies.vue'
import Favorites from '@/views/Favorites.vue'
import Profile from '@/views/Profile.vue'
import Feedback from '@/views/Feedback.vue'
import SmartQA from '@/views/SmartQA.vue'

const routes = [
  {
    path: '/',
    redirect: '/movies'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: {
      requiresGuest: true // 只有未登录用户可以访问
    }
  },
  {
    path: '/movies',
    name: 'Movies',
    component: Movies,
    meta: {
      requiresAuth: true // 需要登录才能访问
    }
  },
  {
    path: '/smart-qa',
    name: 'SmartQA',
    component: SmartQA, 
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: Favorites,
    meta: {
      requiresAuth: true
    }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: {
      requiresAuth: true
    }
  },
  {
    path: '/feedback',
    name: 'Feedback',
    component: Feedback,
    meta: {
      requiresAuth: true
    }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const isAuthenticated = !!token
  
  // 检查是否需要登录
  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
    return
  }
  
  // 检查是否已登录用户访问登录页
  if (to.meta.requiresGuest && isAuthenticated) {
    next('/movies')
    return
  }
  
  next()
})

export default router