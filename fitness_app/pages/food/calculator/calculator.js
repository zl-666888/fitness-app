const foodApi = require('../../../api/food')

Page({
  data: {
    foodId: '', amount: 100,
    foods: [], keyword: '', result: null,
    showPicker: false
  },
  onSearchInput(e) { this.setData({ keyword: e.detail.value }) },
  onSearch() {
    if (!this.data.keyword) return
    foodApi.searchFoods({ page: 1, size: 20, keyword: this.data.keyword }).then(res => {
      this.setData({ foods: res.records, showPicker: true })
    })
  },
  selectFood(e) {
    const food = e.currentTarget.dataset.food
    this.setData({ foodId: food.id, showPicker: false })
    foodApi.calculate({ foodId: food.id, amount: this.data.amount }).then(res => {
      this.setData({ result: { ...res, foodName: food.name } })
    })
  },
  onAmountChange(e) {
    this.setData({ amount: e.detail.value })
    if (this.data.foodId) {
      foodApi.calculate({ foodId: this.data.foodId, amount: e.detail.value }).then(res => {
        this.setData({ result: res })
      })
    }
  }
})
