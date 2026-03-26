import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UserRole, hasPermission, initUserRole } from '../utils/roleConstants'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { roles: [UserRole.ADMIN, UserRole.TEACHER, UserRole.STUDENT] }
      },
      {
        path: 'courses',
        name: 'Courses',
        component: () => import('../views/Courses.vue'),
        meta: { roles: [UserRole.ADMIN, UserRole.TEACHER] }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('../views/Users.vue'),
        meta: { roles: [UserRole.ADMIN] }
      },
      {
        path: 'exams',
        name: 'Exams',
        component: () => import('../views/Exams.vue'),
        meta: { roles: [UserRole.ADMIN, UserRole.TEACHER] }
      },
      {
        path: 'student-exams',
        name: 'StudentExams',
        component: () => import('../views/StudentExams.vue'),
        meta: { roles: [UserRole.STUDENT] }
      },
      {
        path: 'questions',
        name: 'Questions',
        component: () => import('../views/Questions.vue'),
        meta: { roles: [UserRole.ADMIN, UserRole.TEACHER] }
      },
      {
        path: 'classes',
        name: 'Classes',
        component: () => import('../views/Classes.vue'),
        meta: { roles: [UserRole.ADMIN, UserRole.TEACHER] }
      },
      {
        path: 'adaptive-paper',
        name: 'AdaptivePaper',
        component: () => import('../views/AdaptivePaper.vue'),
        meta: { roles: [UserRole.TEACHER] }
      },
      {
        path: 'subjective-grading',
        name: 'SubjectiveGrading',
        component: () => import('../views/SubjectiveGrading.vue'),
        meta: { roles: [UserRole.TEACHER] }
      },
      {
        path: 'anti-cheat-monitor',
        name: 'AntiCheatMonitor',
        component: () => import('../views/AntiCheatMonitor.vue'),
        meta: { roles: [UserRole.ADMIN, UserRole.TEACHER] }
      },
      {
        path: 'student-profile',
        name: 'StudentProfile',
        component: () => import('../views/StudentProfile.vue'),
        meta: { roles: [UserRole.TEACHER] }
      },
      {
        path: 'score-analysis',
        name: 'ScoreAnalysis',
        component: () => import('../views/ScoreAnalysis.vue'),
        meta: { roles: [UserRole.ADMIN, UserRole.TEACHER, UserRole.STUDENT] }
      },
      {
        path: 'exam-monitor/:examInfoId?',
        name: 'ExamMonitor',
        component: () => import('../views/ExamMonitor.vue'),
        meta: { roles: [UserRole.ADMIN, UserRole.TEACHER] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  if (to.path === '/login') {
    next()
    return
  }

  if (!token) {
    next('/login')
    return
  }

  const userRole = initUserRole()

  if (to.meta && to.meta.roles) {
    if (to.meta.roles.includes(userRole)) {
      next()
    } else {
      ElMessage.error('您没有权限访问该页面')
      next('/dashboard')
    }
  } else {
    next()
  }
})

export default router