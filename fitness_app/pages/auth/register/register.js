const authApi = require('../../../api/auth')
const app = getApp()

Page({
  data: {
    username: '',
    password: '',
    nickname: '',
    phone: ''
  },
  onInput(e) {
    const { field } = e.currentTarget.dataset
    this.setData({ [field]: e.detail.value })
  },
  handleRegister() {
    const { username, password, nickname, phone } = this.data
    if (!username || !password) {
      wx.showToast({ title: '请填写账号和密码', icon: 'none' })
      return
    }
    if (username.length < 3) {
      wx.showToast({ title: '用户名至少3个字符', icon: 'none' })
      return
    }
    if (password.length < 6) {
      wx.showToast({ title: '密码至少6个字符', icon: 'none' })
      return
    }
    authApi.register({ username, password, nickname, phone }).then(res => {
      app.setLogin(res.accessToken, res.refreshToken, res.user)
      wx.showToast({ title: '注册成功', icon: 'success' })
      setTimeout(() => wx.switchTab({ url: '/pages/checkin/index/index' }), 500)
    }).catch(() => {})
  },
  goLogin() {
    wx.navigateBack()
  }
})
