<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6" v-for="item in stats" :key="item.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ item.value }}</div>
          <div class="stat-label">{{ item.label }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>本周打卡趋势</template>
          <div ref="checkinChartRef" style="height:300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>用户增长趋势</template>
          <div ref="userChartRef" style="height:300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { getDashboard } from '@/api/dashboard'

const stats = reactive([
  { label: '用户总数', value: 0 },
  { label: '今日打卡', value: 0 },
  { label: '视频数量', value: 0 },
  { label: '食物数量', value: 0 }
])

const checkinChartRef = ref(null)
const userChartRef = ref(null)

onMounted(async () => {
  const res = await getDashboard()
  const data = res.data
  stats[0].value = data.totalUsers || 0
  stats[1].value = data.todayCheckins || 0
  stats[2].value = data.totalVideos || 0
  stats[3].value = data.totalFoods || 0

  await nextTick()
  renderCheckinChart(data.weeklyCheckinTrend || [])
  renderUserChart(data.userGrowthTrend || [])
})

function renderCheckinChart(trend) {
  if (!checkinChartRef.value) return
  import('echarts').then((echarts) => {
    const chart = echarts.init(checkinChartRef.value)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: trend.map(t => t.date) },
      yAxis: { type: 'value', minInterval: 1 },
      series: [{
        name: '打卡数', type: 'line',
        data: trend.map(t => t.count),
        smooth: true, areaStyle: {}
      }]
    })
    window.addEventListener('resize', () => chart.resize())
  })
}

function renderUserChart(trend) {
  if (!userChartRef.value) return
  import('echarts').then((echarts) => {
    const chart = echarts.init(userChartRef.value)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: trend.map(t => t.date) },
      yAxis: { type: 'value', minInterval: 1 },
      series: [{
        name: '新增用户', type: 'bar',
        data: trend.map(t => t.count),
        itemStyle: { color: '#409EFF' }
      }]
    })
    window.addEventListener('resize', () => chart.resize())
  })
}
</script>

<style scoped>
.stat-card { text-align: center; }
.stat-value { font-size: 36px; font-weight: bold; color: #409EFF; }
.stat-label { margin-top: 8px; color: #909399; font-size: 14px; }
</style>
