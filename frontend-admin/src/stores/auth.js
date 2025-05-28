import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const userInfo = ref(null)

  const isLoggedIn = computed(() => !!token.value)

  const login = async (credentials) => {
    try {
      const response = await api.post('/auth/login', credentials)
      const { token: authToken, username, role } = response.data
      
      if (role !== 'ADMIN') {
        throw new Error('权限不足，需要管理员账号')
      }
      
      token.value = authToken
      userInfo.value = { username, role }
      localStorage.setItem('admin_token', authToken)
      
      return response.data
    } catch (error) {
      throw error
    }
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('admin_token')
  }

  const getCurrentUser = async () => {
    try {
      const response = await api.get('/auth/me')
      userInfo.value = response.data
      return response.data
    } catch (error) {
      logout()
      throw error
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    login,
    logout,
    getCurrentUser
  }
})