const userApi = require('../../../api/user')

Page({
  data: {
    form: { goalType: '', targetWeight: '', weeklyFrequency: '', reminderTime: '' },
    goalTypes: ['减脂', '增肌', '塑形', '保持健康']
  },
  onShow() {
    userApi.getGoal().then(goal => {
      if (goal) {
        this.setData({ form: {
          goalType: goal.goalType || '',
          targetWeight: goal.targetWeight || '',
          weeklyFrequency: goal.weeklyFrequency || '',
          reminderTime: goal.reminderTime || ''
        }})
      }
    })
  },
  onTypeTap(e) { this.setData({ 'form.goalType': e.currentTarget.dataset.type }) },
  onInput(e) { const f = e.currentTarget.dataset.field; this.setData({ [`form.${f}`]: e.detail.value }) },
  save() {
    const f = this.data.form
    userApi.setGoal({
      goalType: f.goalType,
      targetWeight: f.targetWeight ? parseFloat(f.targetWeight) : null,
      weeklyFrequency: f.weeklyFrequency ? parseInt(f.weeklyFrequency) : null,
      reminderTime: f.reminderTime || null
    }).then(() => {
      wx.showToast({ title: '保存成功', icon: 'success' })
      setTimeout(() => wx.navigateBack(), 600)
    })
  }
})
