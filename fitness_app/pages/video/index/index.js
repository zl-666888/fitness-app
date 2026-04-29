const videoApi = require('../../../api/video')

Page({
  data: {
    videos: [],
    page: 1, size: 10, hasMore: true, loading: false,
    category: '',
    keyword: '',
    categories: ['胸部训练','背部训练','腿部训练','肩部训练','手臂训练','核心腹部','全身燃脂','瑜伽拉伸','热身放松'],
    difficulties: [
      { label: '全部', value: '' },
      { label: '入门', value: 'beginner' },
      { label: '中级', value: 'intermediate' },
      { label: '高级', value: 'advanced' }
    ],
    difficulty: ''
  },
  onShow() {
    this.setData({ videos: [], page: 1, hasMore: true, keyword: '' })
    this.loadVideos()
  },
  async loadVideos() {
    if (!this.data.hasMore || this.data.loading) return
    this.setData({ loading: true })
    try {
      const params = {
        page: this.data.page,
        size: this.data.size
      }
      if (this.data.category) params.category = this.data.category
      if (this.data.difficulty) params.difficulty = this.data.difficulty
      const res = this.data.keyword
        ? await videoApi.searchVideos({ ...params, keyword: this.data.keyword })
        : await videoApi.getVideoList(params)
      this.setData({
        videos: this.data.page === 1 ? res.records : [...this.data.videos, ...res.records],
        hasMore: res.records && res.records.length >= this.data.size,
        page: this.data.page + 1
      })
    } catch (e) {
      console.error('加载视频失败', e)
      wx.showToast({ title: '加载视频失败，请重试', icon: 'none' })
      this.setData({ hasMore: false })
    } finally {
      this.setData({ loading: false })
    }
  },
  onSearchInput(e) {
    this.setData({ keyword: e.detail.value })
    if (!e.detail.value) {
      this.setData({ videos: [], page: 1, hasMore: true })
      this.loadVideos()
    }
  },
  onSearch() {
    this.setData({ videos: [], page: 1, hasMore: true })
    this.loadVideos()
  },
  onCategoryTap(e) { this.setData({ category: e.currentTarget.dataset.cat, videos: [], page: 1, hasMore: true }); this.loadVideos() },
  onDifficultyTap(e) { this.setData({ difficulty: e.currentTarget.dataset.diff, videos: [], page: 1, hasMore: true }); this.loadVideos() },
  goDetail(e) { wx.navigateTo({ url: `/pages/video/detail/detail?id=${e.detail.video.id}` }) },
  onLoadMore() { this.loadVideos() },
  onPullDownRefresh() {
    this.setData({ videos: [], page: 1, hasMore: true })
    this.loadVideos().then(() => wx.stopPullDownRefresh())
  },
  onReachBottom() { this.loadVideos() }
})
