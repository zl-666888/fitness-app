const api = require('./request')

module.exports = {
  createCheckin: (data) => api.post('/checkin', data),
  getTodayCheckin: () => api.get('/checkin/today'),
  getCalendar: (year, month) => api.get('/checkin/calendar', { year, month }),
  getStats: () => api.get('/checkin/stats'),
  deleteCheckin: (id) => api.delete(`/checkin/${id}`)
}
