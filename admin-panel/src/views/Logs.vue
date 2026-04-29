<template>
  <div class="page">
    <el-card>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="adminId" label="管理员ID" width="90" />
        <el-table-column prop="action" label="操作类型" width="140" />
        <el-table-column prop="targetType" label="操作对象" width="100">
          <template #default="{ row }">{{ targetTypeMap[row.targetType] || row.targetType }}</template>
        </el-table-column>
        <el-table-column prop="targetId" label="对象ID" width="80" />
        <el-table-column prop="detail" label="详情" min-width="160" show-overflow-tooltip />
        <el-table-column prop="ipAddress" label="IP地址" width="140" />
        <el-table-column label="操作时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" :page-sizes="[10,20,50]" layout="total, sizes, prev, pager, next" @change="fetchData" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getLogs } from '@/api/log'

const targetTypeMap = { user: '用户', video: '视频', food: '食物', admin: '管理员' }

const list = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getLogs({ page: page.value, size: size.value })
    list.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function formatDate(date) {
  if (!date) return '-'
  return date.replace('T', ' ').substring(0, 19)
}
</script>

<style scoped>
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
