<template>
  <div class="student-exams">
    <div class="toolbar">
      <h2>我的考试</h2>
    </div>

    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="课程选择">
          <el-select v-model="filterForm.courseId" placeholder="请选择课程" clearable @change="handleCourseChange">
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
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row)">{{ getStatusText(row) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button v-if="canTakeExam(row)" type="primary" size="small" @click="handleStartExam(row)">
            开始考试
          </el-button>
          <el-button v-else-if="isExamEnded(row)" type="info" size="small" disabled>
            已结束
          </el-button>
          <el-button v-else type="warning" size="small" disabled>
            未开始
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="examDialogVisible" :title="currentExam?.title || '考试'" width="900px" :close-on-click-modal="false">
      <div v-if="examStarted" class="exam-container">
        <div class="exam-header">
          <div class="exam-info">
            <span>考试时间：{{ examDetail.examInfo?.totalTime || 60 }}分钟</span>
            <span>剩余时间：<el-tag type="danger">{{ formatTime(remainingTime) }}</el-tag></span>
          </div>
          <div class="question-nav">
            <span>题目导航：</span>
            <el-button
              v-for="(q, index) in allQuestions"
              :key="q.id"
              :type="getNavButtonType(q)"
              :class="['nav-btn', { current: currentQuestionIndex === index }]"
              size="small"
              @click="goToQuestion(index)"
            >
              {{ index + 1 }}
            </el-button>
          </div>
        </div>

        <div class="question-content" v-if="currentQuestion">
          <div class="question-header">
            <h3>第{{ currentQuestionIndex + 1 }}题 {{ currentQuestion.typeName || getQuestionTypeName(currentQuestion.type) }}</h3>
            <el-tag v-if="currentQuestion.score" type="info">满分{{ currentQuestion.score }}分</el-tag>
          </div>
          <div class="question-text">{{ currentQuestion.content }}</div>
          <div class="options-list" v-if="currentQuestion.options && currentQuestion.options.length > 0">
            <div
              v-for="option in currentQuestion.options"
              :key="option.id"
              :class="['option-item', { selected: isOptionSelected(option) }]"
              @click="selectOption(option)"
            >
              <span class="option-label">{{ option.prefix }}</span>
              <span class="option-content">{{ option.content }}</span>
              <el-icon v-if="isOptionSelected(option)" class="check-icon"><Check /></el-icon>
            </div>
          </div>
          <div v-else class="answer-input">
            <el-input
              v-model="currentAnswer"
              type="textarea"
              :rows="4"
              placeholder="请输入答案"
              @blur="saveAnswer"
            />
          </div>
        </div>

        <div class="exam-footer">
          <el-button @click="prevQuestion" :disabled="currentQuestionIndex === 0">上一题</el-button>
          <el-button v-if="currentQuestionIndex < allQuestions.length - 1" type="primary" @click="nextQuestion">下一题</el-button>
          <el-button v-else type="success" @click="handleSubmitExam">交卷</el-button>
        </div>
      </div>
      <div v-else class="exam-ready">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="考试标题">{{ currentExam?.title }}</el-descriptions-item>
          <el-descriptions-item label="考试时长">{{ examDetail.examInfo?.totalTime || 60 }}分钟</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ currentExam?.startTime }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ currentExam?.endTime }}</el-descriptions-item>
          <el-descriptions-item label="总分">{{ currentExam?.totalScore }}分</el-descriptions-item>
          <el-descriptions-item label="及格分数">{{ currentExam?.passingScore }}分</el-descriptions-item>
        </el-descriptions>
        <div class="exam-notice">
          <el-alert title="考试须知" type="info" :closable="false">
            <ul>
              <li>请在规定时间内完成考试</li>
              <li>考试过程中请勿切换浏览器页面</li>
              <li>答题完成后点击"交卷"按钮提交答案</li>
              <li>交卷后无法再次修改答案</li>
            </ul>
          </el-alert>
        </div>
      </div>
      <template #footer v-if="!examStarted">
        <el-button @click="examDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="startExam">开始考试</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check } from '@element-plus/icons-vue'
import axios from '../utils/axios'

const router = useRouter()

const courses = ref([])
const exams = ref([])
const loading = ref(false)
const examDialogVisible = ref(false)
const currentExam = ref(null)
const examStarted = ref(false)
const examDetail = ref({})
const allQuestions = ref([])
const currentQuestionIndex = ref(0)
const remainingTime = ref(0)
const userAnswers = ref({})
const timer = ref(null)

const filterForm = reactive({
  courseId: null,
  status: 0
})

const currentQuestion = computed(() => allQuestions.value[currentQuestionIndex.value] || null)
const currentAnswer = ref('')

const getQuestionTypeName = (type) => {
  const typeMap = {
    0: '单选题',
    1: '多选题',
    2: '判断题',
    3: '填空题',
    4: '主观题',
    5: '文件题',
    6: '代码题'
  }
  return typeMap[type] || '未知'
}

const getStatusType = (row) => {
  const now = new Date()
  const start = new Date(row.startTime)
  const end = new Date(row.endTime)
  if (now < start) return 'info'
  if (now >= start && now <= end) return 'success'
  return 'danger'
}

const getStatusText = (row) => {
  const now = new Date()
  const start = new Date(row.startTime)
  const end = new Date(row.endTime)
  if (now < start) return '未开始'
  if (now >= start && now <= end) return '进行中'
  return '已结束'
}

const canTakeExam = (row) => {
  const now = new Date()
  const start = new Date(row.startTime)
  const end = new Date(row.endTime)
  return now >= start && now <= end
}

const isExamEnded = (row) => {
  const now = new Date()
  const end = new Date(row.endTime)
  return now > end
}

const handleCourseChange = async (courseId) => {
  if (!courseId) {
    exams.value = []
    return
  }
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
  if (!filterForm.courseId) return
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

const handleStartExam = (row) => {
  currentExam.value = row
  examStarted.value = false
  examDialogVisible.value = true
}

const startExam = async () => {
  try {
    const res = await axios.get(`/exam-center/${currentExam.value.id}/start`)
    examDetail.value = res.data || {}
    const questionList = res.data?.questionList || {}

    const questions = []
    Object.keys(questionList).forEach(type => {
      const typeQuestions = questionList[type] || []
      typeQuestions.forEach(q => {
        q.typeName = type
        questions.push(q)
      })
    })

    allQuestions.value = questions
    userAnswers.value = {}
    currentQuestionIndex.value = 0
    remainingTime.value = (examDetail.value.examInfo?.totalTime || 60) * 60
    examStarted.value = true

    startTimer()
  } catch (error) {
    console.error('Failed to start exam:', error)
    ElMessage.error('获取考试信息失败')
  }
}

const startTimer = () => {
  timer.value = setInterval(() => {
    if (remainingTime.value > 0) {
      remainingTime.value--
    } else {
      clearInterval(timer.value)
      ElMessage.warning('考试时间已到，将自动交卷')
      submitExam()
    }
  }, 1000)
}

const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const getNavButtonType = (question) => {
  const qId = question.id.toString()
  if (userAnswers.value[qId]) {
    return 'success'
  }
  return 'info'
}

const goToQuestion = (index) => {
  saveAnswer()
  currentQuestionIndex.value = index
  loadCurrentAnswer()
}

const selectOption = (option) => {
  if (!currentQuestion.value) return
  const qId = currentQuestion.value.id.toString()

  if (currentQuestion.value.type === 1) {
    if (!userAnswers.value[qId]) {
      userAnswers.value[qId] = []
    }
    const idx = userAnswers.value[qId].indexOf(option.id)
    if (idx > -1) {
      userAnswers.value[qId].splice(idx, 1)
    } else {
      userAnswers.value[qId].push(option.id)
    }
  } else {
    userAnswers.value[qId] = [option.id]
  }

  saveAnswer()
}

const isOptionSelected = (option) => {
  if (!currentQuestion.value) return false
  const qId = currentQuestion.value.id.toString()
  if (Array.isArray(userAnswers.value[qId])) {
    return userAnswers.value[qId].includes(option.id)
  }
  return false
}

const loadCurrentAnswer = () => {
  if (!currentQuestion.value) return
  const qId = currentQuestion.value.id.toString()
  const answer = userAnswers.value[qId]

  if (currentQuestion.value.type === 1 || currentQuestion.value.type === 0 || currentQuestion.value.type === 2) {
    currentAnswer.value = ''
  } else if (answer && answer.length > 0) {
    currentAnswer.value = Array.isArray(answer) ? answer[0] : answer
  } else {
    currentAnswer.value = ''
  }
}

const saveAnswer = async () => {
  if (!currentQuestion.value || !examStarted.value) return

  const qId = currentQuestion.value.id.toString()

  if (currentQuestion.value.type === 1 || currentQuestion.value.type === 0 || currentQuestion.value.type === 2) {
    return
  }

  if (currentAnswer.value) {
    userAnswers.value[qId] = [currentAnswer.value]
  }

  try {
    const answerData = {}
    answerData[currentAnswer.value] = 'answer'
    await axios.post(`/exam-center/${currentExam.value.id}/answer/${qId}`, answerData)
  } catch (error) {
    console.error('Failed to save answer:', error)
  }
}

const prevQuestion = () => {
  saveAnswer()
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
    loadCurrentAnswer()
  }
}

const nextQuestion = () => {
  saveAnswer()
  if (currentQuestionIndex.value < allQuestions.value.length - 1) {
    currentQuestionIndex.value++
    loadCurrentAnswer()
  }
}

const handleSubmitExam = async () => {
  try {
    await ElMessageBox.confirm('确定要交卷吗？交卷后无法修改答案。', '提示', {
      type: 'warning'
    })
    await submitExam()
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
  }
}

const submitExam = async () => {
  try {
    clearInterval(timer.value)
    await axios.post(`/exam-center/${currentExam.value.id}/submit`)
    ElMessage.success('交卷成功')
    examDialogVisible.value = false
    examStarted.value = false
    router.push('/score-analysis')
  } catch (error) {
    console.error('Failed to submit exam:', error)
    ElMessage.error('交卷失败')
  }
}

onMounted(() => {
  fetchCourses()
})

onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value)
  }
})
</script>

<style scoped>
.student-exams {
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

.exam-container {
  padding: 20px;
}

.exam-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.exam-info {
  display: flex;
  gap: 20px;
  font-size: 14px;
}

.question-nav {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.nav-btn {
  min-width: 32px;
}

.nav-btn.current {
  border: 2px solid #409eff;
}

.question-content {
  margin-bottom: 20px;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.question-header h3 {
  margin: 0;
}

.question-text {
  padding: 20px;
  background: #f9fafb;
  border-radius: 8px;
  line-height: 1.8;
  margin-bottom: 20px;
}

.options-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.option-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.option-item:hover {
  border-color: #409eff;
  background: #ecf5ff;
}

.option-item.selected {
  border-color: #67c23a;
  background: #f0f9ff;
}

.option-label {
  font-weight: bold;
  margin-right: 15px;
  min-width: 30px;
}

.option-content {
  flex: 1;
}

.check-icon {
  color: #67c23a;
  font-size: 18px;
}

.answer-input {
  margin-top: 20px;
}

.exam-footer {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.exam-ready {
  padding: 20px;
}

.exam-notice {
  margin-top: 20px;
}

.exam-notice ul {
  margin: 10px 0;
  padding-left: 20px;
}

.exam-notice li {
  line-height: 2;
}
</style>