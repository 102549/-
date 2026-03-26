<template>
  <div class="student-profile">
    <div class="toolbar">
      <h2>学生能力画像</h2>
    </div>

    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="课程选择">
          <el-select v-model="filterForm.courseId" placeholder="请选择课程" @change="handleCourseChange">
            <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="学生选择">
          <el-select v-model="filterForm.studentId" placeholder="请选择学生" :disabled="!filterForm.courseId" filterable>
            <el-option v-for="student in students" :key="student.id" :label="student.username" :value="student.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :loading="loading">查询画像</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div v-if="profile" class="profile-content">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="ability-card">
            <template #header>
              <span>能力概览</span>
            </template>
            <div class="ability-overview">
              <div class="ability-level">
                <el-rate v-model="profile.abilityLevel" disabled show-score text-color="#ff9900" />
                <div class="level-label">能力等级</div>
              </div>
              <el-divider />
              <div class="stats-grid">
                <div class="stat-item">
                  <div class="stat-value">{{ profile.totalAnswers }}</div>
                  <div class="stat-label">总答题数</div>
                </div>
                <div class="stat-item">
                  <div class="stat-value" :class="getAccuracyClass(profile.accuracyRate)">
                    {{ profile.accuracyRate }}%
                  </div>
                  <div class="stat-label">正确率</div>
                </div>
                <div class="stat-item">
                  <div class="stat-value">{{ profile.recommendedDifficulty }}</div>
                  <div class="stat-label">推荐难度</div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card class="difficulty-card">
            <template #header>
              <span>推荐难度分析</span>
            </template>
            <div class="difficulty-analysis">
              <el-progress type="dashboard" :percentage="getDifficultyPercent" :color="difficultyColor">
                <template #default>
                  <div class="difficulty-value">
                    <span class="big-number">{{ profile.recommendedDifficulty }}</span>
                    <span class="suffix">/ 5</span>
                  </div>
                </template>
              </el-progress>
              <div class="difficulty-hints">
                <p v-if="profile.recommendedDifficulty >= 4">学生能力较强，适合挑战高难度题目</p>
                <p v-else-if="profile.recommendedDifficulty >= 3">学生能力中等，建议稳扎稳打</p>
                <p v-else>学生基础需要加强，建议从基础题目开始</p>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card class="trend-card">
            <template #header>
              <span>学习趋势</span>
            </template>
            <div class="trend-content">
              <div class="trend-icon" :class="profile.performanceTrend">
                <el-icon v-if="profile.performanceTrend === 'improving'" :size="40"><TrendCharts /></el-icon>
                <el-icon v-else-if="profile.performanceTrend === 'declining'" :size="40"><DataLine /></el-icon>
                <el-icon v-else :size="40"><Remove /></el-icon>
              </div>
              <div class="trend-label">
                <span v-if="profile.performanceTrend === 'improving'" class="trend-text improving">进步中</span>
                <span v-else-if="profile.performanceTrend === 'declining'" class="trend-text declining">退步中</span>
                <span v-else class="trend-text stable">稳定</span>
              </div>
              <p class="trend-hint">
                {{ getTrendHint(profile.performanceTrend) }}
              </p>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-card class="knowledge-card">
        <template #header>
          <span>知识点掌握情况</span>
        </template>
        <el-table :data="profile.masteryList" border stripe>
          <el-table-column prop="tagName" label="知识点" width="200" />
          <el-table-column prop="questionCount" label="题目数" width="100" />
          <el-table-column prop="correctCount" label="正确数" width="100" />
          <el-table-column label="掌握度" width="300">
            <template #default="{ row }">
              <el-progress
                :percentage="getMasteryPercent(row)"
                :color="getMasteryColor(getMasteryPercent(row))"
                :stroke-width="15"
              />
            </template>
          </el-table-column>
          <el-table-column label="掌握等级">
            <template #default="{ row }">
              <el-tag :type="getMasteryTagType(row)">{{ getMasteryLabel(row) }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card class="history-card">
        <template #header>
          <span>历史答题记录</span>
        </template>
        <el-table :data="answerHistory" border stripe v-loading="historyLoading">
          <el-table-column prop="examId" label="考试ID" width="100" />
          <el-table-column prop="questionId" label="题目ID" width="100" />
          <el-table-column prop="questionContent" label="题目内容" show-overflow-tooltip />
          <el-table-column prop="isCorrect" label="是否正确" width="100">
            <template #default="{ row }">
              <el-tag :type="row.isCorrect ? 'success' : 'danger'">
                {{ row.isCorrect ? '正确' : '错误' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="answerTime" label="答题时间" width="180" />
        </el-table>
        <div class="pagination">
          <el-pagination
            v-model:current-page="historyPage"
            :page-size="historyPageSize"
            :total="historyTotal"
            layout="total, prev, pager, next"
            @current-change="handleHistoryPageChange"
          />
        </div>
      </el-card>
    </div>

    <el-empty v-else-if="hasSearched" description="暂无该学生的能力画像数据" />
    <el-empty v-else description="请选择课程和学生查询能力画像" />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../utils/axios'

const courses = ref([])
const students = ref([])
const profile = ref(null)
const answerHistory = ref([])
const hasSearched = ref(false)
const loading = ref(false)
const historyLoading = ref(false)
const historyPage = ref(1)
const historyPageSize = ref(10)
const historyTotal = ref(0)

const filterForm = reactive({
  courseId: null,
  studentId: null
})

const difficultyColor = computed(() => {
  if (!profile.value) return '#409eff'
  const diff = profile.value.recommendedDifficulty
  if (diff >= 4) return '#67c23a'
  if (diff >= 3) return '#e6a23c'
  return '#f56c6c'
})

const getDifficultyPercent = computed(() => {
  if (!profile.value) return 0
  return (profile.value.recommendedDifficulty / 5) * 100
})

const getAccuracyClass = (accuracy) => {
  if (accuracy >= 80) return 'accuracy-high'
  if (accuracy >= 60) return 'accuracy-medium'
  return 'accuracy-low'
}

const getMasteryPercent = (kp) => {
  if (!kp.questionCount || kp.questionCount === 0) return 0
  if (!kp.correctCount) kp.correctCount = 0
  return Math.round((kp.correctCount / kp.questionCount) * 100)
}

const getMasteryColor = (percent) => {
  if (percent >= 80) return '#67c23a'
  if (percent >= 60) return '#e6a23c'
  return '#f56c6c'
}

const getMasteryTagType = (kp) => {
  const percent = getMasteryPercent(kp)
  if (percent >= 80) return 'success'
  if (percent >= 60) return 'warning'
  return 'danger'
}

const getMasteryLabel = (kp) => {
  const percent = getMasteryPercent(kp)
  if (percent >= 80) return '优秀'
  if (percent >= 60) return '良好'
  if (percent >= 40) return '一般'
  return '较差'
}

const getTrendHint = (trend) => {
  if (trend === 'improving') return '近期学习状态良好，正确率呈上升趋势'
  if (trend === 'declining') return '近期学习状态需要关注，正确率有所下降'
  return '学习状态稳定，建议继续保持'
}

const handleCourseChange = async (courseId) => {
  if (!courseId) return
  filterForm.studentId = null
  try {
    const res = await axios.get(`/courses/getInfo?courseId=${courseId}`)
    if (res.data?.userId) {
      students.value = [{
        id: res.data.userId,
        username: res.data.userName || '教师-' + res.data.userId
      }]
    }
    const listRes = await axios.get('/user/list')
    const allUsers = listRes.data || []
    const teacherId = res.data?.userId
    students.value = allUsers.filter(u => u.role !== '1' && u.id !== teacherId)
  } catch (error) {
    console.error('Failed to fetch students:', error)
  }
}

const handleSearch = async () => {
  if (!filterForm.courseId) {
    ElMessage.warning('请选择课程')
    return
  }
  hasSearched.value = true
  loading.value = true
  try {
    const res = await axios.get(`/exam/adaptive-paper/profile/${filterForm.courseId}`)
    profile.value = res.data

    if (profile.value && profile.value.masteryList) {
      profile.value.masteryList = profile.value.masteryList.map(kp => ({
        ...kp,
        correctCount: Math.round(kp.questionCount * (kp.masteryRate || 0) / 100)
      }))
    }
  } catch (error) {
    console.error('Failed to fetch profile:', error)
    profile.value = null
  } finally {
    loading.value = false
  }
}

const handleHistoryPageChange = async (page) => {
  historyPage.value = page
  await fetchAnswerHistory()
}

const fetchAnswerHistory = async () => {
  if (!filterForm.courseId) return
  historyLoading.value = true
  try {
    answerHistory.value = []
    historyTotal.value = 0
  } catch (error) {
    console.error('Failed to fetch history:', error)
  } finally {
    historyLoading.value = false
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

onMounted(() => {
  fetchCourses()
})
</script>

<style scoped>
.student-profile {
  padding: 20px;
}

.toolbar {
  margin-bottom: 20px;
}

.toolbar h2 {
  margin: 0;
}

.filter-card {
  margin-bottom: 20px;
}

.profile-content {
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.ability-card,
.difficulty-card,
.trend-card,
.knowledge-card,
.history-card {
  margin-bottom: 20px;
}

.ability-overview {
  text-align: center;
}

.ability-level {
  padding: 10px 0;
}

.level-label {
  margin-top: 10px;
  color: #666;
  font-size: 14px;
}

.stats-grid {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.stat-value.accuracy-high { color: #67c23a; }
.stat-value.accuracy-medium { color: #e6a23c; }
.stat-value.accuracy-low { color: #f56c6c; }

.stat-label {
  color: #666;
  font-size: 12px;
  margin-top: 5px;
}

.difficulty-analysis {
  text-align: center;
  padding: 20px 0;
}

.difficulty-value {
  text-align: center;
}

.big-number {
  font-size: 36px;
  font-weight: bold;
  color: #333;
}

.suffix {
  font-size: 16px;
  color: #999;
}

.difficulty-hints {
  margin-top: 20px;
  color: #666;
  font-size: 14px;
}

.trend-content {
  text-align: center;
  padding: 20px 0;
}

.trend-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 15px;
}

.trend-icon.improving {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  color: #fff;
}

.trend-icon.declining {
  background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
  color: #fff;
}

.trend-icon.stable {
  background: linear-gradient(135deg, #909399 0%, #a6a9ad 100%);
  color: #fff;
}

.trend-label {
  margin-bottom: 10px;
}

.trend-text {
  font-size: 18px;
  font-weight: bold;
}

.trend-text.improving { color: #67c23a; }
.trend-text.declining { color: #f56c6c; }
.trend-text.stable { color: #909399; }

.trend-hint {
  color: #666;
  font-size: 14px;
  padding: 0 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
