<template>
  <div class="courses">
    <div class="toolbar">
      <h2>课程管理</h2>
      <el-button type="primary" @click="showAddDialog">添加课程</el-button>
    </div>

    <el-table :data="courses" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="课程名称" />
      <el-table-column prop="introduce" label="课程介绍" show-overflow-tooltip />
      <el-table-column prop="userId" label="创建者ID" width="100" />
      <el-table-column prop="isPublic" label="是否公开" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isPublic === 1 ? 'success' : 'info'">
            {{ row.isPublic === 1 ? '公开' : '私有' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="courseForm" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="courseForm.name" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程封面" prop="cover">
          <el-input v-model="courseForm.cover" placeholder="请输入封面URL" />
        </el-form-item>
        <el-form-item label="课程介绍" prop="introduce">
          <el-input v-model="courseForm.introduce" type="textarea" rows="3" placeholder="请输入课程介绍" />
        </el-form-item>
        <el-form-item label="是否公开" prop="isPublic">
          <el-switch v-model="courseForm.isPublic" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../utils/axios'

const courses = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('添加课程')
const submitLoading = ref(false)
const formRef = ref(null)
const isEdit = ref(false)

const courseForm = reactive({
  id: null,
  name: '',
  cover: '',
  introduce: '',
  isPublic: 0
})

const rules = {
  name: [{ required: true, message: '请输入课程名称', trigger: 'blur' }]
}

const fetchCourses = async () => {
  loading.value = true
  try {
    const res = await axios.get('/courses/list/teacher?status=0&currentPage=1')
    courses.value = res.data?.records || res.data || []
  } catch (error) {
    console.error('Failed to fetch courses:', error)
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

const showAddDialog = () => {
  isEdit.value = false
  dialogTitle.value = '添加课程'
  Object.assign(courseForm, { id: null, name: '', cover: '', introduce: '', isPublic: 0 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑课程'
  Object.assign(courseForm, { ...row })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    await axios.post('/courses/update', courseForm)
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    fetchCourses()
  } catch (error) {
    console.error('Failed to submit:', error)
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  fetchCourses()
})
</script>

<style scoped>
.courses {
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.toolbar h2 {
  margin: 0;
}
</style>
