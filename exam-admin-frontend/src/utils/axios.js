import axios from 'axios'
import { ElMessage } from 'element-plus'

const instance = axios.create({
  timeout: 30000
})

let router = null

export const setRouter = (r) => {
  router = r
}

instance.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

instance.interceptors.response.use(
  response => {
    const res = response.data

    if (response.config.url.includes('/oauth/token')) {
      return response
    }

    if (res.code && res.code !== 200 && res.code !== 0) {
      ElMessage.error(res.message || '请求失败')
      if (res.code === 401) {
        localStorage.removeItem('token')
        if (router) {
          router.push('/login')
        } else {
          window.location.href = '/login'
        }
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }

    return response
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          localStorage.removeItem('token')
          if (router) {
            router.push('/login')
          } else {
            window.location.href = '/login'
          }
          break
        case 403:
          ElMessage.error('没有权限访问该资源')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(error.message || '网络错误')
      }
    } else {
      ElMessage.error('网络连接失败，请检查网络')
    }
    return Promise.reject(error)
  }
)

export default instance

export const userAPI = {
  login: (data) => instance.post('/auth/oauth/token', data, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    transformRequest: [(data) => {
      const params = new URLSearchParams()
      for (const key in data) {
        params.append(key, data[key])
      }
      return params
    }]
  }),

  getUserList: () => instance.get('/user/list'),

  updateUser: (data) => instance.post('/user/update', data),

  deleteUser: (id) => instance.post(`/user/delete/${id}`),

  getCourses: (params) => instance.get('/user/courses/list/teacher', { params }),

  getCourseInfo: (courseId) => instance.get(`/user/courses/getInfo?courseId=${courseId}`),

  getClasses: () => instance.get('/classes/list'),

  updateClass: (data) => instance.post('/classes/update', data),

  deleteClass: (id) => instance.post(`/classes/delete/${id}`)
}

export const examAPI = {
  getExamList: (courseId, params) => instance.get(`/exam/exam-info/list/${courseId}`, { params }),

  getExamDetail: (examId) => instance.get(`/exam/exam-info/detail/${examId}`),

  updateExam: (data) => instance.post('/exam/exam-info/update', data),

  deleteExam: (examId) => instance.post(`/exam/exam-info/delete/${examId}`),

  getQuestionList: (courseId, params) => instance.get(`/exam/question/list/${courseId}`, { params }),

  addQuestion: (data) => instance.post('/exam/question/add', data),

  updateQuestion: (data) => instance.post('/exam/question/update', data),

  deleteQuestion: (questionId) => instance.post(`/exam/question/delete/${questionId}`),

  getTagsByCourse: (courseId) => instance.get(`/exam/tags/${courseId}/list`),

  getAdaptivePaperProfile: (courseId) => instance.get(`/exam/adaptive-paper/profile/${courseId}`),

  generateAdaptivePaper: (data) => instance.post('/exam/adaptive-paper/generate', data),

  gradeQuestion: (examInfoId, questionId, data) => instance.post(`/exam/grading/question/${examInfoId}/${questionId}`, data),

  batchGrade: (examInfoId) => instance.post(`/exam/grading/batch/${examInfoId}`),

  getAntiCheatAlerts: (examId) => instance.get(`/exam/anti-cheat/alerts/${examId}`),

  getExamScoreStatistics: (examId) => instance.get(`/exam/exam-paper/statistics/${examId}`),

  getExamScoreList: (examId) => instance.get(`/exam/exam-answer-result/list/${examId}`),

  getExamConsole: (examId) => instance.get(`/exam/exam-console/${examId}/outline-monitor`)
}
