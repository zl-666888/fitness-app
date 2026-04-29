import request from './request'

export function getFoodList(params) {
  return request.get('/admin/foods', { params })
}

export function createFood(data) {
  return request.post('/admin/foods', data)
}

export function updateFood(id, data) {
  return request.put(`/admin/foods/${id}`, data)
}

export function deleteFood(id) {
  return request.delete(`/admin/foods/${id}`)
}

export function importFoods(data) {
  return request.post('/admin/foods/import', data)
}
