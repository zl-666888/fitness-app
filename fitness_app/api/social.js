const api = require('./request')

module.exports = {
  getFeed: (params) => api.get('/feed', params),
  toggleLike: (checkinId) => api.post(`/checkin/${checkinId}/like`),
  addComment: (checkinId, data) => api.post(`/checkin/${checkinId}/comment`, data),
  getComments: (checkinId) => api.get(`/checkin/${checkinId}/comments`),
  follow: (userId) => api.post(`/user/follow/${userId}`),
  unfollow: (userId) => api.post(`/user/unfollow/${userId}`),
  getFollowingList: (params) => api.get('/user/following', params),
  getFollowerList: (params) => api.get('/user/follower', params)
}
