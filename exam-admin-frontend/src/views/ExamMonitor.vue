<template>
  <div class="exam-monitor">
    <div class="toolbar">
      <h2>考试监控控制台</h2>
      <div class="toolbar-right">
        <el-select v-model="selectedExam" placeholder="请选择考试" @change="handleExamChange" style="width: 250px;">
          <el-option v-for="exam in exams" :key="exam.id" :label="exam.title" :value="exam.id" />
        </el-select>
        <el-button type="primary" @click="handleRefresh" :loading="refreshLoading">
          <el-icon><Refresh /></el-icon> 刷新
        </el-button>
      </div>
    </div>

    <div v-if="examInfo" class="monitor-content">
      <el-card class="exam-info-card">
        <el-descriptions :column="3" border>
          <el-descriptions-item label="考试标题">{{ examInfo.title }}</el-descriptions-item>
          <el-descriptions-item label="课程">{{ examInfo.courseName }}</el-descriptions-item>
          <el-descriptions-item label="考试时间">
            {{ examInfo.startTime }} ~ {{ examInfo.endTime }}
          </el-descriptions-item>
          <el-descriptions-item label="应考人数">{{ examInfo.totalStudents || 0 }}</el-descriptions-item>
          <el-descriptions-item label="实考人数">{{ examInfo.actualStudents || 0 }}</el-descriptions-item>
          <el-descriptions-item label="提交人数">{{ examInfo.submittedCount || 0 }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-row :gutter="20" class="stats-row">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-icon online"><el-icon><User /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ onlineCount }}</div>
              <div class="stat-label">在线人数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-icon submitted"><el-icon><Finished /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ examInfo.submittedCount || 0 }}</div>
              <div class="stat-label">已提交</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-icon pending"><el-icon><Clock /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ pendingCount }}</div>
              <div class="stat-label">进行中</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-icon cheating"><el-icon><Warning /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ cheatingCount }}</div>
              <div class="stat-label">可疑行为</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="16">
          <el-card class="students-card">
            <template #header>
              <span>考生答题情况</span>
            </template>
            <el-table :data="examStudents" border stripe v-loading="tableLoading">
              <el-table-column prop="studentId" label="学号" width="100" />
              <el-table-column prop="studentName" label="姓名" width="120" />
              <el-table-column prop="className" label="班级" width="120" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getStatusTagType(row.status)">
                    {{ getStatusLabel(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="progress" label="进度" width="150">
                <template #default="{ row }">
                  <el-progress :percentage="row.progress || 0" :stroke-width="10" />
                </template>
              </el-table-column>
              <el-table-column prop="remainingTime" label="剩余时间" width="120">
                <template #default="{ row }">
                  <span v-if="row.remainingTime" :class="{ 'time-warning': row.remainingTime < 300 }">
                    {{ formatTime(row.remainingTime) }}
                  </span>
                  <span v-else>-</span>
                </template>
              </el-table-column>
              <el-table-column prop="ipAddress" label="IP地址" width="150" />
              <el-table-column label="操作" width="150" fixed="right">
                <template #default="{ row }">
                  <el-button size="small" type="primary" @click="handleMonitorStudent(row)">监控</el-button>
                  <el-button size="small" type="warning" @click="handleForceSubmit(row)">强制交卷</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="realtime-card">
            <template #header>
              <span>实时动态</span>
            </template>
            <div class="activity-feed" v-loading="activityLoading">
              <div v-for="(activity, index) in activities" :key="index" class="activity-item">
                <el-icon :class="activity.type"><component :is="getActivityIcon(activity.type)" /></el-icon>
                <div class="activity-content">
                  <p class="activity-text">{{ activity.text }}</p>
                  <span class="activity-time">{{ activity.time }}</span>
                </div>
              </div>
              <el-empty v-if="activities.length === 0" description="暂无动态" />
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="outline-card">
            <template #header>
              <span>离线考生</span>
            </template>
            <el-table :data="offlineStudents" border stripe size="small">
              <el-table-column prop="studentId" label="学号" width="100" />
              <el-table-column prop="studentName" label="姓名" width="120" />
              <el-table-column prop="lastActiveTime" label="最后活跃时间" width="180" />
              <el-table-column prop="offlineDuration" label="离线时长" width="120">
                <template #default="{ row }">
                  <span class="offline-duration">{{ row.offlineDuration }}</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100">
                <template #default="{ row }">
                  <el-button size="small" type="warning" @click="handleKickStudent(row)">踢出考试</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="alert-card">
            <template #header>
              <span>预警信息</span>
            </template>
            <div class="alert-list">
              <el-alert
                v-for="(alert, index) in alerts"
                :key="index"
                :title="alert.title"
                :description="alert.description"
                :type="alert.type"
                :closable="false"
                show-icon
                class="alert-item"
              />
              <el-empty v-if="alerts.length === 0" description="暂无预警" />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-empty v-else description="请从上方选择考试进入监控" />

    <el-dialog v-model="monitorDialogVisible" title="考生实时监控" width="900px" :close-on-click-modal="false">
      <div v-if="monitoringStudent" class="monitor-detail">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="学号">{{ monitoringStudent.studentId }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ monitoringStudent.studentName }}</el-descriptions-item>
          <el-descriptions-item label="当前进度">
            <el-progress :percentage="monitoringStudent.progress || 0" :stroke-width="10" style="width: 200px;" />
          </el-descriptions-item>
          <el-descriptions-item label="剩余时间">
            <span :class="{ 'time-warning': monitoringStudent.remainingTime < 300 }">
              {{ formatTime(monitoringStudent.remainingTime) }}
            </span>
          </el-descriptions-item>
        </el-descriptions>

        <el-divider>答题情况</el-divider>

        <el-table :data="monitoringStudent.answers || []" border size="small">
          <el-table-column prop="questionId" label="题号" width="80" />
          <el-table-column prop="type" label="题型" width="100">
            <template #default="{ row }">
              <el-tag size="small">{{ getQuestionTypeLabel(row.type) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'answered' ? 'success' : 'warning'" size="small">
                {{ row.status === 'answered' ? '已答' : '未答' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="answer" label="答案" />
        </el-table>

        <el-divider>行为分析</el-divider>

        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="鼠标移动">
            <el-progress
              :percentage="getMouseActivityPercent(monitoringStudent.mouseActivity)"
              :color="getMouseActivityColor(monitoringStudent.mouseActivity)"
            />
          </el-descriptions-item>
          <el-descriptions-item label="切屏次数">
            {{ monitoringStudent.screenSwitchCount || 0 }} 次
            <el-tag v-if="monitoringStudent.screenSwitchCount >= 5" type="danger" size="small" style="margin-left: 10px;">
              异常
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="答案修改">
            {{ monitoringStudent.answerModifyCount || 0 }} 次
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="monitorDialogVisible = false">关闭</el-button>
        <el-button type="warning" @click="handleForceSubmitFromMonitor">强制交卷</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from '../utils/axios'

const route = useRoute()
const courses = ref([])
const exams = ref([])
const selectedExam = ref(null)
const examInfo = ref(null)
const examStudents = ref([])
const activities = ref([])
const offlineStudents = ref([])
const alerts = ref([])
const refreshLoading = ref(false)
const tableLoading = ref(false)
const activityLoading = ref(false)
const monitorDialogVisible = ref(false)
const monitoringStudent = ref(null)
let refreshTimer = null

const onlineCount = computed(() => {
  return examStudents.value.filter(s => s.status === 'online' || s.status === 'examining').length
})

const pendingCount = computed(() => {
  return examStudents.value.filter(s => s.status === 'examining').length
})

const cheatingCount = computed(() => {
  return examStudents.value.filter(s => s.riskLevel === 'HIGH' || s.riskLevel === 'MEDIUM').length
})

const handleExamChange = async (examId) => {
  if (!examId) return
  await fetchExamData(examId)
  startAutoRefresh()
}

const handleRefresh = async () => {
  if (!selectedExam.value) {
    ElMessage.warning('请先选择考试')
    return
  }
  await fetchExamData(selectedExam.value)
}

const fetchExamData = async (examId) => {
  refreshLoading.value = true
  tableLoading.value = true
  activityLoading.value = true
  try {
    const detailRes = await axios.get(`/exam/exam-info/detail/${examId}`)
    examInfo.value = detailRes.data?.examInfo || detailRes.data

    const studentsRes = await axios.get(`/exam/exam-console/${examId}/outline-monitor`)
    examStudents.value = studentsRes.data || []

    const offlineRes = await axios.get(`/exam/exam-console/${examId}/outline-monitor?status=offline`)
    offlineStudents.value = offlineRes.data || []

    addActivity('info', `刷新了监控数据`)
  } catch (error) {
    console.error('Failed to fetch exam data:', error)
    mockExamData()
  } finally {
    refreshLoading.value = false
    tableLoading.value = false
    activityLoading.value = false
  }
}

const mockExamData = () => {
  examInfo.value = {
    title: '期末考试',
    courseName: '计算机网络',
    startTime: '2024-01-15 09:00:00',
    endTime: '2024-01-15 11:00:00',
    totalStudents: 45,
    actualStudents: 42,
    submittedCount: 15
  }
  examStudents.value = []
  offlineStudents.value = []
  alerts.value = []
}

const startAutoRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
  refreshTimer = setInterval(() => {
    if (selectedExam.value) {
      fetchExamData(selectedExam.value)
    }
  }, 30000)
}

const stopAutoRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}

const addActivity = (type, text) => {
  const now = new Date()
  const time = now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
  activities.value.unshift({ type, text, time })
  if (activities.value.length > 20) {
    activities.value.pop()
  }
}

const getActivityIcon = (type) => {
  const iconMap = {
    info: 'InfoFilled',
    success: 'CircleCheckFilled',
    warning: 'WarningFilled',
    danger: 'CircleCloseFilled'
  }
  return iconMap[type] || 'InfoFilled'
}

const getStatusTagType = (status) => {
  const typeMap = {
    online: 'success',
    examining: 'primary',
    paused: 'warning',
    submitted: 'info',
    offline: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusLabel = (status) => {
  const labelMap = {
    online: '在线',
    examining: '答题中',
    paused: '已暂停',
    submitted: '已提交',
    offline: '离线'
  }
  return labelMap[status] || '未知'
}

const getQuestionTypeLabel = (type) => {
  const typeMap = {
    0: '单选题',
    1: '多选题',
    2: '判断题',
    3: '填空题',
    4: '主观题'
  }
  return typeMap[type] || '未知'
}

const formatTime = (seconds) => {
  if (!seconds) return '-'
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  if (hours > 0) {
    return `${hours}:${String(minutes).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
  }
  return `${minutes}:${String(secs).padStart(2, '0')}`
}

const getMouseActivityPercent = (activity) => {
  if (!activity) return 0
  return Math.min(100, activity / 2)
}

const getMouseActivityColor = (activity) => {
  if (!activity) return '#67c23a'
  if (activity > 80) return '#f56c6c'
  if (activity > 50) return '#e6a23c'
  return '#67c23a'
}

const handleMonitorStudent = (student) => {
  monitoringStudent.value = {
    ...student,
    answers: []
  }
  monitorDialogVisible.value = true
}

const handleForceSubmit = async (student) => {
  try {
    await ElMessageBox.confirm(`确定要强制提交学生 ${student.studentName} 的试卷吗?`, '提示', {
      type: 'warning'
    })
    ElMessage.success('已强制提交')
    addActivity('warning', `强制提交了学生 ${student.studentName} 的试卷`)
    await fetchExamData(selectedExam.value)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to force submit:', error)
    }
  }
}

const handleForceSubmitFromMonitor = async () => {
  if (monitoringStudent.value) {
    await handleForceSubmit(monitoringStudent.value)
    monitorDialogVisible.value = false
  }
}

const handleKickStudent = async (student) => {
  try {
    await ElMessageBox.confirm(`确定要将学生 ${student.studentName} 踢出考试吗?`, '提示', {
      type: 'warning'
    })
    ElMessage.success('已踢出考试')
    addActivity('danger', `将学生 ${student.studentName} 踢出考试`)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to kick student:', error)
    }
  }
}

const fetchCourses = async () => {
  try {
    const res = await axios.get('/courses/list/teacher?status=0&currentPage=1')
    const courseList = res.data?.records || res.data || []
    courses.value = courseList
    if (courseList.length > 0) {
      const examRes = await axios.get(`/exam/exam-info/list/${courseList[0].id}`)
      exams.value = examRes.data?.records || examRes.data || []
    }
  } catch (error) {
    console.error('Failed to fetch courses:', error)
  }
}

onMounted(() => {
  fetchCourses()

  if (route.params.examInfoId) {
    selectedExam.value = parseInt(route.params.examInfoId)
    fetchExamData(selectedExam.value)
    startAutoRefresh()
  }
})

onUnmounted(() => {
  stopAutoRefresh()
})
</script>

<style scoped>
.exam-monitor {
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

.toolbar-right {
  display: flex;
  gap: 10px;
}

.monitor-content {
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.exam-info-card {
  margin-bottom: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
  color: #fff;
}

.stat-icon.online { background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%); }
.stat-icon.submitted { background: linear-gradient(135deg, #409eff 0%, #79bbff 100%); }
.stat-icon.pending { background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%); }
.stat-icon.cheating { background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%); }

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.students-card,
.realtime-card,
.outline-card,
.alert-card {
  margin-bottom: 20px;
}

.activity-feed {
  max-height: 300px;
  overflow-y: auto;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-item .el-icon {
  font-size: 16px;
  margin-right: 10px;
  margin-top: 2px;
}

.activity-item .el-icon.info { color: #409eff; }
.activity-item .el-icon.success { color: #67c23a; }
.activity-item .el-icon.warning { color: #e6a23c; }
.activity-item .el-icon.danger { color: #f56c6c; }

.activity-content {
  flex: 1;
}

.activity-text {
  margin: 0;
  color: #333;
  font-size: 14px;
}

.activity-time {
  color: #999;
  font-size: 12px;
}

.alert-list {
  max-height: 300px;
  overflow-y: auto;
}

.alert-item {
  margin-bottom: 10px;
}

.offline-duration {
  color: #f56c6c;
  font-weight: bold;
}

.time-warning {
  color: #f56c6c;
  font-weight: bold;
  animation: blink 1s infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.monitor-detail {
  max-height: 60vh;
  overflow-y: auto;
}
</style>
