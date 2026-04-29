const foodApi = require('../../../api/food')

Page({
  data: {
    food: null,
    amount: 100,
    result: null
  },
  onLoad(options) {
    foodApi.getFoodDetail(options.id).then(food => {
      this.setData({ food })
      this.calc()
    })
  },
  onAmountChange(e) {
    this.setData({ amount: e.detail.value })
    this.calc()
  },
  async calc() {
    if (!this.data.food) return
    const res = await foodApi.calculate({ foodId: this.data.food.id, amount: this.data.amount })
    this.setData({ result: res })
  }
})
