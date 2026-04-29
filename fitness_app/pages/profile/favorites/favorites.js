const videoApi = require('../../../api/video')

Page({
  data: {
    videos: [],
    loading: false
  },
  onShow() {
    this.loadFavorites()
  },
  async loadFavorites() {
    this.setData({ loading: true })
    try {
      const res = await videoApi.getVideoList({ page: 1, size: 50 })
      this.setData({ videos: res.records })
    } finally { this.setData({ loading: false }) }
  },
  goDetail(e) {
    wx.navigateTo({ url: `/pages/video/detail/detail?id=${e.detail.video.id}` })
  }
})
