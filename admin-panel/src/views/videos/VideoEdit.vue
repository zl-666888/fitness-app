<template>
  <div class="page">
    <el-card>
      <template #header>{{ isEdit ? '编辑视频' : '添加视频' }}</template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width:700px">
        <el-form-item label="视频标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入视频标题" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入视频描述" />
        </el-form-item>
        <el-form-item label="封面图URL" prop="coverUrl">
          <el-input v-model="form.coverUrl" placeholder="请输入封面图URL或上传" />
          <el-upload :http-request="uploadCover" :show-file-list="false" style="margin-left:10px">
            <el-button size="small">上传</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="视频文件" prop="videoUrl">
          <el-input v-model="form.videoUrl" placeholder="请上传视频文件或输入URL" />
          <el-upload :http-request="uploadVideo" :show-file-list="false" accept="video/*" style="margin-left:10px">
            <el-button size="small" :loading="uploadingVideo">上传视频</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="选择分类">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="时长(秒)" prop="duration">
          <el-input-number v-model="form.duration" :min="0" placeholder="视频时长(秒)" />
        </el-form-item>
        <el-form-item label="难度" prop="difficulty">
          <el-radio-group v-model="form.difficulty">
            <el-radio value="beginner">入门</el-radio>
            <el-radio value="intermediate">中级</el-radio>
            <el-radio value="advanced">高级</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="教练" prop="coach">
          <el-input v-model="form.coach" placeholder="教练/演示者" />
        </el-form-item>
        <el-form-item label="排序权重" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
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
import { createVideo, updateVideo, getVideoList } from '@/api/video'
import { uploadFile } from '@/api/upload'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const isEdit = ref(false)
const saving = ref(false)
const uploadingVideo = ref(false)
const formRef = ref(null)

const categories = ['胸部训练','背部训练','腿部训练','肩部训练','手臂训练','核心腹部','全身燃脂','瑜伽拉伸','热身放松']

const form = reactive({
  title: '', description: '', coverUrl: '', videoUrl: '',
  category: '', duration: null, difficulty: 'beginner',
  coach: '', sortOrder: 0
})

const rules = {
  title: [{ required: true, message: '请输入视频标题', trigger: 'blur' }],
  videoUrl: [{ required: true, message: '请输入视频URL', trigger: 'blur' }]
}

onMounted(async () => {
  const id = route.params.id
  if (id) {
    isEdit.value = true
    const res = await getVideoList({ page: 1, size: 100 })
    const video = res.data.records.find(v => v.id == id)
    if (video) Object.assign(form, video)
  }
})

async function uploadCover(options) {
  try {
    const res = await uploadFile(options.file)
    form.coverUrl = res.data.url
    ElMessage.success('封面上传成功')
  } catch { ElMessage.error('封面上传失败') }
}

async function uploadVideo(options) {
  uploadingVideo.value = true
  try {
    const res = await uploadFile(options.file)
    form.videoUrl = res.data.url
    ElMessage.success('视频上传成功')
  } catch {
    ElMessage.error('视频上传失败')
  } finally {
    uploadingVideo.value = false
  }
}

async function handleSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    const id = route.params.id
    if (id) {
      await updateVideo(id, form)
      ElMessage.success('视频已更新')
    } else {
      await createVideo(form)
      ElMessage.success('视频已添加')
    }
    router.back()
  } finally { saving.value = false }
}
</script>
