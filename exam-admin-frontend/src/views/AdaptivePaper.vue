<template>
  <div class="adaptive-paper">
    <div class="toolbar">
      <h2>自适应组卷</h2>
    </div>

    <el-card class="config-card">
      <el-form :model="paperConfig" label-width="140px">
        <el-form-item label="课程选择">
          <el-select v-model="paperConfig.courseId" placeholder="请选择课程" @change="handleCourseChange">
            <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="试卷标题">
          <el-input v-model="paperConfig.paperTitle" placeholder="请输入试卷标题" />
        </el-form-item>

        <el-form-item label="启用自适应难度">
          <el-switch v-model="paperConfig.enableAdaptiveDifficulty" active-value="1" inactive-value="0" />
          <span class="hint">开启后系统将根据学生能力画像自动调整题目难度</span>
        </el-form-item>

        <el-form-item label="目标难度" v-if="paperConfig.enableAdaptiveDifficulty === '0'">
          <el-rate v-model="paperConfig.targetDifficulty" :max="5" show-text :texts="['1星', '2星', '3星', '4星', '5星']" />
        </el-form-item>

        <el-divider>题目数量配置</el-divider>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单选题数量">
              <el-input-number v-model="paperConfig.singleChoiceCount" :min="0" :max="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="多选题数量">
              <el-input-number v-model="paperConfig.multipleChoiceCount" :min="0" :max="50" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="判断题数量">
              <el-input-number v-model="paperConfig.judgmentCount" :min="0" :max="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="填空题数量">
              <el-input-number v-model="paperConfig.completionCount" :min="0" :max="50" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="主观题数量">
              <el-input-number v-model="paperConfig.subjectiveCount" :min="0" :max="50" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider>知识点覆盖要求</el-divider>

        <el-form-item label="知识点配置">
          <div v-for="(kp, index) in knowledgePoints" :key="index" class="kp-item">
            <span>{{ kp.tagName }}: </span>
            <el-input-number v-model="kp.count" :min="0" :max="20" size="small" />
          </div>
          <el-button type="primary" link @click="showKnowledgePointDialog">添加知识点要求</el-button>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handlePreviewAbility" :loading="previewLoading">预览学生能力画像</el-button>
          <el-button type="success" @click="handleGeneratePaper" :loading="generateLoading">生成试卷</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="abilityProfile" class="profile-card" header="学生能力画像">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="总答题数">{{ abilityProfile.totalAnswers }}</el-descriptions-item>
        <el-descriptions-item label="正确率">{{ abilityProfile.accuracyRate }}%</el-descriptions-item>
        <el-descriptions-item label="能力等级">
          <el-rate v-model="abilityProfile.abilityLevel" disabled :max="5" show-score />
        </el-descriptions-item>
        <el-descriptions-item label="推荐难度">
          <el-tag :type="getDifficultyType(abilityProfile.recommendedDifficulty)">
            {{ abilityProfile.recommendedDifficulty }}星
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="性能趋势">{{ abilityProfile.performanceTrend }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>知识点掌握情况</el-divider>
      <el-table :data="abilityProfile.masteryList" border stripe>
        <el-table-column prop="tagName" label="知识点" />
        <el-table-column prop="questionCount" label="题目数" width="100" />
        <el-table-column label="掌握度" width="200">
          <template #default="{ row }">
            <el-progress :percentage="row.masteryRate || 0" :color="getProgressColor(row.masteryRate)" />
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card v-if="generatedPaper" class="paper-preview-card" header="生成的试卷预览">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="试卷标题">{{ generatedPaper.title }}</el-descriptions-item>
        <el-descriptions-item label="课程ID">{{ generatedPaper.courseId }}</el-descriptions-item>
        <el-descriptions-item label="总分">{{ generatedPaper.totalScore }}</el-descriptions-item>
        <el-descriptions-item label="题目数量">{{ generatedPaper.questionCount || '待确定' }}</el-descriptions-item>
      </el-descriptions>
      <div class="paper-actions">
        <el-button type="primary" @click="handleConfirmPaper">确认使用此试卷</el-button>
        <el-button @click="handleRegenerate">重新生成</el-button>
      </div>
    </el-card>

    <el-dialog v-model="kpDialogVisible" title="添加知识点要求" width="400px">
      <el-form :model="kpForm" label-width="80px">
        <el-form-item label="知识点">
          <el-select v-model="kpForm.tagId" placeholder="请选择知识点">
            <el-option v-for="tag in availableTags" :key="tag.id" :label="tag.tag" :value="tag.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="题目数量">
          <el-input-number v-model="kpForm.count" :min="1" :max="20" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="kpDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddKp">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../utils/axios'

const courses = ref([])
const abilityProfile = ref(null)
const generatedPaper = ref(null)
const previewLoading = ref(false)
const generateLoading = ref(false)
const kpDialogVisible = ref(false)
const availableTags = ref([])
const knowledgePoints = ref([])

const paperConfig = reactive({
  courseId: null,
  paperTitle: '',
  enableAdaptiveDifficulty: '1',
  targetDifficulty: 3,
  singleChoiceCount: 5,
  multipleChoiceCount: 3,
  judgmentCount: 5,
  completionCount: 3,
  subjectiveCount: 2
})

const kpForm = reactive({
  tagId: null,
  count: 1
})

const handleCourseChange = async (courseId) => {
  if (!courseId) return
  try {
    const res = await axios.get(`/exam/tags/${courseId}/list`)
    availableTags.value = res.data || []
  } catch (error) {
    console.error('Failed to fetch tags:', error)
  }
}

const handlePreviewAbility = async () => {
  if (!paperConfig.courseId) {
    ElMessage.warning('请先选择课程')
    return
  }
  previewLoading.value = true
  try {
    const res = await axios.get(`/exam/adaptive-paper/profile/${paperConfig.courseId}`)
    abilityProfile.value = res.data
    ElMessage.success('能力画像获取成功')
  } catch (error) {
    console.error('Failed to fetch ability profile:', error)
  } finally {
    previewLoading.value = false
  }
}

const handleGeneratePaper = async () => {
  if (!paperConfig.courseId) {
    ElMessage.warning('请先选择课程')
    return
  }
  if (!paperConfig.paperTitle) {
    ElMessage.warning('请输入试卷标题')
    return
  }
  generateLoading.value = true
  try {
    const knowledgePointReq = {}
    knowledgePoints.value.forEach(kp => {
      knowledgePointReq[kp.tagId] = kp.count
    })

    const requestData = {
      ...paperConfig,
      knowledgePointRequirements: knowledgePointReq
    }

    const res = await axios.post('/exam/adaptive-paper/generate', requestData)
    generatedPaper.value = res.data
    ElMessage.success('试卷生成成功')
  } catch (error) {
    console.error('Failed to generate paper:', error)
  } finally {
    generateLoading.value = false
  }
}

const handleConfirmPaper = () => {
  ElMessage.success('试卷已确认，可前往考试管理进行发布')
}

const handleRegenerate = () => {
  generatedPaper.value = null
  handleGeneratePaper()
}

const handleAddKp = () => {
  if (!kpForm.tagId || !kpForm.count) {
    ElMessage.warning('请完善知识点信息')
    return
  }
  const tag = availableTags.value.find(t => t.id === kpForm.tagId)
  if (tag) {
    knowledgePoints.value.push({
      tagId: kpForm.tagId,
      tagName: tag.tag,
      count: kpForm.count
    })
  }
  kpDialogVisible.value = false
}

const getDifficultyType = (difficulty) => {
  if (difficulty >= 4) return 'success'
  if (difficulty >= 3) return 'warning'
  return 'danger'
}

const getProgressColor = (percentage) => {
  if (percentage >= 80) return '#67c23a'
  if (percentage >= 60) return '#e6a23c'
  return '#f56c6c'
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
.adaptive-paper {
  padding: 20px;
}

.toolbar {
  margin-bottom: 20px;
}

.toolbar h2 {
  margin: 0;
}

.config-card {
  margin-bottom: 20px;
}

.hint {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}

.kp-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.profile-card {
  margin-bottom: 20px;
}

.paper-preview-card {
  margin-bottom: 20px;
}

.paper-actions {
  margin-top: 20px;
  text-align: center;
}
</style>
