<template>
  <div class="anti-cheat-monitor">
    <div class="toolbar">
      <h2>作弊风险监控</h2>
      <div class="toolbar-right">
        <el-select v-model="selectedExam" placeholder="请选择考试" @change="handleExamChange">
          <el-option v-for="exam in exams" :key="exam.id" :label="exam.title" :value="exam.id" />
        </el-select>
        <el-button type="primary" @click="handleRefresh" :loading="refreshLoading">
          <el-icon><Refresh /></el-icon> 刷新
        </el-button>
      </div>
    </div>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card normal">
          <div class="stat-icon"><el-icon><CircleCheck /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.normalCount }}</div>
            <div class="stat-label">正常考生</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card low">
          <div class="stat-icon"><el-icon><Warning /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.lowRiskCount }}</div>
            <div class="stat-label">低风险</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card medium">
          <div class="stat-icon"><el-icon><WarningFilled /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.mediumRiskCount }}</div>
            <div class="stat-label">中风险</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card high">
          <div class="stat-icon"><el-icon><CircleClose /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.highRiskCount }}</div>
            <div class="stat-label">高风险</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="risk-distribution-card">
      <template #header>
        <span>风险等级分布</span>
      </template>
      <div class="chart-container">
        <div class="pie-chart">
          <el-progress type="dashboard" :percentage="stats.highRiskPercent" :color="riskColors.high">
            <template #default>
              <span class="progress-text">高风险<br/>{{ stats.highRiskPercent }}%</span>
            </template>
          </el-progress>
        </div>
        <div class="pie-chart">
          <el-progress type="dashboard" :percentage="stats.mediumRiskPercent" :color="riskColors.medium">
            <template #default>
              <span class="progress-text">中风险<br/>{{ stats.mediumRiskPercent }}%</span>
            </template>
          </el-progress>
        </div>
        <div class="pie-chart">
          <el-progress type="dashboard" :percentage="stats.lowRiskPercent" :color="riskColors.low">
            <template #default>
              <span class="progress-text">低风险<br/>{{ stats.lowRiskPercent }}%</span>
            </template>
          </el-progress>
        </div>
        <div class="pie-chart">
          <el-progress type="dashboard" :percentage="stats.normalPercent" :color="riskColors.normal">
            <template #default>
              <span class="progress-text">正常<br/>{{ stats.normalPercent }}%</span>
            </template>
          </el-progress>
        </div>
      </div>
    </el-card>

    <el-card class="alert-card" v-if="highRiskStudents.length > 0">
      <template #header>
        <div class="card-header">
          <span><el-icon class="alert-icon"><Bell /></el-icon> 高风险预警 (需要立即处理)</span>
          <el-badge :value="highRiskStudents.length" type="danger" />
        </div>
      </template>
      <el-table :data="highRiskStudents" border stripe>
        <el-table-column prop="studentId" label="学生ID" width="100" />
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="anomalyCount" label="异常特征数" width="120">
          <template #default="{ row }">
            <el-tag type="danger">{{ row.anomalyCount }} 项</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="anomalies" label="异常详情">
          <template #default="{ row }">
            <div class="anomaly-tags">
              <el-tag v-for="(anomaly, index) in row.anomalies" :key="index" type="danger" size="small" class="anomaly-tag">
                {{ anomaly }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="alertTime" label="预警时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleViewDetail(row)">查看详情</el-button>
            <el-button size="small" type="warning" @click="handleSendWarning(row)">发送警告</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card class="students-card">
      <template #header>
        <span>全部考生行为监控</span>
      </template>
      <el-table :data="allStudents" border stripe v-loading="tableLoading">
        <el-table-column prop="userId" label="学生ID" width="100" />
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="riskLevel" label="风险等级" width="120">
          <template #default="{ row }">
            <el-tag :type="getRiskTagType(row.riskLevel)">
              {{ getRiskLevelLabel(row.riskLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="anomalyCount" label="异常数" width="100" />
        <el-table-column prop="mouseAvgSpeed" label="鼠标速度" width="120">
          <template #default="{ row }">
            <span :class="getAnomalyClass(row.mouseAvgSpeed, 500)">
              {{ row.mouseAvgSpeed ? row.mouseAvgSpeed.toFixed(0) : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="clickFrequency" label="点击频率" width="120">
          <template #default="{ row }">
            <span :class="getAnomalyClass(row.clickFrequency, 60)">
              {{ row.clickFrequency ? row.clickFrequency.toFixed(0) : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="screenSwitchCount" label="切屏次数" width="120">
          <template #default="{ row }">
            <span :class="getAnomalyClass(row.screenSwitchCount, 5, true)">
              {{ row.screenSwitchCount || 0 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="answerModifyCount" label="改题次数" width="120">
          <template #default="{ row }">
            <span :class="getAnomalyClass(row.answerModifyCount, 10, true)">
              {{ row.answerModifyCount || 0 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="ipAddress" label="IP地址" width="150" />
        <el-table-column prop="answerHour" label="答题时段" width="100">
          <template #default="{ row }">
            <span :class="{ 'anomaly-text': row.answerHour >= 0 && row.answerHour <= 5 }">
              {{ row.answerHour !== null ? `${row.answerHour}:00` : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleViewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailDialogVisible" title="考生行为详情" width="700px">
      <div v-if="currentStudent" class="detail-content">
        <el-descriptions title="基本信息" :column="2" border>
          <el-descriptions-item label="学生ID">{{ currentStudent.userId }}</el-descriptions-item>
          <el-descriptions-item label="学生姓名">{{ currentStudent.studentName || '未知' }}</el-descriptions-item>
          <el-descriptions-item label="风险等级">
            <el-tag :type="getRiskTagType(currentStudent.riskLevel)">
              {{ getRiskLevelLabel(currentStudent.riskLevel) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="异常特征数">{{ currentStudent.anomalyCount }}</el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <h4>行为特征分析</h4>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="鼠标平均移动速度">
            <span :class="getAnomalyClass(currentStudent.mouseAvgSpeed, 500)">
              {{ currentStudent.mouseAvgSpeed ? currentStudent.mouseAvgSpeed.toFixed(2) + ' px/s' : '未采集' }}
            </span>
            <span class="threshold-hint"> (阈值: 500 px/s)</span>
          </el-descriptions-item>
          <el-descriptions-item label="鼠标点击频率">
            <span :class="getAnomalyClass(currentStudent.clickFrequency, 60)">
              {{ currentStudent.clickFrequency ? currentStudent.clickFrequency.toFixed(2) + ' 次/分' : '未采集' }}
            </span>
            <span class="threshold-hint"> (阈值: 60 次/分)</span>
          </el-descriptions-item>
          <el-descriptions-item label="切屏次数">
            <span :class="getAnomalyClass(currentStudent.screenSwitchCount, 5, true)">
              {{ currentStudent.screenSwitchCount || 0 }} 次
            </span>
            <span class="threshold-hint"> (阈值: 5 次)</span>
          </el-descriptions-item>
          <el-descriptions-item label="答案修改次数">
            <span :class="getAnomalyClass(currentStudent.answerModifyCount, 10, true)">
              {{ currentStudent.answerModifyCount || 0 }} 次
            </span>
            <span class="threshold-hint"> (阈值: 10 次)</span>
          </el-descriptions-item>
          <el-descriptions-item label="答题时长">
            {{ currentStudent.answerDuration ? formatDuration(currentStudent.answerDuration) : '未采集' }}
          </el-descriptions-item>
          <el-descriptions-item label="IP地址">{{ currentStudent.ipAddress || '未记录' }}</el-descriptions-item>
          <el-descriptions-item label="答题时段">
            <span :class="{ 'anomaly-text': currentStudent.answerHour >= 0 && currentStudent.answerHour <= 5 }">
              {{ currentStudent.answerHour !== null ? `${currentStudent.answerHour}:00` : '未记录' }}
            </span>
          </el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <h4>异常特征列表</h4>
        <div v-if="currentStudent.anomalies && currentStudent.anomalies.length > 0" class="anomaly-list">
          <el-alert
            v-for="(anomaly, index) in currentStudent.anomalies"
            :key="index"
            :title="anomaly"
            type="error"
            :closable="false"
            show-icon
            class="anomaly-item"
          />
        </div>
        <el-empty v-else description="未检测到异常特征" />
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="warning" @click="handleSendWarning(currentStudent)">发送警告</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../utils/axios'

const exams = ref([])
const selectedExam = ref(null)
const allStudents = ref([])
const highRiskStudents = ref([])
const refreshLoading = ref(false)
const tableLoading = ref(false)
const detailDialogVisible = ref(false)
const currentStudent = ref(null)

const stats = reactive({
  totalCount: 0,
  normalCount: 0,
  lowRiskCount: 0,
  mediumRiskCount: 0,
  highRiskCount: 0,
  normalPercent: 0,
  lowRiskPercent: 0,
  mediumRiskPercent: 0,
  highRiskPercent: 0
})

const riskColors = {
  normal: '#67c23a',
  low: '#e6a23c',
  medium: '#f56c6c',
  high: '#c45656'
}

const getRiskTagType = (riskLevel) => {
  if (!riskLevel) return 'success'
  const level = riskLevel.toUpperCase()
  if (level === 'HIGH') return 'danger'
  if (level === 'MEDIUM') return 'warning'
  if (level === 'LOW') return 'info'
  return 'success'
}

const getRiskLevelLabel = (riskLevel) => {
  if (!riskLevel) return '正常'
  const level = riskLevel.toUpperCase()
  if (level === 'HIGH') return '高风险'
  if (level === 'MEDIUM') return '中风险'
  if (level === 'LOW') return '低风险'
  return '正常'
}

const getAnomalyClass = (value, threshold, inclusive = false) => {
  if (value === null || value === undefined) return ''
  if (inclusive) {
    return value >= threshold ? 'anomaly-text' : ''
  }
  return value > threshold ? 'anomaly-text' : ''
}

const formatDuration = (seconds) => {
  if (!seconds) return '-'
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  if (hours > 0) {
    return `${hours}小时${minutes}分钟${secs}秒`
  }
  if (minutes > 0) {
    return `${minutes}分钟${secs}秒`
  }
  return `${secs}秒`
}

const handleExamChange = async (examId) => {
  if (!examId) return
  await fetchAlertData(examId)
}

const handleRefresh = async () => {
  if (!selectedExam.value) {
    ElMessage.warning('请先选择考试')
    return
  }
  await fetchAlertData(selectedExam.value)
}

const fetchAlertData = async (examId) => {
  refreshLoading.value = true
  tableLoading.value = true
  try {
    const res = await axios.get(`/exam/anti-cheat/alerts/${examId}`)
    const students = res.data || []

    allStudents.value = students
    highRiskStudents.value = students.filter(s => s.riskLevel === 'HIGH' || s.riskLevel?.toUpperCase() === 'HIGH')

    stats.totalCount = students.length
    stats.normalCount = students.filter(s => !s.riskLevel || s.riskLevel.toUpperCase() === 'NORMAL').length
    stats.lowRiskCount = students.filter(s => s.riskLevel?.toUpperCase() === 'LOW').length
    stats.mediumRiskCount = students.filter(s => s.riskLevel?.toUpperCase() === 'MEDIUM').length
    stats.highRiskCount = students.filter(s => s.riskLevel?.toUpperCase() === 'HIGH').length

    if (stats.totalCount > 0) {
      stats.normalPercent = Math.round(stats.normalCount / stats.totalCount * 100)
      stats.lowRiskPercent = Math.round(stats.lowRiskCount / stats.totalCount * 100)
      stats.mediumRiskPercent = Math.round(stats.mediumRiskCount / stats.totalCount * 100)
      stats.highRiskPercent = Math.round(stats.highRiskCount / stats.totalCount * 100)
    } else {
      stats.normalPercent = 0
      stats.lowRiskPercent = 0
      stats.mediumRiskPercent = 0
      stats.highRiskPercent = 0
    }
  } catch (error) {
    console.error('Failed to fetch alert data:', error)
    ElMessage.error('获取预警数据失败')
  } finally {
    refreshLoading.value = false
    tableLoading.value = false
  }
}

const handleViewDetail = (row) => {
  currentStudent.value = row
  detailDialogVisible.value = true
}

const handleSendWarning = async (student) => {
  try {
    ElMessage.success(`已向学生 ${student.userId} 发送警告消息`)
  } catch (error) {
    console.error('Failed to send warning:', error)
    ElMessage.error('发送警告失败')
  }
}

const fetchCourses = async () => {
  try {
    const res = await axios.get('/courses/list/teacher?status=0&currentPage=1')
    const courses = res.data?.records || res.data || []
    if (courses.length > 0) {
      const courseId = courses[0].id
      const examRes = await axios.get(`/exam/exam-info/list/${courseId}`)
      exams.value = examRes.data?.records || examRes.data || []
    }
  } catch (error) {
    console.error('Failed to fetch courses:', error)
  }
}

onMounted(() => {
  fetchCourses()
})
</script>

<style scoped>
.anti-cheat-monitor {
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

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-card.normal { border-left: 4px solid #67c23a; }
.stat-card.low { border-left: 4px solid #e6a23c; }
.stat-card.medium { border-left: 4px solid #f56c6c; }
.stat-card.high { border-left: 4px solid #c45656; }

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

.stat-card.normal .stat-icon { background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%); }
.stat-card.low .stat-icon { background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%); }
.stat-card.medium .stat-icon { background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%); }
.stat-card.high .stat-icon { background: linear-gradient(135deg, #c45656 0%, #e66a6a 100%); }

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

.risk-distribution-card {
  margin-bottom: 20px;
}

.chart-container {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
}

.pie-chart {
  text-align: center;
}

.progress-text {
  font-size: 14px;
  line-height: 1.3;
}

.alert-card {
  margin-bottom: 20px;
  border-color: #f56c6c;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.alert-icon {
  color: #f56c6c;
  font-size: 20px;
}

.anomaly-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.anomaly-tag {
  margin: 2px;
}

.anomaly-text {
  color: #f56c6c;
  font-weight: bold;
}

.threshold-hint {
  color: #909399;
  font-size: 12px;
  margin-left: 10px;
}

.anomaly-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.anomaly-item {
  margin-bottom: 5px;
}
</style>
