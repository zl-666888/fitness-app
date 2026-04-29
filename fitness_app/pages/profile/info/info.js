const userApi = require('../../../api/user')
const app = getApp()

Page({
  data: {
    form: {}
  },
  onShow() {
    userApi.getProfile().then(user => {
      this.setData({ form: {
        nickname: user.nickname || '',
        gender: user.gender || 0,
        height: user.height || '',
        weight: user.weight || '',
        phone: user.phone || ''
      }})
    })
  },
  onInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({ [`form.${field}`]: e.detail.value })
  },
  onGenderTap(e) {
    this.setData({ 'form.gender': parseInt(e.currentTarget.dataset.gender) })
  },
  save() {
    const d = this.data.form
    userApi.updateProfile({
      nickname: d.nickname,
      gender: d.gender,
      height: d.height ? parseFloat(d.height) : null,
      weight: d.weight ? parseFloat(d.weight) : null,
      phone: d.phone
    }).then(() => {
      wx.showToast({ title: '保存成功', icon: 'success' })
      setTimeout(() => wx.navigateBack(), 600)
    })
  }
})
