import axios from 'axios'

export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  const token = localStorage.getItem('admin_token')
  return axios.post('/api/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
      Authorization: token ? `Bearer ${token}` : ''
    }
  }).then(res => res.data)
}
