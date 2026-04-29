import request from './request'

export function getVideoList(params) {
  return request.get('/admin/videos', { params })
}

export function createVideo(data) {
  return request.post('/admin/videos', data)
}

export function updateVideo(id, data) {
  return request.put(`/admin/videos/${id}`, data)
}

export function deleteVideo(id) {
  return request.delete(`/admin/videos/${id}`)
}
