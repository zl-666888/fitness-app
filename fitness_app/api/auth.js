const api = require('./request')

module.exports = {
  register: (data) => api.post('/user/register', data),
  login: (data) => api.post('/user/login', data)
}
