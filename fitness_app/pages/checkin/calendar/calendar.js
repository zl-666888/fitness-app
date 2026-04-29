const checkinApi = require('../../../api/checkin')

Page({
  data: {
    year: 0, month: 0,
    days: [], weekDays: ['日','一','二','三','四','五','六'],
    checkinDates: [],
    today: ''
  },
  onLoad() {
    const now = new Date()
    const today = `${now.getFullYear()}-${String(now.getMonth()+1).padStart(2,'0')}-${String(now.getDate()).padStart(2,'0')}`
    this.setData({ year: now.getFullYear(), month: now.getMonth() + 1, today })
    this.loadCalendar()
  },
  async loadCalendar() {
    const { year, month } = this.data
    const dates = await checkinApi.getCalendar(year, month)
    this.setData({ checkinDates: dates })
    this.buildCalendar()
  },
  buildCalendar() {
    const { year, month, checkinDates, today } = this.data
    const firstDay = new Date(year, month - 1, 1).getDay()
    const daysInMonth = new Date(year, month, 0).getDate()
    const days = []
    for (let i = 0; i < firstDay; i++) days.push({ day: '', date: '' })
    for (let i = 1; i <= daysInMonth; i++) {
      const date = `${year}-${String(month).padStart(2,'0')}-${String(i).padStart(2,'0')}`
      days.push({
        day: i,
        date,
        checked: checkinDates.includes(date),
        isToday: date === today
      })
    }
    this.setData({ days })
  },
  prevMonth() {
    let { year, month } = this.data
    if (month === 1) { year--; month = 12 } else { month-- }
    this.setData({ year, month })
    this.loadCalendar()
  },
  nextMonth() {
    let { year, month } = this.data
    if (month === 12) { year++; month = 1 } else { month++ }
    this.setData({ year, month })
    this.loadCalendar()
  }
})
