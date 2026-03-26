<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="aside">
      <div class="logo">考试系统</div>
      <el-menu
        :default-active="$route.path"
        router
        class="menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>

        <el-menu-item v-if="showCourses" index="/courses">
          <el-icon><Reading /></el-icon>
          <span>课程管理</span>
        </el-menu-item>

        <el-menu-item v-if="showUsers" index="/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>

        <el-menu-item v-if="showExams" index="/exams">
          <el-icon><Document /></el-icon>
          <span>考试管理</span>
        </el-menu-item>

        <el-menu-item v-if="showStudentExams" index="/student-exams">
          <el-icon><Document /></el-icon>
          <span>我的考试</span>
        </el-menu-item>

        <el-menu-item v-if="showQuestions" index="/questions">
          <el-icon><QuestionFilled /></el-icon>
          <span>题库管理</span>
        </el-menu-item>

        <el-menu-item v-if="showClasses" index="/classes">
          <el-icon><School /></el-icon>
          <span>班级管理</span>
        </el-menu-item>

        <el-sub-menu v-if="showExamAnalysis" index="exam-advanced">
          <template #title>
            <el-icon><DataAnalysis /></el-icon>
            <span>考试分析</span>
          </template>
          <el-menu-item v-if="showAdaptivePaper" index="/adaptive-paper">
            <el-icon><Memo /></el-icon>
            <span>自适应组卷</span>
          </el-menu-item>
          <el-menu-item v-if="showSubjectiveGrading" index="/subjective-grading">
            <el-icon><Edit /></el-icon>
            <span>主观题批改</span>
          </el-menu-item>
          <el-menu-item v-if="showScoreAnalysis" index="/score-analysis">
            <el-icon><DataLine /></el-icon>
            <span>成绩统计</span>
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu v-if="showMonitor" index="monitor">
          <template #title>
            <el-icon><Monitor /></el-icon>
            <span>考试监控</span>
          </template>
          <el-menu-item v-if="showExamMonitor" index="/exam-monitor">
            <el-icon><VideoCamera /></el-icon>
            <span>监控控制台</span>
          </el-menu-item>
          <el-menu-item v-if="showAntiCheatMonitor" index="/anti-cheat-monitor">
            <el-icon><Warning /></el-icon>
            <span>作弊预警</span>
          </el-menu-item>
        </el-sub-menu>

        <el-menu-item v-if="showStudentProfile" index="/student-profile">
          <el-icon><UserFilled /></el-icon>
          <span>能力画像</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <span class="role-tag" :type="roleTagType">{{ roleLabel }}</span>
        </div>
        <div class="header-right">
          <span class="username">{{ username }}</span>
          <el-button type="danger" size="small" @click="handleLogout">退出</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { UserRole, RoleLabel, initUserRole, clearUserRole } from '../utils/roleConstants'

const router = useRouter()
const username = ref(localStorage.getItem('username') || 'User')

const userRole = computed(() => initUserRole())

const roleTagType = computed(() => {
  switch (userRole.value) {
    case UserRole.ADMIN: return 'danger'
    case UserRole.TEACHER: return 'warning'
    case UserRole.STUDENT: return 'success'
    default: return 'info'
  }
})

const roleLabel = computed(() => RoleLabel[userRole.value] || '未知')

const showCourses = computed(() => [UserRole.ADMIN, UserRole.TEACHER].includes(userRole.value))
const showUsers = computed(() => userRole.value === UserRole.ADMIN)
const showExams = computed(() => [UserRole.ADMIN, UserRole.TEACHER].includes(userRole.value))
const showStudentExams = computed(() => userRole.value === UserRole.STUDENT)
const showQuestions = computed(() => [UserRole.ADMIN, UserRole.TEACHER].includes(userRole.value))
const showClasses = computed(() => [UserRole.ADMIN, UserRole.TEACHER].includes(userRole.value))
const showExamAnalysis = computed(() => [UserRole.ADMIN, UserRole.TEACHER].includes(userRole.value))
const showAdaptivePaper = computed(() => userRole.value === UserRole.TEACHER)
const showSubjectiveGrading = computed(() => userRole.value === UserRole.TEACHER)
const showScoreAnalysis = computed(() => [UserRole.ADMIN, UserRole.TEACHER, UserRole.STUDENT].includes(userRole.value))
const showMonitor = computed(() => [UserRole.ADMIN, UserRole.TEACHER].includes(userRole.value))
const showExamMonitor = computed(() => [UserRole.ADMIN, UserRole.TEACHER].includes(userRole.value))
const showAntiCheatMonitor = computed(() => [UserRole.ADMIN, UserRole.TEACHER].includes(userRole.value))
const showStudentProfile = computed(() => userRole.value === UserRole.TEACHER)

const handleLogout = () => {
  clearUserRole()
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background: #304156;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  background: #263445;
}

.menu {
  border: none;
  background: #304156;
}

.menu .el-menu-item {
  color: #bfcbd9;
}

.menu .el-menu-item:hover,
.menu .el-menu-item.is-active {
  background: #263445;
  color: #409eff;
}

.menu .el-sub-menu__title {
  color: #bfcbd9;
}

.menu .el-sub-menu__title:hover {
  background: #263445;
  color: #409eff;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
}

.role-tag {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  color: #666;
}

.main {
  background: #f0f2f5;
  padding: 20px;
}
</style>