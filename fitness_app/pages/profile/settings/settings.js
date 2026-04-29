const app = getApp()

Page({
  data: {},
  logout() {
    wx.showModal({ title: '退出登录', content: '确定要退出登录吗？', success: (res) => {
      if (!res.confirm) return
      app.logout()
      wx.reLaunch({ url: '/pages/auth/login/login' })
    }})
  },
  clearCache() {
    wx.showModal({ title: '清除缓存', content: '确定要清除本地缓存吗？', success: (res) => {
      if (!res.confirm) return
      wx.clearStorageSync()
      wx.showToast({ title: '已清除', icon: 'success' })
    }})
  }
})
