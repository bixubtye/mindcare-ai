<template>
  <div class="register">
    <header class="register-header">
      <h1>创建您的账户</h1>
      <p>请填写注册信息</p>
    </header>

    <el-form :model="form" label-position="top" class="register-form" :rules="rules" ref="formRef">
      <el-form-item prop="username" label="用户名或邮箱">
        <el-input v-model="form.username" size="large" placeholder="请输入用户名或邮箱" autocomplete="username" />
      </el-form-item>

      <el-form-item prop="email" label="邮箱">
        <el-input v-model="form.email" size="large" type="email" placeholder="请输入邮箱" autocomplete="email" />
      </el-form-item>

      <el-form-item prop="nickname" label="昵称">
        <el-input v-model="form.nickname" size="large" placeholder="请输入昵称（可选）" />
      </el-form-item>

      <el-form-item prop="phone" label="手机号">
        <el-input v-model="form.phone" size="large" type="tel" placeholder="请输入手机号（可选）" autocomplete="tel" />
      </el-form-item>

      <el-form-item prop="password" label="密码">
        <el-input v-model="form.password" size="large" type="password" show-password placeholder="请输入密码" autocomplete="new-password" />
      </el-form-item>

      <el-form-item prop="confirmPassword" class="last-field" label="确认密码">
        <el-input v-model="form.confirmPassword" size="large" type="password" placeholder="请再次输入密码" autocomplete="new-password" />
      </el-form-item>

      <el-button type="primary" class="register-button" @click="submitForm">创建用户</el-button>
    </el-form>

    <p class="login-prompt">已有账户？ <router-link to="/auth/login">立即登录</router-link></p>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { register } from '@/api/frontend'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const form = reactive({
    "username": "",
    "email": "",
    "nickname": "",
    "phone": "",
    "password": "",
    "confirmPassword": "",
    "gender": 0,
    "userType": 1

})
const rules = reactive({
    username: [{ required: true, message: '请输入用户名或邮箱', trigger: 'blur' }],
    email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
    confirmPassword: [{ required: true, message: '请再次输入密码', trigger: 'blur' }],
})
const submitForm=async () => {
    if(!formRef.value) {
        return
    }
    formRef.value.validate(async(valid) => {
        register(form).then(() => {
            ElMessage.success('注册成功')
            router.push({ path: '/auth/login' })
        })
    })
}
</script>

<style scoped>
.register {
  box-sizing: border-box;
  width: 100%;
  max-width: 432px;
  margin: 0 auto;
  background: #fff;
}

.register-header {
  margin-bottom: 24px;
  text-align: center;
}

.register-header h1 {
  margin: 0;
  color: #101010;
  font-size: 30px;
  font-weight: 700;
  line-height: 1.2;
  letter-spacing: 0;
}

.register-header p {
  margin: 8px 0 0;
  color: #67717d;
  font-size: 16px;
  line-height: 20px;
}

.register-form :deep(.el-form-item) {
  margin-bottom: 14px;
}

.register-form :deep(.el-form-item.last-field) {
  margin-bottom: 0;
}

.register-form :deep(.el-form-item__label) {
  height: auto;
  margin-bottom: 0;
  padding: 0 0 7px;
  color: #565e68;
  font-size: 14px;
  line-height: 18px;
}

.required-mark {
  margin-right: 4px;
  color: #df6b71;
}

.register-form :deep(.el-input__wrapper) {
  box-sizing: border-box;
  height: 40px;
  padding: 0 12px;
  border-radius: 5px;
  background: #fff;
  box-shadow: 0 0 0 1px #d8dce2 inset;
}

.register-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #5a98ef inset, 0 0 0 2px rgb(90 152 239 / 12%);
}

.register-form :deep(.el-input__inner) {
  height: 100%;
  color: #30343a;
  font-size: 14px;
}

.register-form :deep(.el-input__inner::placeholder) {
  color: #a8adb5;
}

.register-button {
  width: 100%;
  height: 42px;
  margin-top: 28px;
  border-color: #5a98ef;
  border-radius: 5px;
  background: #5a98ef;
  color: #fff;
  font-size: 14px;
  font-weight: 600;
}

.register-button:hover,
.register-button:focus {
  border-color: #4b89e2;
  background: #4b89e2;
}

.login-prompt {
  margin: 20px 0 0;
  color: #171717;
  font-size: 14px;
  line-height: 22px;
  text-align: center;
}

.login-prompt a {
  color: #593c73;
  text-decoration: none;
}

@media (max-height: 720px) {
  .register-header {
    margin-bottom: 16px;
  }

  .register-header h1 {
    font-size: 27px;
  }

  .register-header p {
    margin-top: 6px;
    font-size: 14px;
    line-height: 18px;
  }

  .register-form :deep(.el-form-item) {
    margin-bottom: 10px;
  }

  .register-form :deep(.el-form-item__label) {
    padding-bottom: 5px;
    font-size: 13px;
    line-height: 16px;
  }

  .register-form :deep(.el-input__wrapper) {
    height: 36px;
  }

  .register-form :deep(.el-input__inner) {
    font-size: 13px;
  }

  .register-button {
    height: 38px;
    margin-top: 18px;
  }

  .login-prompt {
    margin-top: 14px;
    line-height: 20px;
  }
}

@media (max-height: 650px) {
  .register-header {
    margin-bottom: 10px;
  }

  .register-header h1 {
    font-size: 24px;
  }

  .register-header p {
    margin-top: 4px;
    font-size: 13px;
    line-height: 16px;
  }

  .register-form :deep(.el-form-item) {
    margin-bottom: 6px;
  }

  .register-form :deep(.el-form-item__label) {
    padding-bottom: 3px;
    font-size: 12px;
    line-height: 14px;
  }

  .register-form :deep(.el-input__wrapper) {
    height: 32px;
  }

  .register-form :deep(.el-input__inner) {
    font-size: 12px;
  }

  .register-button {
    height: 34px;
    margin-top: 10px;
    font-size: 12px;
  }

  .login-prompt {
    margin-top: 8px;
    font-size: 12px;
    line-height: 18px;
  }
}

@media (max-width: 768px) {
  :global(.auth-layout:has(.register) .left-section) {
    display: none;
  }
}
</style>
