import { defineStore } from 'pinia'
import { login as loginApi, logout as logoutApi } from '@/api/auth'
import { parseToken } from '@/utils/jwt'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('admin_token') || '',
    refreshToken: localStorage.getItem('admin_refresh_token') || '',
    role: localStorage.getItem('admin_role') || '',
    adminInfo: null
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    isSuper: (state) => state.role === 'super'
  },
  actions: {
    async login(username, password) {
      const res = await loginApi({ username, password })
      const { accessToken, refreshToken, user } = res.data
      this.token = accessToken
      this.refreshToken = refreshToken
      this.adminInfo = user

      const payload = parseToken(accessToken)
      this.role = payload.role

      localStorage.setItem('admin_token', accessToken)
      localStorage.setItem('admin_refresh_token', refreshToken)
      localStorage.setItem('admin_role', payload.role)
    },
    logout() {
      this.token = ''
      this.refreshToken = ''
      this.role = ''
      this.adminInfo = null
      localStorage.removeItem('admin_token')
      localStorage.removeItem('admin_refresh_token')
      localStorage.removeItem('admin_role')
    }
  }
})
