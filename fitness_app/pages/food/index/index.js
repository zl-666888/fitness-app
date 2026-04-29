const foodApi = require('../../../api/food')

Page({
  data: {
    foods: [],
    page: 1, size: 10, hasMore: true, loading: false,
    category: '', keyword: '',
    categories: ['主食谷物','肉类蛋类','蔬菜菌藻','水果','奶制品','豆类坚果','饮品','零食甜点','调味品']
  },
  onShow() {
    this.setData({ foods: [], page: 1, hasMore: true })
    this.loadFoods()
  },
  async loadFoods() {
    if (!this.data.hasMore || this.data.loading) return
    this.setData({ loading: true })
    try {
      const params = { page: this.data.page, size: this.data.size }
      if (this.data.category) params.category = this.data.category
      const res = this.data.keyword 
        ? await foodApi.searchFoods({ page: this.data.page, size: this.data.size, keyword: this.data.keyword })
        : await foodApi.getFoodList(params)
      this.setData({
        foods: this.data.page === 1 ? res.records : [...this.data.foods, ...res.records],
        hasMore: res.records.length >= this.data.size,
        page: this.data.page + 1
      })
    } catch (e) {
      console.error('加载食物数据失败', e)
      wx.showToast({ title: '加载食物数据失败，请重试', icon: 'none' })
      this.setData({ hasMore: false })
    } finally { this.setData({ loading: false }) }
  },
  onSearchInput(e) {
    this.setData({ keyword: e.detail.value })
    if (!e.detail.value) { this.setData({ foods: [], page: 1, hasMore: true }); this.loadFoods() }
  },
  onSearch() { this.setData({ foods: [], page: 1, hasMore: true }); this.loadFoods() },
  onCategoryTap(e) { this.setData({ category: e.currentTarget.dataset.cat, foods: [], page: 1, hasMore: true }); this.loadFoods() },
  goDetail(e) { wx.navigateTo({ url: `/pages/food/detail/detail?id=${e.detail.food.id}` }) },
  goCalculator() { wx.navigateTo({ url: '/pages/food/calculator/calculator' }) },
  onLoadMore() { this.loadFoods() },
  onPullDownRefresh() {
    this.setData({ foods: [], page: 1, hasMore: true })
    this.loadFoods().then(() => wx.stopPullDownRefresh())
  },
  onReachBottom() { this.loadFoods() }
})
