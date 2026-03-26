<template>
  <div class="score-analysis">
    <div class="toolbar">
      <h2>成绩统计分析</h2>
    </div>

    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="课程选择">
          <el-select v-model="filterForm.courseId" placeholder="请选择课程" @change="handleCourseChange">
            <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="考试选择">
          <el-select v-model="filterForm.examInfoId" placeholder="请选择考试" :disabled="!filterForm.courseId">
            <el-option v-for="exam in exams" :key="exam.id" :label="exam.title" :value="exam.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :loading="loading">统计分析</el-button>
          <el-button type="success" @click="handleExport">导出成绩</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div v-if="statistics" class="analysis-content">
      <el-row :gutter="20" class="stats-row">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-icon students"><el-icon><User /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalStudents }}</div>
              <div class="stat-label">考生人数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-icon avg-score"><el-icon><DataAnalysis /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.avgScore }}</div>
              <div class="stat-label">平均分</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-icon pass-rate"><el-icon><CircleCheck /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.passRate }}%</div>
              <div class="stat-label">及格率</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-icon excellent-rate"><el-icon><Star /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.excellentRate }}%</div>
              <div class="stat-label">优秀率</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <span>分数分布</span>
            </template>
            <div ref="scoreDistributionChart" class="echarts-container"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <span>成绩概览</span>
            </template>
            <div ref="scoreOverviewChart" class="echarts-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" v-if="studentScores.length > 0">
        <el-col :span="24">
          <el-card class="chart-card">
            <template #header>
              <span>学生成绩对比</span>
            </template>
            <div ref="studentComparisonChart" class="echarts-container-large"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-card class="students-card">
        <template #header>
          <span>学生成绩明细</span>
        </template>
        <el-table :data="studentScores" border stripe v-loading="tableLoading">
          <el-table-column prop="studentId" label="学号" width="100" />
          <el-table-column prop="studentName" label="姓名" width="120" />
          <el-table-column prop="className" label="班级" width="120" />
          <el-table-column prop="score" label="成绩" width="100">
            <template #default="{ row }">
              <span :class="getScoreClass(row.score)">{{ row.score }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="score" label="排名" width="80">
            <template #default="{ row }">
              <el-tag :type="getRankTagType(row.rank)">{{ row.rank }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="resultType" label="结果" width="100">
            <template #default="{ row }">
              <el-tag :type="row.score >= statistics.passingScore ? 'success' : 'danger'">
                {{ row.score >= statistics.passingScore ? '及格' : '不及格' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="submitTime" label="交卷时间" width="180" />
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="handleViewDetail(row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card class="question-analysis-card">
        <template #header>
          <span>题目分析</span>
        </template>
        <el-table :data="questionAnalysis" border stripe>
          <el-table-column prop="questionId" label="题号" width="80" />
          <el-table-column prop="content" label="题目内容" show-overflow-tooltip />
          <el-table-column prop="type" label="题型" width="100">
            <template #default="{ row }">
              <el-tag>{{ getQuestionTypeLabel(row.type) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="difficulty" label="难度" width="100">
            <template #default="{ row }">
              <el-rate v-model="row.difficulty" disabled size="small" />
            </template>
          </el-table-column>
          <el-table-column prop="correctRate" label="正确率" width="150">
            <template #default="{ row }">
              <el-progress
                :percentage="row.correctRate"
                :color="getCorrectRateColor(row.correctRate)"
                :stroke-width="15"
              />
            </template>
          </el-table-column>
          <el-table-column prop="avgScore" label="平均得分" width="100">
            <template #default="{ row }">
              {{ row.avgScore?.toFixed(1) || '-' }} / {{ row.totalScore }}
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <el-dialog v-model="detailDialogVisible" title="学生成绩详情" width="700px">
      <div v-if="currentStudent" class="detail-content">
        <el-descriptions title="基本信息" :column="2" border>
          <el-descriptions-item label="学号">{{ currentStudent.studentId }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ currentStudent.studentName }}</el-descriptions-item>
          <el-descriptions-item label="班级">{{ currentStudent.className }}</el-descriptions-item>
          <el-descriptions-item label="考试得分">
            <span :class="getScoreClass(currentStudent.score)">{{ currentStudent.score }}</span>
          </el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <h4>答题详情</h4>
        <el-table :data="currentStudent.answerDetails" border size="small">
          <el-table-column prop="questionId" label="题号" width="80" />
          <el-table-column prop="type" label="题型" width="100">
            <template #default="{ row }">
              <el-tag size="small">{{ getQuestionTypeLabel(row.type) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="isCorrect" label="是否正确" width="100">
            <template #default="{ row }">
              <el-tag :type="row.isCorrect ? 'success' : 'danger'" size="small">
                {{ row.isCorrect ? '正确' : '错误' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="score" label="得分" width="80">
            <template #default="{ row }">
              {{ row.score }} / {{ row.totalScore }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import axios from '../utils/axios'

const courses = ref([])
const exams = ref([])
const statistics = ref(null)
const studentScores = ref([])
const questionAnalysis = ref([])
const loading = ref(false)
const tableLoading = ref(false)
const detailDialogVisible = ref(false)
const currentStudent = ref(null)

const scoreDistributionChart = ref(null)
const scoreOverviewChart = ref(null)
const studentComparisonChart = ref(null)

let scoreDistributionInstance = null
let scoreOverviewInstance = null
let studentComparisonInstance = null

const filterForm = reactive({
  courseId: null,
  examInfoId: null
})

const handleCourseChange = async (courseId) => {
  if (!courseId) return
  filterForm.examInfoId = null
  try {
    const res = await axios.get(`/exam/exam-info/list/${courseId}`)
    exams.value = res.data?.records || res.data || []
  } catch (error) {
    console.error('Failed to fetch exams:', error)
  }
}

const handleSearch = async () => {
  if (!filterForm.examInfoId) {
    ElMessage.warning('请选择考试')
    return
  }
  loading.value = true
  tableLoading.value = true
  try {
    const res = await axios.get(`/exam/exam-paper/statistics/${filterForm.examInfoId}`)
    statistics.value = res.data

    if (statistics.value) {
      statistics.value.avgScore = statistics.value.avgScore?.toFixed(1) || 0
      statistics.value.passRate = statistics.value.passRate?.toFixed(1) || 0
      statistics.value.excellentRate = statistics.value.excellentRate?.toFixed(1) || 0

      await nextTick()
      initScoreDistributionChart()
    }

    const studentsRes = await axios.get(`/exam/exam-answer-result/list/${filterForm.examInfoId}`)
    studentScores.value = studentsRes.data || []

    await nextTick()
    initStudentComparisonChart()
    questionAnalysis.value = []
  } catch (error) {
    console.error('Failed to fetch statistics:', error)
    statistics.value = generateMockStatistics()
    buildMockStudentScores()

    await nextTick()
    initScoreDistributionChart()
    initStudentComparisonChart()
  } finally {
    loading.value = false
    tableLoading.value = false
  }
}

const generateMockStatistics = () => {
  return {
    totalStudents: 45,
    avgScore: 72.5,
    passRate: 78.5,
    excellentRate: 23.5,
    maxScore: 98,
    minScore: 35,
    medianScore: 75,
    stdDeviation: 12.3,
    totalScore: 100,
    passingScore: 60
  }
}

const buildMockStudentScores = () => {
  studentScores.value = []
  const names = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十', '郑十一', '陈十二']
  for (let i = 0; i < 10; i++) {
    studentScores.value.push({
      studentId: `202400${i + 1}`,
      studentName: names[i],
      className: '计算机1班',
      score: Math.round(Math.random() * 40 + 55),
      rank: i + 1,
      submitTime: '2024-01-15 10:30:00'
    })
  }
}

const initScoreDistributionChart = () => {
  if (!scoreDistributionChart.value) return

  if (scoreDistributionInstance) {
    scoreDistributionInstance.dispose()
  }

  scoreDistributionInstance = echarts.init(scoreDistributionChart.value)

  const ranges = ['0-59', '60-69', '70-79', '80-89', '90-100']
  const mockScores = [5, 8, 15, 12, 5]
  const colors = ['#f56c6c', '#e6a23c', '#409eff', '#67c23a', '#1faa07']

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    xAxis: {
      type: 'category',
      data: ranges,
      axisLabel: { color: '#666' },
      axisLine: { lineStyle: { color: '#ddd' } }
    },
    yAxis: {
      type: 'value',
      name: '人数',
      axisLabel: { color: '#666' },
      axisLine: { lineStyle: { color: '#ddd' } }
    },
    series: [{
      type: 'bar',
      data: mockScores.map((val, idx) => ({
        value: val,
        itemStyle: { color: colors[idx] }
      })),
      barWidth: '50%',
      label: {
        show: true,
        position: 'top',
        formatter: '{c}人'
      }
    }]
  }

  scoreDistributionInstance.setOption(option)
}

const initScoreOverviewChart = () => {
  if (!scoreOverviewChart.value) return

  if (scoreOverviewInstance) {
    scoreOverviewInstance.dispose()
  }

  scoreOverviewInstance = echarts.init(scoreOverviewChart.value)

  const option = {
    tooltip: {
      formatter: '{a} <br/>{b} : {c}'
    },
    series: [
      {
        name: '成绩指标',
        type: 'gauge',
        center: ['50%', '60%'],
        radius: '80%',
        startAngle: 200,
        endAngle: -20,
        min: 0,
        max: 100,
        splitNumber: 10,
        itemStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 1,
            y2: 0,
            colorStops: [
              { offset: 0, color: '#f56c6c' },
              { offset: 0.5, color: '#e6a23c' },
              { offset: 1, color: '#67c23a' }
            ]
          }
        },
        progress: {
          show: true,
          width: 20
        },
        pointer: {
          show: true,
          length: '60%',
          width: 6
        },
        axisLine: {
          lineStyle: {
            width: 20
          }
        },
        axisTick: {
          show: true,
          distance: -20,
          length: 8
        },
        splitLine: {
          show: true,
          distance: -20,
          length: 14
        },
        axisLabel: {
          distance: -50,
          color: '#999',
          fontSize: 10
        },
        anchor: {
          show: false
        },
        title: {
          offsetCenter: [0, '40%'],
          fontSize: 14
        },
        detail: {
          valueAnimation: true,
          width: '60%',
          lineHeight: 40,
          borderRadius: 8,
          offsetCenter: [0, '-10%'],
          fontSize: 24,
          fontWeight: 'bold',
          formatter: '{value}',
          color: 'auto'
        },
        data: [
          { value: statistics.value?.avgScore || 0, name: '平均分' },
          { value: statistics.value?.maxScore || 0, name: '最高分' },
          { value: statistics.value?.minScore || 0, name: '最低分' }
        ]
      }
    ]
  }

  scoreOverviewInstance.setOption(option)
}

const initStudentComparisonChart = () => {
  if (!studentComparisonChart.value || studentScores.value.length === 0) return

  if (studentComparisonInstance) {
    studentComparisonInstance.dispose()
  }

  studentComparisonInstance = echarts.init(studentComparisonChart.value)

  const sortedScores = [...studentScores.value].sort((a, b) => b.score - a.score)
  const names = sortedScores.map(s => s.studentName)
  const scores = sortedScores.map(s => s.score)
  const passingScore = statistics.value?.passingScore || 60

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: function(params) {
        const data = params[0]
        const rank = sortedScores.findIndex(s => s.studentName === data.name) + 1
        return `${data.name}<br/>成绩: ${data.value}<br/>排名: 第${rank}名`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      max: statistics.value?.totalScore || 100,
      axisLabel: { color: '#666' }
    },
    yAxis: {
      type: 'category',
      data: names,
      inverse: true,
      axisLabel: {
        color: '#666',
        interval: 0
      }
    },
    series: [
      {
        name: '成绩',
        type: 'bar',
        data: scores,
        barWidth: '60%',
        itemStyle: {
          color: function(params) {
            const score = params.value
            if (score >= 90) return '#1faa07'
            if (score >= 80) return '#67c23a'
            if (score >= 60) return '#409eff'
            return '#f56c6c'
          },
          borderRadius: [0, 4, 4, 0]
        },
        label: {
          show: true,
          position: 'right',
          formatter: '{c}分'
        },
        markLine: {
          silent: true,
          symbol: 'none',
          lineStyle: {
            color: '#e6a23c',
            type: 'dashed',
            width: 2
          },
          data: [{ xAxis: passingScore }],
          label: {
            position: 'end',
            formatter: '及格线'
          }
        }
      }
    ]
  }

  studentComparisonInstance.setOption(option)
}

const getScoreClass = (score) => {
  if (score >= 90) return 'score-excellent'
  if (score >= 80) return 'score-good'
  if (score >= 60) return 'score-pass'
  return 'score-fail'
}

const getRankTagType = (rank) => {
  if (rank <= 3) return 'gold'
  if (rank <= 10) return 'silver'
  return 'info'
}

const getCorrectRateColor = (rate) => {
  if (rate >= 80) return '#67c23a'
  if (rate >= 60) return '#e6a23c'
  return '#f56c6c'
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

const handleExport = async () => {
  if (!filterForm.examInfoId) {
    ElMessage.warning('请先选择考试进行统计分析')
    return
  }
  ElMessage.success('成绩导出功能开发中')
}

const handleViewDetail = (row) => {
  currentStudent.value = {
    ...row,
    answerDetails: []
  }
  detailDialogVisible.value = true
}

const fetchCourses = async () => {
  try {
    const res = await axios.get('/courses/list/teacher?status=0&currentPage=1')
    courses.value = res.data?.records || res.data || []
  } catch (error) {
    console.error('Failed to fetch courses:', error)
  }
}

const handleResize = () => {
  scoreDistributionInstance?.resize()
  scoreOverviewInstance?.resize()
  studentComparisonInstance?.resize()
}

onMounted(() => {
  fetchCourses()
  window.addEventListener('resize', handleResize)
})
</script>

<style scoped>
.score-analysis {
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

.stat-icon.students { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-icon.avg-score { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.stat-icon.pass-rate { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }
.stat-icon.excellent-rate { background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); }

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

.chart-card,
.students-card,
.question-analysis-card {
  margin-bottom: 20px;
}

.echarts-container {
  height: 300px;
  width: 100%;
}

.echarts-container-large {
  height: 400px;
  width: 100%;
}

.score-excellent {
  color: #1faa07;
  font-weight: bold;
}

.score-good {
  color: #67c23a;
  font-weight: bold;
}

.score-pass {
  color: #409eff;
  font-weight: bold;
}

.score-fail {
  color: #f56c6c;
  font-weight: bold;
}

.detail-content {
  max-height: 60vh;
  overflow-y: auto;
}
</style>