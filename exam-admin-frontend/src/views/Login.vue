<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="title">在线考试系统</h2>
      <el-form :model="loginForm" :rules="rules" ref="formRef">
        <el-form-item label="选择身份" prop="role">
          <el-select v-model="loginForm.role" placeholder="请选择登录身份" style="width: 100%">
            <el-option label="管理员" :value="UserRole.ADMIN" />
            <el-option label="教师" :value="UserRole.TEACHER" />
            <el-option label="学生" :value="UserRole.STUDENT" />
          </el-select>
        </el-form-item>
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="Lock" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" :loading="loading" @click="handleLogin">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from '../utils/axios'
import { UserRole, setUserRole, clearUserRole } from '../utils/roleConstants'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  role: UserRole.TEACHER,
  username: '',
  password: ''
})

const rules = {
  role: [{ required: true, message: '请选择登录身份', trigger: 'change' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const getClientId = (role) => {
  return role === UserRole.STUDENT ? 'exam-app' : 'admin-app'
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    clearUserRole()

    const params = new URLSearchParams()
    params.append('username', loginForm.username)
    params.append('password', loginForm.password)
    params.append('grant_type', 'password')
    params.append('client_id', getClientId(loginForm.role))
    params.append('client_secret', '123456')
    params.append('scope', 'all')

    const res = await axios.post('/auth/oauth/token', params, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })

    if (res.data && res.data.access_token) {
      localStorage.setItem('token', res.data.access_token)
      localStorage.setItem('username', loginForm.username)
      setUserRole(loginForm.role)
      ElMessage.success('登录成功')
      router.push('/dashboard')
    } else {
      ElMessage.error('登录失败，返回数据格式错误')
    }
  } catch (error) {
    console.error('Login error:', error)
    if (error.response) {
      if (error.response.status === 401) {
        ElMessage.error('用户名或密码错误')
      } else if (error.response.status === 403) {
        ElMessage.error('没有权限，请检查身份选择是否正确')
      } else {
        ElMessage.error(`登录失败: ${error.response.data?.error_description || error.message}`)
      }
    } else {
      ElMessage.error('网络错误，请检查后端服务是否启动')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
  padding: 20px;
}

.title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}
</style>