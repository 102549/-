<template>
  <div class="dashboard">
    <h2>系统概览</h2>
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon users"><el-icon><User /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.users }}</div>
            <div class="stat-label">用户总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon courses"><el-icon><Reading /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.courses }}</div>
            <div class="stat-label">课程总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon exams"><el-icon><Document /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.exams }}</div>
            <div class="stat-label">考试总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon questions"><el-icon><QuestionFilled /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.questions }}</div>
            <div class="stat-label">题目总数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button v-if="showAdaptivePaperBtn" type="primary" @click="$router.push('/adaptive-paper')">自适应组卷</el-button>
            <el-button v-if="showSubjectiveGradingBtn" type="success" @click="$router.push('/subjective-grading')">主观题批改</el-button>
            <el-button v-if="showAntiCheatMonitorBtn" type="warning" @click="$router.push('/anti-cheat-monitor')">作弊监控</el-button>
            <el-button v-if="showStudentProfileBtn" type="info" @click="$router.push('/student-profile')">能力画像</el-button>
            <el-button v-if="showExamMonitorBtn" type="primary" @click="$router.push('/exam-monitor')">考试监控</el-button>
            <el-button v-if="showUsersBtn" type="danger" @click="$router.push('/users')">用户管理</el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>系统状态</span>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="认证服务状态">
              <el-tag :type="serviceStatus.auth ? 'success' : 'danger'">
                {{ serviceStatus.auth ? '运行中' : '未连接' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="用户服务状态">
              <el-tag :type="serviceStatus.user ? 'success' : 'danger'">
                {{ serviceStatus.user ? '运行中' : '未连接' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="考试服务状态">
              <el-tag :type="serviceStatus.exam ? 'success' : 'danger'">
                {{ serviceStatus.exam ? '运行中' : '未连接' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row" v-if="userRole === 'STUDENT'">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <span>学生操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/exams')">参加考试</el-button>
            <el-button type="success" @click="$router.push('/score-analysis')">查看成绩</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import axios from '../utils/axios'
import { initUserRole, UserRole } from '../utils/roleConstants'

const userRole = computed(() => initUserRole())

const showAdaptivePaperBtn = computed(() => userRole.value === UserRole.TEACHER)
const showSubjectiveGradingBtn = computed(() => userRole.value === UserRole.TEACHER)
const showAntiCheatMonitorBtn = computed(() => [UserRole.ADMIN, UserRole.TEACHER].includes(userRole.value))
const showStudentProfileBtn = computed(() => userRole.value === UserRole.TEACHER)
const showExamMonitorBtn = computed(() => [UserRole.ADMIN, UserRole.TEACHER].includes(userRole.value))
const showUsersBtn = computed(() => userRole.value === UserRole.ADMIN)

const stats = ref({
  users: 0,
  courses: 0,
  exams: 0,
  questions: 0
})

const serviceStatus = reactive({
  auth: false,
  user: false,
  exam: false
})

const checkServices = async () => {
  const checkAuth = async () => {
    try {
      await axios.get('/oauth/token_key')
      return true
    } catch (e) {
      return false
    }
  }

  const checkUser = async () => {
    try {
      await axios.get('/user/list')
      return true
    } catch (e) {
      return false
    }
  }

  const checkExam = async () => {
    try {
      await axios.get('/tags/1/list')
      return true
    } catch (e) {
      return false
    }
  }

  const [authOk, userOk, examOk] = await Promise.all([checkAuth(), checkUser(), checkExam()])
  serviceStatus.auth = authOk
  serviceStatus.user = userOk
  serviceStatus.exam = examOk
}

const fetchStats = async () => {
  try {
    const coursesRes = await axios.get('/courses/list/teacher?status=0&currentPage=1').catch(e => {
      console.error('Courses API error:', e)
      return { data: { code: 0, data: { records: [] } } }
    })
    const coursesData = coursesRes.data?.data
    const courses = coursesData?.records || coursesData || []
    stats.value.courses = Array.isArray(courses) ? courses.length : 0
  } catch (error) {
    console.error('Failed to fetch courses:', error)
  }

  try {
    const usersRes = await axios.get('/user/list').catch(e => {
      console.error('Users API error:', e)
      return { data: { code: 0, data: [] } }
    })
    const usersData = usersRes.data?.data
    const users = Array.isArray(usersData) ? usersData : (usersData?.records || [])
    stats.value.users = Array.isArray(users) ? users.length : 0
  } catch (error) {
    console.error('Failed to fetch users:', error)
    stats.value.users = 0
  }

  if (stats.value.courses > 0) {
    await fetchExamsAndQuestions()
  }
}

const fetchExamsAndQuestions = async () => {
  try {
    let totalExams = 0
    let totalQuestions = 0

    const coursesRes = await axios.get('/courses/list/teacher?status=0&currentPage=1').catch(() => ({ data: { code: 0, data: { records: [] } } }))
    const coursesData = coursesRes.data?.data
    const courses = coursesData?.records || coursesData || []

    for (const course of courses.slice(0, 5)) {
      try {
        const [examRes, questionRes] = await Promise.all([
          axios.get(`/exam/exam-info/list/${course.id}`).catch(() => ({ data: { code: 0, data: [] } })),
          axios.get(`/exam/question/list/${course.id}?currentPage=1`).catch(() => ({ data: { code: 0, data: [] } }))
        ])

        const examData = examRes.data?.data
        const questionData = questionRes.data?.data
        const exams = Array.isArray(examData) ? examData : (examData?.records || [])
        const questions = Array.isArray(questionData) ? questionData : (questionData?.records || [])

        totalExams += Array.isArray(exams) ? exams.length : 0
        totalQuestions += Array.isArray(questions) ? questions.length : 0
      } catch (e) {
        console.error('Failed to fetch course data:', e)
      }
    }

    stats.value.exams = totalExams
    stats.value.questions = totalQuestions
  } catch (error) {
    console.error('Failed to fetch exams and questions:', error)
  }
}

onMounted(() => {
  checkServices()
  fetchStats()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

h2 {
  margin-bottom: 20px;
  color: #333;
}

.stats-row {
  margin-bottom: 20px;
}

.charts-row {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.chart-card {
  height: 100%;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  font-size: 28px;
  color: #fff;
}

.stat-icon.users { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-icon.courses { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.stat-icon.exams { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.stat-icon.questions { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  color: #999;
  font-size: 14px;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
</style>