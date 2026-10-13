<template>
  <div class="frontend-layout">
    <header class="site-header">
      <div class="navbar-container">
        <router-link class="navbar-brand" to="/" aria-label="校园节律首页">
          <BrandMark class="nav-mark" />
          <span class="brand-copy">
            <strong>校园节律</strong>
            <small>CampusRhythm</small>
          </span>
        </router-link>

        <button class="mobile-menu-button" type="button" :aria-expanded="menuOpen" aria-label="打开导航" @click="menuOpen = !menuOpen">
          <el-icon><Menu /></el-icon>
        </button>

        <nav class="nav-session" :class="{ 'is-open': menuOpen }" aria-label="主导航" @click="menuOpen = false">
          <router-link to="/">首页</router-link>
          <router-link v-if="islogin" to="/consultation">压力复盘</router-link>
          <router-link v-if="islogin" to="/emotion-dairy">状态打卡</router-link>
          <router-link to="/knowledge">应对工具箱</router-link>
          <div class="account-actions">
            <el-button v-if="islogin" plain @click="handleLogout">退出登录</el-button>
            <template v-else>
              <router-link class="login-link" to="/auth/login">登录</router-link>
              <router-link to="/auth/register"><el-button type="primary">注册</el-button></router-link>
            </template>
          </div>
        </nav>
      </div>
    </header>

    <div class="main-container"><router-view /></div>

    <footer class="footer-container">
      <BrandMark tone="light" class="footer-mark" />
      <p>© 2026 校园节律 CampusRhythm · 找到适合自己的恢复节奏</p>
    </footer>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { Menu } from '@element-plus/icons-vue'
import { logout } from '@/api/admin'
import { useRouter } from 'vue-router'
import BrandMark from '@/components/BrandMark.vue'

const islogin = ref(false)
const menuOpen = ref(false)
const router = useRouter()

onMounted(() => {
  islogin.value = localStorage.getItem('token') !== null
})

const handleLogout = () => {
  logout().then(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push({ path: '/auth/login' })
  })
}
</script>

<style scoped>
.frontend-layout { min-height: 100vh; display: flex; flex-direction: column; background: var(--page-bg); }
.site-header { position: sticky; top: 0; z-index: 30; border-bottom: 1px solid rgba(221, 231, 227, 0.92); background: rgba(255, 255, 255, 0.95); box-shadow: 0 4px 18px rgba(35, 82, 75, 0.04); backdrop-filter: blur(12px); }
.navbar-container { display: flex; align-items: center; justify-content: space-between; width: min(1320px, calc(100% - 40px)); min-height: 74px; margin: 0 auto; }
.navbar-brand { display: inline-flex; align-items: center; gap: 12px; color: var(--text-primary); text-decoration: none; }
.nav-mark { width: 44px; height: 44px; border-radius: 13px; }
.brand-copy { display: flex; flex-direction: column; line-height: 1.05; }
.brand-copy strong { font-size: 20px; letter-spacing: 0.06em; }
.brand-copy small { margin-top: 6px; color: var(--text-secondary); font-size: 11px; letter-spacing: 0.08em; }
.nav-session { display: flex; align-items: center; gap: clamp(18px, 2.7vw, 38px); }
.nav-session > a { position: relative; padding: 27px 0 25px; color: #465653; font-size: 15px; font-weight: 600; text-decoration: none; white-space: nowrap; }
.nav-session > a::after { position: absolute; right: 0; bottom: 15px; left: 0; height: 3px; border-radius: 4px; background: var(--brand-primary); content: ''; opacity: 0; transform: scaleX(0.35); transition: 0.2s ease; }
.nav-session > a:hover, .nav-session > a.router-link-exact-active { color: var(--brand-primary-dark); }
.nav-session > a.router-link-exact-active::after { opacity: 1; transform: scaleX(1); }
.account-actions { display: flex; align-items: center; gap: 14px; padding-left: 2px; }
.account-actions a { text-decoration: none; }
.login-link { color: var(--brand-primary-dark); font-size: 15px; font-weight: 600; }
.mobile-menu-button { display: none; width: 42px; height: 42px; border: 1px solid var(--border-color); border-radius: 11px; background: #fff; color: var(--brand-primary-dark); font-size: 21px; }
.main-container { flex: 1; min-width: 0; }
.footer-container { display: flex; align-items: center; justify-content: center; gap: 12px; min-height: 84px; padding: 18px 24px; background: var(--brand-primary-dark); color: rgba(255, 255, 255, 0.86); text-align: center; }
.footer-container p { margin: 0; font-size: 13px; letter-spacing: 0.02em; }
.footer-mark { width: 36px; height: 36px; border-radius: 10px; }
@media (max-width: 900px) {
  .navbar-container { width: min(100% - 28px, 1320px); }
  .mobile-menu-button { display: inline-flex; align-items: center; justify-content: center; }
  .nav-session { position: absolute; top: 66px; right: 14px; left: 14px; display: none; align-items: stretch; gap: 0; padding: 10px; border: 1px solid var(--border-color); border-radius: 14px; background: #fff; box-shadow: var(--shadow-md); }
  .nav-session.is-open { display: flex; flex-direction: column; }
  .nav-session > a { padding: 12px 14px; border-radius: 9px; }
  .nav-session > a::after { display: none; }
  .nav-session > a.router-link-exact-active { background: var(--brand-primary-soft); }
  .account-actions { justify-content: flex-end; margin-top: 6px; padding: 10px 8px 2px; border-top: 1px solid var(--border-color); }
}
@media (max-width: 420px) {
  .navbar-container { width: calc(100% - 24px); }
  .brand-copy small { display: none; }
  .footer-container { flex-direction: column; }
}
</style>
