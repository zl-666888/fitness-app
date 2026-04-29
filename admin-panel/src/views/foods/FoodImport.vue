<template>
  <div class="page">
    <el-card>
      <template #header>批量导入食物数据</template>
      <div class="import-area">
        <p class="tip">请粘贴 JSON 数组格式数据，每项为一个食物对象：</p>
        <p class="tip">示例：<code>[{"name":"苹果","category":"水果","calories":52,"protein":0.2,"fat":0.2,"carbohydrate":13.5,"fiber":2.4,"unit":"g"}]</code></p>
        <el-input
          v-model="jsonText"
          type="textarea"
          :rows="12"
          placeholder='[{"name":"食物名","category":"分类","calories":0,"protein":0,"fat":0,"carbohydrate":0,"fiber":0,"unit":"g"}]'
        />
        <div style="margin-top:16px">
          <el-button type="primary" :loading="importing" @click="handleImport">批量导入</el-button>
        </div>
        <el-divider />
        <p class="tip">或上传 JSON 文件：</p>
        <el-upload
          :auto-upload="false"
          accept=".json"
          :on-change="handleFileChange"
          :show-file-list="false"
        >
          <el-button>选择 JSON 文件</el-button>
        </el-upload>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { importFoods } from '@/api/food'
import { ElMessage } from 'element-plus'

const jsonText = ref('')
const importing = ref(false)

async function handleImport() {
  if (!jsonText.value.trim()) {
    ElMessage.warning('请输入数据')
    return
  }
  try {
    const data = JSON.parse(jsonText.value)
    if (!Array.isArray(data)) {
      ElMessage.error('数据格式错误，应为 JSON 数组')
      return
    }
    importing.value = true
    await importFoods(data)
    ElMessage.success(`成功导入 ${data.length} 条食物数据`)
    jsonText.value = ''
  } catch (e) {
    if (e instanceof SyntaxError) {
      ElMessage.error('JSON 格式错误，请检查')
    }
  } finally {
    importing.value = false
  }
}

function handleFileChange(file) {
  const reader = new FileReader()
  reader.onload = (e) => {
    jsonText.value = e.target.result
    ElMessage.success('文件已读取，点击"批量导入"')
  }
  reader.readAsText(file.raw)
}
</script>

<style scoped>
.tip { color: #909399; font-size: 13px; margin-bottom: 8px; }
.tip code { background: #f0f2f5; padding: 2px 6px; border-radius: 3px; font-size: 12px; }
</style>
