<template>
    <div class="login">
        <!-- 左上角：返回首页 -->
        <div class="back-bar">
            <router-link class="back-link" to="/">
                <el-icon class="back-icon"><ArrowLeft /></el-icon>
                <span>返回首页</span>
            </router-link>
        </div>

        <!-- 中间登录表单 -->
        <div class="login-body">
            <div class="login-card">
                <h2 class="login-title">登录您的账户</h2>
                <p class="login-subtitle">请输入您的登录信息</p>

                <el-form ref="formRef" :model="form" class="login-form" @submit.prevent :rules="rules">
                    <el-form-item prop="username">
                        <el-input
                            v-model="form.username"
                            size="large"
                            placeholder="用户名或邮箱"
                            :prefix-icon="User"
                            clearable
                        />
                    </el-form-item>
                    <el-form-item prop="password">
                        <el-input
                            v-model="form.password"
                            size="large"
                            placeholder="密码"
                            :prefix-icon="Lock"
                            show-password
                            type="password"
                        />
                    </el-form-item>
                    <el-button
                        type="primary"
                        size="large"
                        class="submit-btn"
                        @click="handleLogin"
                    >
                        登录账户
                    </el-button>
                </el-form>

                <p class="register-tip">
                    还没有账户？
                    <router-link to="/auth/register" class="register-link">去注册</router-link>
                </p>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ArrowLeft, User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api/admin'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
const formRef = ref()

const rules = ref({
    username: [{ required: true, message: '请输入用户名或邮箱', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
})

const form = reactive({
    username: '',
    password: ''
})

const router = useRouter()

const handleLogin = async () => {
    if (!formRef.value) return
    try {
        // validate() 返回 Promise：校验通过才往下走，不通过会抛错并自动显示红字
        await formRef.value.validate()
        // 校验通过，调用登录接口
        const res = await login(form)
        // 后端返回结构：{ code, data: { token, userInfo } }
        if (!res.data?.token) {
            ElMessage.error('登录失败')
            return
        }
        localStorage.setItem('token', res.data.token)
        localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo))
        //根据用户角色跳转不同页面
        if(res.data.userInfo.userType===2)
        {
            router.push({ path: '/back/dashboard' })
        }
        else
        {
            router.push({ path: '/' })
        }
        ElMessage.success('登录成功')
    } catch (e) {
        // 校验未通过或请求失败，el-form 自动显示红字
    }
}
</script>

<style scoped>
.login {
    width: 100%;
    align-self: stretch;
    display: flex;
    flex-direction: column;
}

/* 返回首页 */
.back-bar {
    padding: 4px 0;
}

.back-link {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    font-size: 14px;
    color: #6b7280;
    text-decoration: none;
    padding: 6px 10px;
    margin-left: -10px;
    border-radius: 8px;
    transition: color 0.25s, background-color 0.25s;
}

.back-link:hover {
    color: #16a34a;
    background-color: rgba(34, 197, 94, 0.08);
}

.back-icon {
    font-size: 16px;
}

/* 表单居中区域 */
.login-body {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
}

.login-card {
    width: 100%;
    max-width: 400px;
}

.login-title {
    margin: 0;
    font-size: 28px;
    font-weight: 700;
    color: #111827;
    letter-spacing: 1px;
}

.login-subtitle {
    margin: 10px 0 32px;
    font-size: 14px;
    color: #9ca3af;
}

.login-form {
    width: 100%;
}

/* 输入框间距与圆角细节 */
.login-form :deep(.el-form-item) {
    margin-bottom: 20px;
}

.login-form :deep(.el-input__wrapper) {
    border-radius: 10px;
    padding: 4px 14px;
    box-shadow: 0 0 0 1px #e5e7eb inset;
    transition: box-shadow 0.25s;
}

.login-form :deep(.el-input__wrapper.is-focus) {
    box-shadow: 0 0 0 1px #22c55e inset;
}

.login-form :deep(.el-input__inner) {
    height: 40px;
    font-size: 15px;
}

/* 登录大按钮：与左侧绿色主题呼应 */
.submit-btn {
    width: 100%;
    height: 48px;
    margin-top: 6px;
    font-size: 16px;
    font-weight: 600;
    letter-spacing: 4px;
    border-radius: 10px;
    background-color: #22c55e;
    border-color: #22c55e;
    transition: background-color 0.25s, border-color 0.25s, transform 0.15s;
}

.submit-btn:hover,
.submit-btn:focus {
    background-color: #16a34a;
    border-color: #16a34a;
}

.submit-btn:active {
    transform: scale(0.98);
}

/* 底部注册提示 */
.register-tip {
    margin: 24px 0 0;
    text-align: center;
    font-size: 14px;
    color: #9ca3af;
}

.register-link {
    color: #16a34a;
    font-weight: 500;
    text-decoration: none;
    margin-left: 2px;
    transition: color 0.25s;
}

.register-link:hover {
    color: #22c55e;
    text-decoration: underline;
}
</style>
