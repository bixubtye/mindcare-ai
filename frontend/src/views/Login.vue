<template>
  <div class="auth-form-page">
    <router-link class="back-link" to="/"><el-icon><ArrowLeft /></el-icon><span>返回首页</span></router-link>
    <div class="form-card">
      <header><p class="form-kicker">CampusRhythm</p><h1>欢迎回来</h1><p>登录后继续记录你的状态与节奏</p></header>
      <el-form ref="formRef" :model="form" :rules="rules" class="auth-form" @submit.prevent>
        <el-form-item prop="username" label="用户名或邮箱">
          <el-input v-model="form.username" size="large" placeholder="请输入用户名或邮箱" :prefix-icon="User" clearable />
        </el-form-item>
        <el-form-item prop="password" label="密码">
          <el-input v-model="form.password" size="large" placeholder="请输入密码" :prefix-icon="Lock" show-password type="password" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-button type="primary" size="large" class="submit-button" @click="handleLogin">登录</el-button>
      </el-form>
      <p class="switch-tip">还没有账户？<router-link to="/auth/register">创建账户</router-link></p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ArrowLeft, Lock, User } from '@element-plus/icons-vue'
import { login } from '@/api/admin'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const formRef = ref()
const rules = ref({
  username: [{ required: true, message: '请输入用户名或邮箱', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
})
const form = reactive({ username: '', password: '' })
const router = useRouter()

const handleLogin = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    const res = await login(form)
    if (!res.data?.token) {
      ElMessage.error('登录失败')
      return
    }
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo))
    if (res.data.userInfo.userType === 2) router.push({ path: '/back/dashboard' })
    else router.push({ path: '/' })
    ElMessage.success('登录成功')
  } catch (e) {}
}
</script>

<style scoped>
.auth-form-page { width: 100%; max-width: 460px; }
.back-link { display: inline-flex; align-items: center; gap: 7px; margin-bottom: 34px; padding: 7px 9px; border-radius: 8px; color: var(--text-secondary); font-size: 14px; text-decoration: none; }
.back-link:hover { background: var(--brand-primary-soft); color: var(--brand-primary-dark); }
.form-card { padding: 34px; border: 1px solid var(--border-color); border-radius: 18px; background: #fff; box-shadow: var(--shadow-sm); }
.form-card header { margin-bottom: 30px; }
.form-kicker { margin: 0 0 8px; color: var(--brand-primary); font-size: 12px; font-weight: 800; letter-spacing: .12em; text-transform: uppercase; }
.form-card h1 { margin: 0; color: var(--text-primary); font-size: 32px; line-height: 1.25; }
.form-card header > p:last-child { margin: 9px 0 0; color: var(--text-secondary); font-size: 14px; }
.auth-form :deep(.el-form-item) { margin-bottom: 20px; }
.auth-form :deep(.el-form-item__label) { padding-bottom: 7px; color: #42514e; font-weight: 600; }
.auth-form :deep(.el-input__wrapper) { min-height: 48px; padding: 0 14px; }
.submit-button { width: 100%; height: 48px; margin-top: 8px; font-size: 16px; }
.switch-tip { margin: 24px 0 0; color: var(--text-muted); font-size: 14px; text-align: center; }
.switch-tip a { margin-left: 5px; color: var(--brand-primary-dark); font-weight: 700; text-decoration: none; }
@media (max-width: 520px) { .back-link { margin-bottom: 20px; } .form-card { padding: 26px 20px; } }
</style>
