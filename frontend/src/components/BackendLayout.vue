<template>
  <div class="common-layout">
    <el-container>
      <el-aside :width="isCollapsed ? '64px' : '220px'" class="layout-aside" :class="{ 'is-collapsed': isCollapsed }">
        <Sidebar />
      </el-aside>
      <button v-if="isMobile && !isCollapsed" class="aside-overlay" type="button" aria-label="关闭后台导航" @click="adminStore.toggleCollapsed" />
      <el-container>
        <el-header>
          <Navbar />
        </el-header>
        <el-main>
          <router-view class="main-content"></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>
<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import Sidebar from './Sidebar.vue'
import Navbar from './Navbar.vue'
import { useAdminStore } from '@/stores/admin'

const adminStore = useAdminStore()
const isCollapsed = computed(() => adminStore.isCollapsed)
const isMobile = ref(false)

const syncViewport = () => {
  const nextMobile = window.innerWidth <= 768
  if (nextMobile && !isMobile.value && !adminStore.isCollapsed) {
    adminStore.toggleCollapsed()
  }
  isMobile.value = nextMobile
}

onMounted(() => {
  syncViewport()
  window.addEventListener('resize', syncViewport)
})

onBeforeUnmount(() => window.removeEventListener('resize', syncViewport))
</script>
<style lang="scss" scoped>
.common-layout {
  height: 100vh;
  background: var(--page-bg);

  :deep(.el-container) {
    height: 100%;
  }

  :deep(.el-aside) {
    height: 100%;
    transition: width 0.3s;
    overflow: hidden;
    border-right: 1px solid var(--border-color);
    background: #f8fbfa;
  }
  :deep(.el-header) {
    height: 64px;
    padding: 0;
  }
  :deep(.el-main) {
    min-width: 0;
    padding: 20px;
    overflow-x: hidden;
    background: var(--page-bg);
  }
  .main-content {
  min-height: 100%;
  padding: 22px;
  background: #fff;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  box-sizing: border-box;
}
}

@media (max-width: 768px) {
  .common-layout {
    .layout-aside {
      position: fixed;
      inset: 0 auto 0 0;
      z-index: 50;
      width: 220px !important;
      box-shadow: 12px 0 32px rgba(31, 45, 43, .16);
      transform: translateX(0);
      transition: transform .25s ease;
    }
    .layout-aside.is-collapsed { transform: translateX(-100%); }
    :deep(.el-main) { padding: 12px; }
    .main-content { padding: 16px; }
  }

  .aside-overlay {
    position: fixed;
    inset: 0;
    z-index: 40;
    border: 0;
    background: rgba(31, 45, 43, .25);
  }
}

</style>
