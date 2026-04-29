const api = require('./request')

module.exports = {
  getFoodList: (params) => api.get('/foods', params),
  searchFoods: (params) => api.get('/foods/search', params),
  getFoodDetail: (id) => api.get(`/foods/${id}`),
  calculate: (data) => api.post('/foods/calculate', data)
}
