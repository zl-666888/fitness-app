const socialApi = require('../../../api/social')

Page({
  data: {
    feedList: [],
    page: 1,
    size: 10,
    hasMore: true,
    loading: false
  },
  onShow() {
    this.setData({ feedList: [], page: 1, hasMore: true })
    this.loadFeed()
  },
  async loadFeed() {
    if (!this.data.hasMore || this.data.loading) return
    this.setData({ loading: true })
    try {
      const res = await socialApi.getFeed({ page: this.data.page, size: this.data.size })
      const records = res.records.map(r => ({
        ...r,
        intensityLabel: { low: '低强度', medium: '中等', high: '高强度' }[r.intensity] || ''
      }))
      this.setData({
        feedList: this.data.page === 1 ? records : [...this.data.feedList, ...records],
        hasMore: records.length >= this.data.size,
        page: this.data.page + 1
      })
    } finally { this.setData({ loading: false }) }
  },
  onLike(e) {
    const item = e.detail.item
    socialApi.toggleLike(item.checkinId).then(res => {
      const list = this.data.feedList.map(f => {
        if (f.checkinId === item.checkinId) {
          f.liked = res.liked
          f.likeCount += res.liked ? 1 : -1
        }
        return f
      })
      this.setData({ feedList: list })
    })
  },
  onComment(e) {
    const item = e.detail.item
    wx.showModal({ title: '写评论', editable: true, placeholderText: '说点什么...', success: (res) => {
      if (!res.confirm || !res.content) return
      socialApi.addComment(item.checkinId, { content: res.content }).then(() => {
        wx.showToast({ title: '评论成功', icon: 'none' })
        const list = this.data.feedList.map(f => {
          if (f.checkinId === item.checkinId) f.commentCount++
          return f
        })
        this.setData({ feedList: list })
      })
    }})
  },
  onLoadMore() { this.loadFeed() },
  onPullDownRefresh() {
    this.setData({ feedList: [], page: 1, hasMore: true })
    this.loadFeed().then(() => wx.stopPullDownRefresh())
  },
  onReachBottom() { this.loadFeed() }
})
