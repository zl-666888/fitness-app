<template>
  <div class="page">
    <el-card>
      <template #header>{{ isEdit ? '编辑食物' : '添加食物' }}</template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" style="max-width:700px">
        <el-form-item label="食物名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入食物名称" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="选择分类">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="热量(kcal/100g)" prop="calories">
          <el-input-number v-model="form.calories" :min="0" :precision="2" style="width:200px" />
        </el-form-item>
        <el-form-item label="蛋白质(g/100g)">
          <el-input-number v-model="form.protein" :min="0" :precision="2" style="width:200px" />
        </el-form-item>
        <el-form-item label="脂肪(g/100g)">
          <el-input-number v-model="form.fat" :min="0" :precision="2" style="width:200px" />
        </el-form-item>
        <el-form-item label="碳水(g/100g)">
          <el-input-number v-model="form.carbohydrate" :min="0" :precision="2" style="width:200px" />
        </el-form-item>
        <el-form-item label="纤维(g/100g)">
          <el-input-number v-model="form.fiber" :min="0" :precision="2" style="width:200px" />
        </el-form-item>
        <el-form-item label="单位">
          <el-select v-model="form.unit">
            <el-option label="g" value="g" />
            <el-option label="份" value="份" />
            <el-option label="个" value="个" />
            <el-option label="碗" value="碗" />
          </el-select>
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="form.imageUrl" placeholder="请输入食物图片URL" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createFood, updateFood, getFoodList } from '@/api/food'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const isEdit = ref(false)
const saving = ref(false)
const formRef = ref(null)

const categories = ['主食谷物','肉类蛋类','蔬菜菌藻','水果','奶制品','豆类坚果','饮品','零食甜点','调味品']

const form = reactive({
  name: '', category: '', calories: null, protein: null,
  fat: null, carbohydrate: null, fiber: null, unit: 'g', imageUrl: ''
})

const rules = {
  name: [{ required: true, message: '请输入食物名称', trigger: 'blur' }]
}

onMounted(async () => {
  const id = route.params.id
  if (id) {
    isEdit.value = true
    const res = await getFoodList({ page: 1, size: 200 })
    const food = res.data.records.find(f => f.id == id)
    if (food) Object.assign(form, food)
  }
})

async function handleSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    const id = route.params.id
    if (id) {
      await updateFood(id, form)
      ElMessage.success('食物已更新')
    } else {
      await createFood(form)
      ElMessage.success('食物已添加')
    }
    router.back()
  } finally { saving.value = false }
}
</script>
