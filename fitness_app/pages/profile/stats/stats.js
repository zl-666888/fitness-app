const checkinApi = require('../../../api/checkin')

Page({
  data: { stats: null },
  onShow() {
    checkinApi.getStats().then(stats => this.setData({ stats }))
  }
})
