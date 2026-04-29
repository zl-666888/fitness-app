Page({
  data: {
    checkin: null
  },
  onLoad(options) {
    this.setData({ checkin: JSON.parse(decodeURIComponent(options.data)) })
  },
  onDelete() {
    wx.showModal({ title: '确认删除', content: '确定要删除这条打卡记录吗？', success: (res) => {
      if (!res.confirm) return
      const checkinApi = require('../../../api/checkin')
      checkinApi.deleteCheckin(this.data.checkin.id).then(() => {
        wx.showToast({ title: '已删除', icon: 'success' })
        setTimeout(() => wx.navigateBack(), 1000)
      })
    }})
  }
})
