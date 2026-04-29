import request from './request'

export function getLogs(params) {
  return request.get('/admin/logs', { params })
}
