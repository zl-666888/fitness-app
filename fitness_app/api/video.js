const api = require('./request')

module.exports = {
  getVideoList: (params) => api.get('/videos', params),
  searchVideos: (params) => api.get('/videos/search', params),
  getVideoDetail: (id) => api.get(`/videos/${id}`),
  toggleFavorite: (id) => api.post(`/videos/${id}/favorite`)
}
