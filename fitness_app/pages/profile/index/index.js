const userApi = require('../../../api/user')
const checkinApi = require('../../../api/checkin')
const app = getApp()

Page({
  data: {
    user: null,
    stats: { totalDays: 0, consecutiveDays: 0, currentMonthDays: 0 }
  },
  onShow() {
    if (!wx.getStorageSync('token')) {
      wx.reLaunch({ url: '/pages/auth/login/login' })
      return
    }
    this.loadData()
  },
  async loadData() {
    try {
      const [user, stats] = await Promise.all([
        userApi.getProfile(),
        checkinApi.getStats().catch(() => null)
      ])
      this.setData({ user, stats: stats || this.data.stats })
    } catch {}
  },
  goInfo() { wx.navigateTo({ url: '/pages/profile/info/info' }) },
  goGoal() { wx.navigateTo({ url: '/pages/profile/goal/goal' }) },
  goStats() { wx.navigateTo({ url: '/pages/profile/stats/stats' }) },
  goFavorites() { wx.navigateTo({ url: '/pages/profile/favorites/favorites' }) },
  goFollowing() { wx.navigateTo({ url: '/pages/profile/following/following' }) },
  goSettings() { wx.navigateTo({ url: '/pages/profile/settings/settings' }) }
})
