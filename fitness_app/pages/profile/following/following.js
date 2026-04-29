const socialApi = require('../../../api/social')

Page({
  data: {
    list: [], loading: false
  },
  onShow() {
    socialApi.getFollowingList({ page: 1, size: 50 }).then(res => {
      this.setData({ list: res.records })
    })
  }
})
