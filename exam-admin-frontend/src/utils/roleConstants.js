export const UserRole = {
  ADMIN: 'ADMIN',
  TEACHER: 'TEACHER',
  STUDENT: 'STUDENT'
}

export const RoleLabel = {
  [UserRole.ADMIN]: '管理员',
  [UserRole.TEACHER]: '教师',
  [UserRole.STUDENT]: '学生'
}

export const RoleClientMap = {
  [UserRole.ADMIN]: 'admin-app',
  [UserRole.TEACHER]: 'admin-app',
  [UserRole.STUDENT]: 'exam-app'
}

export const routePermissions = {
  '/dashboard': [UserRole.ADMIN, UserRole.TEACHER, UserRole.STUDENT],
  '/courses': [UserRole.ADMIN, UserRole.TEACHER],
  '/users': [UserRole.ADMIN],
  '/exams': [UserRole.ADMIN, UserRole.TEACHER, UserRole.STUDENT],
  '/questions': [UserRole.ADMIN, UserRole.TEACHER],
  '/classes': [UserRole.ADMIN, UserRole.TEACHER],
  '/adaptive-paper': [UserRole.TEACHER],
  '/subjective-grading': [UserRole.TEACHER],
  '/score-analysis': [UserRole.ADMIN, UserRole.TEACHER, UserRole.STUDENT],
  '/exam-monitor': [UserRole.ADMIN, UserRole.TEACHER],
  '/anti-cheat-monitor': [UserRole.ADMIN, UserRole.TEACHER],
  '/student-profile': [UserRole.TEACHER]
}

export function hasPermission(routePath, userRole) {
  const allowedRoles = routePermissions[routePath]
  if (!allowedRoles) return false
  return allowedRoles.includes(userRole)
}

export function getUserRoleFromLoginUser(loginUser) {
  if (!loginUser || !loginUser.role) return null

  if (loginUser.role === '1') {
    return UserRole.TEACHER
  } else if (loginUser.role === '0') {
    return UserRole.STUDENT
  }

  return null
}

export function initUserRole() {
  const role = localStorage.getItem('userRole')
  if (role) {
    return role
  }

  const username = localStorage.getItem('username')
  if (username === 'admin') {
    localStorage.setItem('userRole', UserRole.ADMIN)
    return UserRole.ADMIN
  } else if (username === 'teacher') {
    localStorage.setItem('userRole', UserRole.TEACHER)
    return UserRole.TEACHER
  } else {
    localStorage.setItem('userRole', UserRole.STUDENT)
    return UserRole.STUDENT
  }
}

export function setUserRole(role) {
  localStorage.setItem('userRole', role)
}

export function clearUserRole() {
  localStorage.removeItem('userRole')
}