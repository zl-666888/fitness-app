const api = require('./request')

module.exports = {
  getProfile: () => api.get('/user/profile'),
  updateProfile: (data) => api.put('/user/profile', data),
  getGoal: () => api.get('/user/goal'),
  setGoal: (data) => api.put('/user/goal', data)
}
