const app = getApp()

let isRefreshing = false
let refreshSubscribers = []

const getBaseUrl = () => {
  const env = wx.getAccountInfoSync().miniProgram.envVersion
  const LAN_IP = '192.168.8.131'
  const BASE = `http://${LAN_IP}:8080/api`
  const urls = {
    develop: BASE,
    trial: BASE,
    release: BASE
  }
  return urls[env] || BASE
}

function forceLogout() {
  wx.removeStorageSync('token')
  wx.removeStorageSync('refreshToken')
  wx.removeStorageSync('userInfo')
  wx.showToast({ title: '登录已过期', icon: 'none' })
  setTimeout(() => wx.reLaunch({ url: '/pages/auth/login/login' }), 1000)
}

function doRefresh() {
  return new Promise((resolve, reject) => {
    const refreshToken = wx.getStorageSync('refreshToken')
    if (!refreshToken) {
      reject(new Error('No refresh token'))
      return
    }
    wx.request({
      url: getBaseUrl() + '/user/refresh',
      method: 'POST',
      data: { refreshToken },
      header: { 'Content-Type': 'application/json' },
      timeout: 15000,
      success(res) {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          const { accessToken, refreshToken: newRefreshToken } = res.data.data
          wx.setStorageSync('token', accessToken)
          wx.setStorageSync('refreshToken', newRefreshToken)
          resolve(accessToken)
        } else {
          reject(res)
        }
      },
      fail(err) {
        reject(err)
      }
    })
  })
}

function onRefreshed(newToken) {
  refreshSubscribers.forEach(cb => cb(newToken))
  refreshSubscribers = []
}

const request = (url, options = {}) => {
  const BASE_URL = getBaseUrl()
  const token = wx.getStorageSync('token')
  const header = {
    'Content-Type': 'application/json',
    ...options.header
  }
  if (token && !options._skipAuth) {
    header['Authorization'] = `Bearer ${token}`
  }

  return new Promise((resolve, reject) => {
    wx.request({
      url: BASE_URL + url,
      method: options.method || 'GET',
      data: options.data,
      header,
      timeout: 15000,
      success(res) {
        if (res.statusCode === 200) {
          if (res.data.code === 200) {
            resolve(res.data.data)
          } else {
            wx.showToast({ title: res.data.message || '请求失败', icon: 'none' })
            reject(res.data)
          }
        } else if (res.statusCode === 401) {
          if (options._isRetry) {
            forceLogout()
            reject(res)
            return
          }
          const storedRefreshToken = wx.getStorageSync('refreshToken')
          if (!storedRefreshToken) {
            forceLogout()
            reject(res)
            return
          }
          if (!isRefreshing) {
            isRefreshing = true
            doRefresh().then(newToken => {
              isRefreshing = false
              onRefreshed(newToken)
              options._isRetry = true
              options.header = options.header || {}
              options.header['Authorization'] = `Bearer ${newToken}`
              request(url, options).then(resolve).catch(reject)
            }).catch(() => {
              isRefreshing = false
              refreshSubscribers = []
              forceLogout()
              reject(res)
            })
          } else {
            addRefreshSubscriber(newToken => {
              options._isRetry = true
              options.header = options.header || {}
              options.header['Authorization'] = `Bearer ${newToken}`
              request(url, options).then(resolve).catch(reject)
            })
          }
        } else if (res.statusCode === 403) {
          wx.showToast({ title: '无权限', icon: 'none' })
          reject(res)
        } else {
          wx.showToast({ title: `请求失败(${res.statusCode})`, icon: 'none' })
          reject(res)
        }
      },
      fail(err) {
        wx.showToast({ title: '网络异常', icon: 'none' })
        reject(err)
      }
    })
  })
}

function addRefreshSubscriber(cb) {
  refreshSubscribers.push(cb)
}

const api = {
  get: (url, data) => request(url, { method: 'GET', data }),
  post: (url, data) => request(url, { method: 'POST', data }),
  put: (url, data) => request(url, { method: 'PUT', data }),
  delete: (url, data) => request(url, { method: 'DELETE', data })
}

module.exports = api
