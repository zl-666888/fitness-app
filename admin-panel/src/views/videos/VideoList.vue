<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <el-select v-model="category" placeholder="按分类筛选" style="width:160px" clearable @change="fetchData">
          <el-option label="全部" value="" />
          <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
        </el-select>
        <el-button type="primary" @click="$router.push('/videos/add')">添加视频</el-button>
      </div>
    </el-card>
    <el-card style="margin-top:16px">
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="封面" width="100">
          <template #default="{ row }">
            <el-image v-if="row.coverUrl" :src="row.coverUrl" style="width:80px;height:50px" fit="cover" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="difficulty" label="难度" width="80">
          <template #default="{ row }">
            <el-tag :type="tagType(row.difficulty)" size="small">{{ row.difficulty || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长(秒)" width="90" />
        <el-table-column prop="coach" label="教练" width="100" />
        <el-table-column prop="sortOrder" label="排序" width="70" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="$router.push(`/videos/${row.id}/edit`)">编辑</el-button>
            <el-popconfirm title="确定删除该视频吗？" @confirm="handleDelete(row.id)">
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getVideoList, deleteVideo } from '@/api/video'
import { ElMessage } from 'element-plus'

const categories = ['胸部训练','背部训练','腿部训练','肩部训练','手臂训练','核心腹部','全身燃脂','瑜伽拉伸','热身放松']

const list = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const category = ref('')

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getVideoList({ page: page.value, size: size.value, category: category.value })
    list.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function tagType(difficulty) {
  const map = { beginner: 'success', intermediate: 'warning', advanced: 'danger' }
  return map[difficulty] || 'info'
}

async function handleDelete(id) {
  await deleteVideo(id)
  ElMessage.success('视频已删除')
  fetchData()
}
</script>

<style scoped>
.toolbar { display: flex; gap: 10px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
