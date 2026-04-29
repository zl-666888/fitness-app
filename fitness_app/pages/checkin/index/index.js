const checkinApi = require('../../../api/checkin')
const userApi = require('../../../api/user')

Page({
  data: {
    todayCheckin: null,
    stats: { totalDays: 0, currentMonthDays: 0, consecutiveDays: 0 },
    showCheckinSheet: false,
    checkinForm: { type: '跑步', duration: 30, intensity: 'medium', content: '' },
    types: ['跑步', '快走', '骑行', '瑜伽', '力量训练', 'HIIT', '游泳', '球类', '普拉提', '其他'],
    intensities: [
      { value: 'low', label: '低强度' },
      { value: 'medium', label: '中等' },
      { value: 'high', label: '高强度' }
    ]
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
      const [checkin, stats] = await Promise.all([
        checkinApi.getTodayCheckin().catch(() => null),
        checkinApi.getStats().catch(() => null)
      ])
      this.setData({
        todayCheckin: checkin,
        stats: stats || this.data.stats
      })
    } catch {}
  },
  openCheckin() {
    if (this.data.todayCheckin) {
      wx.showToast({ title: '今日已打卡', icon: 'none' })
      return
    }
    this.setData({ showCheckinSheet: true })
  },
  closeSheet() { this.setData({ showCheckinSheet: false }) },
  preventClose() {},
  onTypeSelect(e) { this.setData({ 'checkinForm.type': e.currentTarget.dataset.type }) },
  onDurChange(e) { this.setData({ 'checkinForm.duration': e.detail.value }) },
  onIntChange(e) { this.setData({ 'checkinForm.intensity': e.currentTarget.dataset.val }) },
  onContentInput(e) { this.setData({ 'checkinForm.content': e.detail.value }) },
  async submitCheckin() {
    const now = new Date()
    const date = `${now.getFullYear()}-${String(now.getMonth()+1).padStart(2,'0')}-${String(now.getDate()).padStart(2,'0')}`
    const time = `${String(now.getHours()).padStart(2,'0')}:${String(now.getMinutes()).padStart(2,'0')}:${String(now.getSeconds()).padStart(2,'0')}`
    const { checkinForm } = this.data
    await checkinApi.createCheckin({
      checkinDate: date,
      checkinTime: time,
      type: checkinForm.type,
      duration: checkinForm.duration,
      intensity: checkinForm.intensity,
      content: checkinForm.content
    })
    this.setData({ showCheckinSheet: false })
    wx.showToast({ title: '打卡成功！🎉', icon: 'none' })
    this.loadData()
  },
  goCalendar() { wx.navigateTo({ url: '/pages/checkin/calendar/calendar' }) },
  goStats() { wx.navigateTo({ url: '/pages/profile/stats/stats' }) }
})
