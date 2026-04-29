<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <el-select v-model="category" placeholder="按分类筛选" style="width:160px" clearable @change="fetchData">
          <el-option label="全部" value="" />
          <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
        </el-select>
        <el-input v-model="keyword" placeholder="搜索食物名称" style="width:220px" clearable @clear="fetchData" @keyup.enter="fetchData" />
        <el-button type="primary" @click="$router.push('/foods/add')">添加食物</el-button>
        <el-button @click="$router.push('/foods/import')">批量导入</el-button>
      </div>
    </el-card>
    <el-card style="margin-top:16px">
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="名称" width="150" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="calories" label="热量(kcal/100g)" width="130" />
        <el-table-column prop="protein" label="蛋白质(g)" width="100" />
        <el-table-column prop="fat" label="脂肪(g)" width="90" />
        <el-table-column prop="carbohydrate" label="碳水(g)" width="90" />
        <el-table-column prop="fiber" label="纤维(g)" width="80" />
        <el-table-column prop="unit" label="单位" width="70" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="$router.push(`/foods/${row.id}/edit`)">编辑</el-button>
            <el-popconfirm title="确定删除该食物吗？" @confirm="handleDelete(row.id)">
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
import { getFoodList, deleteFood } from '@/api/food'
import { ElMessage } from 'element-plus'

const categories = ['主食谷物','肉类蛋类','蔬菜菌藻','水果','奶制品','豆类坚果','饮品','零食甜点','调味品']

const list = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const category = ref('')
const keyword = ref('')

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getFoodList({ page: page.value, size: size.value, category: category.value })
    let records = res.data.records
    if (keyword.value) {
      records = records.filter(f => f.name && f.name.includes(keyword.value))
    }
    list.value = records
    total.value = res.data.total
  } finally { loading.value = false }
}

async function handleDelete(id) {
  await deleteFood(id)
  ElMessage.success('食物已删除')
  fetchData()
}
</script>

<style scoped>
.toolbar { display: flex; gap: 10px; flex-wrap: wrap; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
