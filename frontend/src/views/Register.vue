<template>
  <div class="auth-form-page">
    <router-link class="back-link" to="/"><el-icon><ArrowLeft /></el-icon><span>返回首页</span></router-link>
    <div class="form-card">
      <header><p class="form-kicker">CampusRhythm</p><h1>开始记录自己的节奏</h1><p>创建账户，开启每日状态记录</p></header>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="auth-form">
        <div class="field-grid">
          <el-form-item prop="username" label="用户名或邮箱"><el-input v-model="form.username" size="large" placeholder="请输入用户名或邮箱" autocomplete="username" /></el-form-item>
          <el-form-item prop="email" label="邮箱"><el-input v-model="form.email" size="large" type="email" placeholder="请输入邮箱" autocomplete="email" /></el-form-item>
          <el-form-item prop="nickname" label="昵称"><el-input v-model="form.nickname" size="large" placeholder="请输入昵称（可选）" /></el-form-item>
          <el-form-item prop="phone" label="手机号"><el-input v-model="form.phone" size="large" type="tel" placeholder="请输入手机号（可选）" autocomplete="tel" /></el-form-item>
          <el-form-item prop="password" label="密码"><el-input v-model="form.password" size="large" type="password" show-password placeholder="请输入密码" autocomplete="new-password" /></el-form-item>
          <el-form-item prop="confirmPassword" label="确认密码"><el-input v-model="form.confirmPassword" size="large" type="password" placeholder="请再次输入密码" autocomplete="new-password" /></el-form-item>
        </div>
        <el-button type="primary" class="submit-button" @click="submitForm">创建账户</el-button>
      </el-form>
      <p class="switch-tip">已有账户？<router-link to="/auth/login">立即登录</router-link></p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ArrowLeft } from '@element-plus/icons-vue'
import { register } from '@/api/frontend'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const form = reactive({ username: '', email: '', nickname: '', phone: '', password: '', confirmPassword: '', gender: 0, userType: 1 })
const rules = reactive({
  username: [{ required: true, message: '请输入用户名或邮箱', trigger: 'blur' }],
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请再次输入密码', trigger: 'blur' }]
})

const submitForm = async () => {
  if (!formRef.value) return
  formRef.value.validate(async () => {
    register(form).then(() => {
      ElMessage.success('注册成功')
      router.push({ path: '/auth/login' })
    })
  })
}
</script>

<style scoped>
.auth-form-page { width: 100%; max-width: 620px; }
.back-link { display: inline-flex; align-items: center; gap: 7px; margin-bottom: 22px; padding: 7px 9px; border-radius: 8px; color: var(--text-secondary); font-size: 14px; text-decoration: none; }
.back-link:hover { background: var(--brand-primary-soft); color: var(--brand-primary-dark); }
.form-card { padding: 30px 34px; border: 1px solid var(--border-color); border-radius: 18px; background: #fff; box-shadow: var(--shadow-sm); }
.form-card header { margin-bottom: 22px; }
.form-kicker { margin: 0 0 8px; color: var(--brand-primary); font-size: 12px; font-weight: 800; letter-spacing: .12em; text-transform: uppercase; }
.form-card h1 { margin: 0; color: var(--text-primary); font-size: 30px; line-height: 1.25; }
.form-card header > p:last-child { margin: 8px 0 0; color: var(--text-secondary); font-size: 14px; }
.field-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 0 16px; }
.auth-form :deep(.el-form-item) { margin-bottom: 14px; }
.auth-form :deep(.el-form-item__label) { padding-bottom: 6px; color: #42514e; font-weight: 600; }
.auth-form :deep(.el-input__wrapper) { min-height: 42px; padding: 0 12px; }
.submit-button { width: 100%; height: 46px; margin-top: 8px; font-size: 16px; }
.switch-tip { margin: 20px 0 0; color: var(--text-muted); font-size: 14px; text-align: center; }
.switch-tip a { margin-left: 5px; color: var(--brand-primary-dark); font-weight: 700; text-decoration: none; }
@media (max-width: 560px) { .field-grid { grid-template-columns: 1fr; } .form-card { padding: 26px 20px; } }
@media (max-height: 760px) and (min-width: 861px) { .form-card { padding-top: 22px; padding-bottom: 22px; } .form-card header { margin-bottom: 14px; } .auth-form :deep(.el-form-item) { margin-bottom: 8px; } .auth-form :deep(.el-input__wrapper) { min-height: 38px; } }
</style>
