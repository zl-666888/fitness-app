const authApi = require('../../../api/auth')
const app = getApp()

Page({
  data: {
    username: '',
    password: ''
  },
  onLoad() {
    if (wx.getStorageSync('token')) {
      wx.switchTab({ url: '/pages/checkin/index/index' })
    }
  },
  onInput(e) {
    const { field } = e.currentTarget.dataset
    this.setData({ [field]: e.detail.value })
  },
  handleLogin() {
    const { username, password } = this.data
    if (!username || !password) {
      wx.showToast({ title: '请填写完整', icon: 'none' })
      return
    }
    authApi.login({ username, password }).then(res => {
      app.setLogin(res.accessToken, res.refreshToken, res.user)
      wx.showToast({ title: '登录成功', icon: 'success' })
      setTimeout(() => wx.switchTab({ url: '/pages/checkin/index/index' }), 500)
    })
  },
  goRegister() {
    wx.navigateTo({ url: '/pages/auth/register/register' })
  }
})
