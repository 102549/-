<template>
  <div class="subjective-grading">
    <div class="toolbar">
      <h2>主观题智能批改</h2>
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
          <el-button type="primary" @click="handleSearch" :loading="searchLoading">查询待批改题目</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="gradingList.length > 0" class="grading-card">
      <template #header>
        <div class="card-header">
          <span>待批改主观题列表 (共 {{ gradingList.length }} 道)</span>
          <el-button type="success" @click="handleBatchGrade" :loading="batchLoading">批量智能批改</el-button>
        </div>
      </template>

      <el-table :data="gradingList" border stripe v-loading="tableLoading">
        <el-table-column prop="questionId" label="题目ID" width="100" />
        <el-table-column prop="content" label="题目内容" show-overflow-tooltip />
        <el-table-column prop="type" label="题型" width="100">
          <template #default="{ row }">
            <el-tag>主观题</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="maxScore" label="满分" width="80" />
        <el-table-column prop="studentAnswer" label="学生答案" show-overflow-tooltip />
        <el-table-column prop="currentScore" label="当前得分" width="100">
          <template #default="{ row }">
            <span v-if="row.currentScore !== null">{{ row.currentScore }}</span>
            <el-tag type="warning" v-else>待批改</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleGrade(row)">智能批改</el-button>
            <el-button size="small" @click="handleViewDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-empty v-else-if="hasSearched" description="暂无待批改的主观题" />

    <el-dialog v-model="gradingDialogVisible" title="主观题智能批改" width="800px" :close-on-click-modal="false">
      <div v-if="currentQuestion" class="grading-content">
        <el-card class="question-card">
          <template #header>题目信息</template>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="题目ID">{{ currentQuestion.questionId }}</el-descriptions-item>
            <el-descriptions-item label="题型">主观题</el-descriptions-item>
            <el-descriptions-item label="满分">{{ currentQuestion.maxScore }}</el-descriptions-item>
          </el-descriptions>
          <div class="question-content">
            <h4>题目内容：</h4>
            <p>{{ currentQuestion.content }}</p>
          </div>
          <div class="standard-answer" v-if="currentQuestion.standardAnswer">
            <h4>参考答案：</h4>
            <p>{{ currentQuestion.standardAnswer }}</p>
          </div>
        </el-card>

        <el-card class="answer-card">
          <template #header>学生答案</template>
          <div class="student-answer">
            <p>{{ currentQuestion.studentAnswer || '学生未作答' }}</p>
          </div>
        </el-card>

        <el-card v-if="gradingResult" class="result-card" :class="getResultClass">
          <template #header>批改结果</template>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="得分">
              <span class="score">{{ gradingResult.score }} / {{ gradingResult.maxScore }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="结果">
              <el-tag :type="getResultTagType">{{ getResultLabel(gradingResult.resultType) }}</el-tag>
            </el-descriptions-item>
          </el-descriptions>

          <el-divider>详细反馈</el-divider>
          <div class="feedback-content">
            <pre>{{ gradingResult.feedback }}</pre>
          </div>

          <el-divider>个性化评语</el-divider>
          <div class="comment-content">
            <pre>{{ gradingResult.personalizedComment }}</pre>
          </div>
        </el-card>

        <el-card v-if="!gradingResult && gradingLoading" class="loading-card">
          <el-icon class="loading-icon"><Loading /></el-icon>
          <p>正在使用HanLP进行语义分析...</p>
        </el-card>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="gradingDialogVisible = false">关闭</el-button>
          <el-button v-if="!gradingResult" type="primary" @click="handleConfirmGrade" :loading="confirmLoading">
            确认批改
          </el-button>
          <el-button v-else type="success" @click="handleNextQuestion">
            下一题 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="批改详情" width="700px">
      <div v-if="currentQuestion" class="detail-content">
        <el-descriptions title="基本信息" :column="2" border>
          <el-descriptions-item label="学生姓名">{{ currentQuestion.studentName || '未知' }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ currentQuestion.submitTime || '未知' }}</el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <h4>题目内容</h4>
        <div class="detail-section">
          <p>{{ currentQuestion.content }}</p>
        </div>

        <h4>学生答案</h4>
        <div class="detail-section student-answer-box">
          <p>{{ currentQuestion.studentAnswer || '学生未作答' }}</p>
        </div>

        <h4>参考答案</h4>
        <div class="detail-section standard-answer-box">
          <p>{{ currentQuestion.standardAnswer || '暂无' }}</p>
        </div>

        <h4>教师评语</h4>
        <div class="detail-section comment-box">
          <p>{{ currentQuestion.teacherComment || '暂无' }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../utils/axios'

const courses = ref([])
const exams = ref([])
const gradingList = ref([])
const hasSearched = ref(false)
const searchLoading = ref(false)
const tableLoading = ref(false)
const batchLoading = ref(false)
const gradingDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const gradingLoading = ref(false)
const confirmLoading = ref(false)
const currentQuestion = ref(null)
const gradingResult = ref(null)

const filterForm = reactive({
  courseId: null,
  examInfoId: null
})

const getResultClass = computed(() => {
  if (!gradingResult.value) return ''
  const ratio = gradingResult.value.score / gradingResult.value.maxScore
  if (ratio >= 0.7) return 'result-excellent'
  if (ratio >= 0.5) return 'result-good'
  return 'result-poor'
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
  if (!filterForm.courseId) {
    ElMessage.warning('请选择课程')
    return
  }
  hasSearched.value = true
  tableLoading.value = true
  try {
    const res = await axios.get(`/exam/exam-info/list/${filterForm.courseId}`)
    const examList = res.data?.records || res.data || []
    gradingList.value = []
    for (const exam of examList) {
      try {
        const detailRes = await axios.get(`/exam/exam-info/detail/${exam.id}`)
        if (detailRes.data?.examInfo?.hasSubjective) {
          gradingList.value.push({
            ...detailRes.data.examInfo,
            examId: exam.id
          })
        }
      } catch (e) {
        console.error(`Failed to fetch exam detail ${exam.id}:`, e)
      }
    }
  } catch (error) {
    console.error('Failed to search:', error)
  } finally {
    tableLoading.value = false
  }
}

const handleGrade = async (row) => {
  currentQuestion.value = row
  gradingResult.value = null
  gradingDialogVisible.value = true
  gradingLoading.value = true

  try {
    const res = await axios.post(`/exam/grading/question/${filterForm.examInfoId}/${row.questionId}`, {
      answer: row.studentAnswer
    })
    gradingResult.value = res.data
    if (res.data.personalizedComment) {
      gradingResult.value.personalizedComment = res.data.personalizedComment
    } else {
      gradingResult.value.personalizedComment = generateDefaultComment(res.data)
    }
  } catch (error) {
    console.error('Failed to grade:', error)
    ElMessage.error('批改失败')
  } finally {
    gradingLoading.value = false
  }
}

const generateDefaultComment = (result) => {
  const percentage = (result.score / result.maxScore) * 100
  if (percentage >= 90) {
    return '答案准确完整，要点齐全，表达清晰流畅。继续保持！'
  } else if (percentage >= 70) {
    return '答案基本正确，但对某些关键知识点的理解还不够深入。建议加强对薄弱知识点的学习。'
  } else if (percentage >= 50) {
    return '答案存在明显偏差，需要重新学习相关知识点。建议查看参考答案并理解解题思路。'
  } else {
    return '答案偏离主题或存在重大错误，需要认真复习相关内容。建议从基础开始系统学习。'
  }
}

const handleConfirmGrade = async () => {
  if (!gradingResult.value) return
  confirmLoading.value = true
  try {
    const index = gradingList.value.findIndex(q => q.questionId === currentQuestion.value.questionId)
    if (index !== -1) {
      gradingList.value[index].currentScore = gradingResult.value.score
    }
    gradingDialogVisible.value = false
    ElMessage.success('批改已确认')
  } catch (error) {
    console.error('Failed to confirm:', error)
  } finally {
    confirmLoading.value = false
  }
}

const handleNextQuestion = () => {
  const currentIndex = gradingList.value.findIndex(q => q.questionId === currentQuestion.value.questionId)
  if (currentIndex < gradingList.value.length - 1) {
    handleGrade(gradingList.value[currentIndex + 1])
  } else {
    ElMessage.success('已是最后一道题')
    gradingDialogVisible.value = false
  }
}

const handleBatchGrade = async () => {
  if (gradingList.value.length === 0) {
    ElMessage.warning('没有可批改的题目')
    return
  }
  batchLoading.value = true
  try {
    const res = await axios.post(`/exam/grading/batch/${filterForm.examInfoId}`)
    ElMessage.success(`批量批改完成，共处理 ${res.data?.totalCount || 0} 道题目`)
    handleSearch()
  } catch (error) {
    console.error('Failed to batch grade:', error)
  } finally {
    batchLoading.value = false
  }
}

const handleViewDetail = (row) => {
  currentQuestion.value = row
  detailDialogVisible.value = true
}

const getResultTagType = (resultType) => {
  if (resultType === 'CORRECT' || resultType === 1) return 'success'
  if (resultType === 'PARTIAL' || resultType === 2) return 'warning'
  return 'danger'
}

const getResultLabel = (resultType) => {
  if (resultType === 'CORRECT' || resultType === 1) return '正确'
  if (resultType === 'PARTIAL' || resultType === 2) return '部分正确'
  return '错误'
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
.subjective-grading {
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

.grading-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.grading-content {
  max-height: 70vh;
  overflow-y: auto;
}

.question-card,
.answer-card,
.result-card {
  margin-bottom: 15px;
}

.question-content,
.standard-answer {
  margin-top: 15px;
}

.question-content h4,
.standard-answer h4,
.feedback-content h4 {
  margin: 10px 0;
  color: #606266;
}

.standard-answer {
  background: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
}

.student-answer {
  padding: 15px;
  background: #ecf5ff;
  border-radius: 4px;
  min-height: 60px;
}

.result-card.result-excellent {
  border-color: #67c23a;
}

.result-card.result-good {
  border-color: #e6a23c;
}

.result-card.result-poor {
  border-color: #f56c6c;
}

.score {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
}

.feedback-content pre,
.comment-content pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  line-height: 1.6;
}

.loading-card {
  text-align: center;
  padding: 30px;
}

.loading-icon {
  font-size: 40px;
  color: #409eff;
  animation: rotating 2s linear infinite;
}

@keyframes rotating {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}

.detail-content {
  max-height: 60vh;
  overflow-y: auto;
}

.detail-section {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 15px;
}

.student-answer-box {
  background: #ecf5ff;
}

.standard-answer-box {
  background: #f0f9eb;
}

.comment-box {
  background: #fef0f0;
}
</style>
