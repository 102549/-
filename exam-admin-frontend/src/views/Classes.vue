<template>
  <div class="classes">
    <div class="toolbar">
      <h2>班级管理</h2>
      <el-button type="primary" @click="showAddDialog">添加班级</el-button>
    </div>

    <el-table :data="classes" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="班级名称" />
      <el-table-column prop="courseId" label="课程ID" width="100" />
      <el-table-column prop="teacherId" label="教师ID" width="100" />
      <el-table-column prop="createdAt" label="创建时间" width="180" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="classForm" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="班级名称" prop="name">
          <el-input v-model="classForm.name" placeholder="请输入班级名称" />
        </el-form-item>
        <el-form-item label="课程ID" prop="courseId">
          <el-select v-model="classForm.courseId" placeholder="请选择课程">
            <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id" />
          </el-select>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '../utils/axios'

const classes = ref([])
const courses = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('添加班级')
const submitLoading = ref(false)
const formRef = ref(null)
const isEdit = ref(false)

const classForm = reactive({
  id: null,
  name: '',
  courseId: null,
  teacherId: null
})

const rules = {
  name: [{ required: true, message: '请输入班级名称', trigger: 'blur' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }]
}

const fetchClasses = async () => {
  loading.value = true
  try {
    const res = await axios.get('/classes/list')
    classes.value = res.data || []
  } catch (error) {
    console.error('Failed to fetch classes:', error)
    ElMessage.error('获取班级列表失败')
  } finally {
    loading.value = false
  }
}

const fetchCourses = async () => {
  try {
    const res = await axios.get('/courses/list/teacher?status=0&currentPage=1')
    courses.value = res.data?.records || res.data || []
  } catch (error) {
    console.error('Failed to fetch courses:', error)
  }
}

const showAddDialog = () => {
  isEdit.value = false
  dialogTitle.value = '添加班级'
  Object.assign(classForm, { id: null, name: '', courseId: null })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑班级'
  Object.assign(classForm, { ...row })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    await axios.post('/classes/update', classForm)
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    fetchClasses()
  } catch (error) {
    console.error('Failed to submit:', error)
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该班级吗?', '提示', {
      type: 'warning'
    })
    await axios.post(`/classes/delete/${id}`)
    ElMessage.success('删除成功')
    fetchClasses()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete:', error)
    }
  }
}

onMounted(() => {
  fetchClasses()
  fetchCourses()
})
</script>

<style scoped>
.classes {
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
