App({
  globalData: {
    userInfo: null,
    token: null,
    refreshToken: null,
    todayCheckin: null
  },

  onLaunch() {
    this.checkLogin()
  },

  checkLogin() {
    const token = wx.getStorageSync('token')
    const refreshToken = wx.getStorageSync('refreshToken')
    const userInfo = wx.getStorageSync('userInfo')
    if (token && userInfo) {
      this.globalData.token = token
      this.globalData.refreshToken = refreshToken
      this.globalData.userInfo = userInfo
    } else {
      this.globalData.token = null
      this.globalData.refreshToken = null
      this.globalData.userInfo = null
    }
  },

  setLogin(token, refreshToken, userInfo) {
    this.globalData.token = token
    this.globalData.refreshToken = refreshToken
    this.globalData.userInfo = userInfo
    wx.setStorageSync('token', token)
    wx.setStorageSync('refreshToken', refreshToken)
    wx.setStorageSync('userInfo', userInfo)
  },

  logout() {
    this.globalData.token = null
    this.globalData.refreshToken = null
    this.globalData.userInfo = null
    wx.removeStorageSync('token')
    wx.removeStorageSync('refreshToken')
    wx.removeStorageSync('userInfo')
  }
})
