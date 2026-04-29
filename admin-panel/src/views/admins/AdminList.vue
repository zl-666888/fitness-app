<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="showAdd">添加管理员</el-button>
      </div>
    </el-card>
    <el-card style="margin-top:16px">
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="账号" width="160" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'super' ? 'danger' : 'warning'" size="small">
              {{ row.role === 'super' ? '超管' : '管理员' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="showEdit(row)">编辑</el-button>
            <el-popconfirm v-if="row.role !== 'super'" title="确定删除该管理员吗？" @confirm="handleDelete(row.id)">
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

    <el-dialog v-model="dialogVisible" :title="editId ? '编辑管理员' : '添加管理员'" width="500px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" placeholder="请输入管理员账号" :disabled="!!editId" />
        </el-form-item>
        <el-form-item label="密码" :prop="editId ? '' : 'password'">
          <el-input v-model="form.password" type="password" placeholder="请输入密码，留空则不修改" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="form.role">
            <el-radio value="admin">管理员</el-radio>
            <el-radio value="super">超级管理员</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAdminList, createAdmin, updateAdmin, deleteAdmin } from '@/api/admin'
import { ElMessage } from 'element-plus'

const list = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const editId = ref(null)
const saving = ref(false)
const formRef = ref(null)

const form = reactive({ username: '', password: '', realName: '', role: 'admin' })

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getAdminList({ page: page.value, size: size.value })
    list.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function showAdd() {
  editId.value = null
  resetForm()
  form.password = ''
  dialogVisible.value = true
}

function showEdit(row) {
  editId.value = row.id
  form.username = row.username
  form.password = ''
  form.realName = row.realName || ''
  form.role = row.role
  dialogVisible.value = true
}

function resetForm() {
  form.username = ''
  form.password = ''
  form.realName = ''
  form.role = 'admin'
  formRef.value?.resetFields()
}

async function handleSave() {
  const rulesToValidate = editId.value
    ? { username: rules.username, role: rules.role }
    : rules
  const valid = await formRef.value.validate((keys) => keys.length > 0 ? null : rulesToValidate).catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    const data = { username: form.username, realName: form.realName, role: form.role }
    if (form.password) data.password = form.password

    if (editId.value) {
      await updateAdmin(editId.value, data)
      ElMessage.success('管理员已更新')
    } else {
      await createAdmin(data)
      ElMessage.success('管理员已添加')
    }
    dialogVisible.value = false
    fetchData()
  } finally { saving.value = false }
}

async function handleDelete(id) {
  await deleteAdmin(id)
  ElMessage.success('管理员已删除')
  fetchData()
}

function formatDate(date) {
  if (!date) return '-'
  return date.replace('T', ' ').substring(0, 19)
}
</script>

<style scoped>
.toolbar { display: flex; gap: 10px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
