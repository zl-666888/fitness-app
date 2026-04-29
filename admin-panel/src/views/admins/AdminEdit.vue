<template>
  <div class="page">
    <el-card>
      <template #header>{{ isEdit ? '编辑管理员' : '添加管理员' }}</template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" style="max-width:500px">
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" placeholder="请输入管理员账号" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" :prop="isEdit ? '' : 'password'">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
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
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
          <el-button @click="$router.push('/admins')">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createAdmin, updateAdmin, getAdminList } from '@/api/admin'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const isEdit = ref(false)
const saving = ref(false)
const formRef = ref(null)

const form = reactive({ username: '', password: '', realName: '', role: 'admin' })

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

onMounted(async () => {
  const id = route.params.id
  if (id) {
    isEdit.value = true
    const res = await getAdminList({ page: 1, size: 50 })
    const admin = res.data.records.find(a => a.id == id)
    if (admin) {
      form.username = admin.username
      form.realName = admin.realName || ''
      form.role = admin.role
    }
  }
})

async function handleSave() {
  const rulesToValidate = isEdit.value
    ? { realName: rules.realName, role: rules.role }
    : rules
  const valid = await formRef.value.validate(Object.keys(rulesToValidate)).catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    const data = { username: form.username, realName: form.realName, role: form.role }
    if (form.password) data.password = form.password

    if (isEdit.value) {
      await updateAdmin(route.params.id, data)
      ElMessage.success('管理员已更新')
    } else {
      await createAdmin(data)
      ElMessage.success('管理员已添加')
    }
    router.push('/admins')
  } finally { saving.value = false }
}
</script>
