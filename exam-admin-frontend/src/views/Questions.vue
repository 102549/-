<template>
  <div class="questions">
    <div class="toolbar">
      <h2>题库管理</h2>
      <el-button type="primary" @click="showAddDialog">添加题目</el-button>
    </div>

    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="课程选择">
          <el-select v-model="filterForm.courseId" placeholder="请选择课程" @change="handleCourseChange">
            <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <el-table :data="questions" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="content" label="题目内容" show-overflow-tooltip />
      <el-table-column prop="type" label="类型" width="100">
        <template #default="{ row }">
          <el-tag>{{ typeMap[row.type] || '未知' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="difficulty" label="难度" width="100">
        <template #default="{ row }">
          <el-rate v-model="row.difficulty" disabled size="small" />
        </template>
      </el-table-column>
      <el-table-column prop="score" label="分值" width="80" />
      <el-table-column prop="courseId" label="课程ID" width="100" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="questionForm" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="题目内容" prop="content">
          <el-input v-model="questionForm.content" type="textarea" rows="3" placeholder="请输入题目内容" />
        </el-form-item>
        <el-form-item label="题目类型" prop="type">
          <el-select v-model="questionForm.type" placeholder="请选择题目类型">
            <el-option label="单选题" :value="0" />
            <el-option label="多选题" :value="1" />
            <el-option label="判断题" :value="2" />
            <el-option label="填空题" :value="3" />
            <el-option label="主观题" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度" prop="difficulty">
          <el-rate v-model="questionForm.difficulty" />
        </el-form-item>
        <el-form-item label="分值" prop="score">
          <el-input v-model.number="questionForm.score" type="number" placeholder="请输入分值" />
        </el-form-item>
        <el-form-item label="课程ID" prop="courseId">
          <el-select v-model="questionForm.courseId" placeholder="请选择课程">
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

const courses = ref([])
const questions = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('添加题目')
const submitLoading = ref(false)
const formRef = ref(null)
const isEdit = ref(false)

const filterForm = reactive({
  courseId: null
})

const typeMap = {
  0: '单选题',
  1: '多选题',
  2: '判断题',
  3: '填空题',
  4: '主观题'
}

const questionForm = reactive({
  id: null,
  content: '',
  type: 0,
  difficulty: 3,
  score: 5,
  courseId: null
})

const rules = {
  content: [{ required: true, message: '请输入题目内容', trigger: 'blur' }],
  type: [{ required: true, message: '请选择题目类型', trigger: 'change' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }]
}

const handleCourseChange = async (courseId) => {
  if (!courseId) return
  filterForm.courseId = courseId
  await fetchQuestions()
}

const fetchCourses = async () => {
  try {
    const res = await axios.get('/courses/list/teacher?status=0&currentPage=1')
    courses.value = res.data?.records || res.data || []
  } catch (error) {
    console.error('Failed to fetch courses:', error)
  }
}

const fetchQuestions = async () => {
  if (!filterForm.courseId) {
    questions.value = []
    return
  }
  loading.value = true
  try {
    const res = await axios.get(`/exam/question/list/${filterForm.courseId}?currentPage=1`)
    questions.value = res.data?.records || res.data || []
  } catch (error) {
    console.error('Failed to fetch questions:', error)
    ElMessage.error('获取题目列表失败')
  } finally {
    loading.value = false
  }
}

const showAddDialog = () => {
  isEdit.value = false
  dialogTitle.value = '添加题目'
  Object.assign(questionForm, { id: null, content: '', type: 0, difficulty: 3, score: 5, courseId: filterForm.courseId })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑题目'
  Object.assign(questionForm, { ...row })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    await axios.post('/exam/question/add', questionForm)
    ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
    dialogVisible.value = false
    fetchQuestions()
  } catch (error) {
    console.error('Failed to submit:', error)
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该题目吗?', '提示', {
      type: 'warning'
    })
    await axios.post(`/exam/question/delete/${id}`)
    ElMessage.success('删除成功')
    fetchQuestions()
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
.questions {
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
