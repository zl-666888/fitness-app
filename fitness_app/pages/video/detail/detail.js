const videoApi = require('../../../api/video')

Page({
  data: {
    video: null,
    favorited: false
  },
  async onLoad(options) {
    try {
      const video = await videoApi.getVideoDetail(options.id)
      this.setData({ video })
    } catch (e) {
      console.error('获取视频详情失败', e)
      wx.showToast({ title: '获取视频详情失败', icon: 'none' })
    }
  },
  async toggleFav() {
    try {
      const res = await videoApi.toggleFavorite(this.data.video.id)
      this.setData({ favorited: res.favorited })
      wx.showToast({ title: res.favorited ? '已收藏' : '已取消', icon: 'none' })
    } catch (e) {
      console.error('收藏操作失败', e)
      wx.showToast({ title: '操作失败', icon: 'none' })
    }
  },
  onPlay() {
    console.log('视频开始播放')
  },
  onVideoError(e) {
    console.error('视频播放出错', e)
    wx.showToast({ title: '视频加载失败，请重试', icon: 'none' })
  }
})
