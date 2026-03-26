<template>
  <div class="exams">
    <div class="toolbar">
      <h2>考试管理</h2>
      <el-button type="primary" @click="showAddDialog">创建考试</el-button>
    </div>

    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="课程选择">
          <el-select v-model="filterForm.courseId" placeholder="请选择课程" @change="handleCourseChange">
            <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="考试状态">
          <el-select v-model="filterForm.status" placeholder="全部" clearable>
            <el-option label="全部" :value="0" />
            <el-option label="未开始" :value="1" />
            <el-option label="进行中" :value="2" />
            <el-option label="已结束" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <el-table :data="exams" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="考试标题" />
      <el-table-column prop="courseId" label="课程ID" width="100" />
      <el-table-column prop="totalScore" label="总分" width="100" />
      <el-table-column prop="passingScore" label="及格分" width="100" />
      <el-table-column prop="startTime" label="开始时间" width="180" />
      <el-table-column prop="endTime" label="结束时间" width="180" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="examForm" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="考试标题" prop="title">
          <el-input v-model="examForm.title" placeholder="请输入考试标题" />
        </el-form-item>
        <el-form-item label="课程ID" prop="courseId">
          <el-select v-model="examForm.courseId" placeholder="请选择课程">
            <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="总分" prop="totalScore">
          <el-input v-model.number="examForm.totalScore" type="number" placeholder="请输入总分" />
        </el-form-item>
        <el-form-item label="及格分数" prop="passingScore">
          <el-input v-model.number="examForm.passingScore" type="number" placeholder="请输入及格分数" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="examForm.startTime" type="datetime" placeholder="选择开始时间" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="examForm.endTime" type="datetime" placeholder="选择结束时间" value-format="YYYY-MM-DD HH:mm:ss" />
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

const courses = ref([])
const exams = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('创建考试')
const submitLoading = ref(false)
const formRef = ref(null)
const isEdit = ref(false)

const filterForm = reactive({
  courseId: null,
  status: 0
})

const examForm = reactive({
  id: null,
  title: '',
  courseId: null,
  totalScore: 100,
  passingScore: 60,
  startTime: '',
  endTime: ''
})

const rules = {
  title: [{ required: true, message: '请输入考试标题', trigger: 'blur' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  totalScore: [{ required: true, message: '请输入总分', trigger: 'blur' }],
  passingScore: [{ required: true, message: '请输入及格分数', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

const handleCourseChange = async (courseId) => {
  if (!courseId) return
  filterForm.courseId = courseId
  await fetchExams()
}

const fetchCourses = async () => {
  try {
    const res = await axios.get('/courses/list/teacher?status=0&currentPage=1')
    courses.value = res.data?.records || res.data || []
  } catch (error) {
    console.error('Failed to fetch courses:', error)
  }
}

const fetchExams = async () => {
  if (!filterForm.courseId) {
    exams.value = []
    return
  }
  loading.value = true
  try {
    const res = await axios.get(`/exam/exam-info/list/${filterForm.courseId}?status=${filterForm.status}`)
    exams.value = res.data?.records || res.data || []
  } catch (error) {
    console.error('Failed to fetch exams:', error)
    ElMessage.error('获取考试列表失败')
  } finally {
    loading.value = false
  }
}

const showAddDialog = () => {
  isEdit.value = false
  dialogTitle.value = '创建考试'
  Object.assign(examForm, { id: null, title: '', courseId: filterForm.courseId, totalScore: 100, passingScore: 60, startTime: '', endTime: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑考试'
  Object.assign(examForm, { ...row })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    await axios.post('/exam/exam-info/update', { examInfo: examForm })
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    fetchExams()
  } catch (error) {
    console.error('Failed to submit:', error)
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该考试吗?', '提示', {
      type: 'warning'
    })
    await axios.post(`/exam/exam-info/delete/${id}`)
    ElMessage.success('删除成功')
    fetchExams()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete:', error)
    }
  }
}

onMounted(() => {
  fetchCourses()
})
</script>

<style scoped>
.exams {
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

.filter-card {
  margin-bottom: 20px;
}
</style>
