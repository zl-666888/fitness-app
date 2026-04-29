<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <el-input v-model="keyword" placeholder="搜索用户名/手机号" style="width:260px" clearable @clear="fetchData" @keyup.enter="fetchData" />
        <el-button type="primary" @click="fetchData">搜索</el-button>
      </div>
    </el-card>
    <el-card style="margin-top:16px">
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" width="140" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column label="性别" width="80">
          <template #default="{ row }">
            {{ { 0: '未知', 1: '男', 2: '女' }[row.gender] || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="height" label="身高(cm)" width="90" />
        <el-table-column prop="weight" label="体重(kg)" width="90" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" min-width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="showDetail(row)">详情</el-button>
            <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
            <el-popconfirm title="确定删除该用户吗？将同时删除其所有打卡数据" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" :page-sizes="[10,20,50]" layout="total, sizes, prev, pager, next" @change="fetchData" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="用户详情" width="500px">
      <el-descriptions v-if="currentUser" :column="2" border>
        <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ currentUser.nickname || '-' }}</el-descriptions-item>
        <el-descriptions-item label="身高">{{ currentUser.height || '-' }} cm</el-descriptions-item>
        <el-descriptions-item label="体重">{{ currentUser.weight || '-' }} kg</el-descriptions-item>
        <el-descriptions-item label="性别">{{ { 0: '未知', 1: '男', 2: '女' }[currentUser.gender] || '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机">{{ currentUser.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ formatDate(currentUser.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'" size="small">
            {{ currentUser.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserList, deleteUser, toggleUserStatus } from '@/api/user'
import { ElMessage } from 'element-plus'

const list = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const keyword = ref('')
const dialogVisible = ref(false)
const currentUser = ref(null)

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getUserList({ page: page.value, size: size.value, keyword: keyword.value })
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function showDetail(row) {
  currentUser.value = row
  dialogVisible.value = true
}

async function toggleStatus(row) {
  await toggleUserStatus(row.id)
  ElMessage.success('状态已切换')
  fetchData()
}

async function handleDelete(id) {
  await deleteUser(id)
  ElMessage.success('用户已删除')
  fetchData()
}

function formatDate(date) {
  if (!date) return '-'
  return date.replace('T', ' ').substring(0, 19)
}
</script>

<style scoped>
.page { }
.toolbar { display: flex; gap: 10px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
