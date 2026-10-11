<template>
  <div class="frontend-layout">
    <div class="navbar-container">
        <div class="navbar-brand">
            <el-image style="height: 48px;width: 48px;" :src="iconURL" alt="logo" class="logo-image" />
            <h1 class="brand-title">心理健康AI助手</h1>
        </div>
        <div class="nav-session">
            <router-link to="/">首页</router-link>
            <router-link to="/consultation" v-if="islogin">AI咨询</router-link>
            <router-link to="/emotion-dairy" v-if="islogin">情感日记</router-link>
            <router-link to="/knowledge">知识库</router-link>
            <el-button v-if="islogin" size="medium" @click="handleLogout">退出登录</el-button>
            <template v-else>
                <router-link to="/auth/login">登录</router-link>
                <router-link to="/auth/register">
                    <el-button type="primary" size="medium">注册</el-button>
                </router-link>
            </template>
        </div>
    </div>
    <div class="main-container">
        <router-view></router-view>
    </div>
    <div class="footer-container">
       <div class="footer-bottom">
        <p>&copy; 2026 心理健康AI助手. All rights reserved.</p>
       </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import {logout} from '@/api/admin'
import {useRouter} from 'vue-router'
const iconURL=new URL('@/assets/robot.png',import.meta.url).href
const islogin=ref(false)
onMounted(()=>{
    islogin.value=localStorage.getItem('token')!==null
})
const router=useRouter()
const handleLogout=()=>{
    logout().then(()=>{
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push({ path: '/auth/login' })
    })
}
</script>
<style scoped>
.frontend-layout {
    background: #fff;
    border-bottom: 1px solid #e9edf1;
}
.footer-container {
    background: #000;
}
.footer-bottom {
    color: #fff;
    text-align: center;
    padding: 16px 0;
}
.footer-bottom p {
    margin: 0;
    font-size: 14px;
}
.navbar-container {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: calc(100% - 40px);
    max-width: 1348px;
    height: 74px;
    margin: 0 auto;
}
.navbar-brand {
    display: flex;
    align-items: center;
    gap: 16px;
    flex-shrink: 0;
}
.brand-title {
    margin: 0;
    color: #252a30;
    font-size: 24px;
    font-weight: 700;
    line-height: 1.2;
    letter-spacing: 0;
    white-space: nowrap;
}
.nav-session {
    display: flex;
    align-items: center;
    gap: 46px;
}
.nav-session a {
    color: #303841;
    font-size: 18px;
    font-weight: 500;
    line-height: 1;
    text-decoration: none;
    white-space: nowrap;
}
.nav-session :deep(.el-button) {
    height: 38px;
    padding: 0 16px;
    border-color: #dfe6ec;
    border-radius: 4px;
    background: #fff;
    color: #34404d;
    font-size: 16px;
    font-weight: 500;
    box-shadow: 0 1px 3px rgb(0 0 0 / 4%);
}
@media (max-width: 800px) {
    .navbar-container {
        height: auto;
        min-height: 74px;
        flex-wrap: wrap;
        justify-content: center;
        gap: 16px;
        padding: 12px 0;
    }
    .nav-session {
        justify-content: center;
        flex-wrap: wrap;
        gap: 16px 24px;
    }
}
</style>
