<template>
  <el-menu
    :collapse="isCollapsed"
    :default-active="activeMenu"
    class="el-menu-vertical-demo"
    router
    @open="handleOpen"
    @close="handleClose"
  >
    <div class="brand" :class="{ 'is-collapse': isCollapsed }">
      <el-image :src="brandLogo" alt="logo" class="brand-logo" />
      <div v-show="!isCollapsed" class="info-card">
        <h1 class="brand-title">心理健康AI助手</h1>
        <p class="brand-subtitle">管理后台</p>
      </div>
    </div>
    <el-menu-item
      v-for="item in menuRoutes"
      :key="item.path"
      :index="'/back/' + item.path"
    >
      <el-icon><component :is="iconMap[item.meta?.icon]" /></el-icon>
      <span>{{ item.meta?.title }}</span>
    </el-menu-item>
  </el-menu>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  PieChart,
  Reading,
  ChatRound,
  Sunrise
} from '@element-plus/icons-vue'
import brandLogo from '@/assets/robot.png'
import { useAdminStore } from '@/stores/admin'

const adminStore = useAdminStore()
const isCollapsed = computed(() => adminStore.isCollapsed)

const iconMap = {
  PieChart,
  Reading,
  ChatRound,
  Sunrise
}

const route = useRoute()
const router = useRouter()

const menuRoutes = computed(() => {
  const backRoute = router.options.routes.find(r => r.path === '/back')
  return backRoute?.children || []
})

const activeMenu = computed(() => route.path)

const handleOpen = (key) => {}
const handleClose = (key) => {}
</script>

<style lang="scss" scoped>
.el-menu-vertical-demo {
  height: 100%;
  border-right: none;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  border-bottom: 1px solid var(--el-menu-border-color, #e4e7ed);
  margin-bottom: 8px;

  &.is-collapse {
    justify-content: center;
    padding: 16px 0;
  }

  .brand-logo {
    width: 40px;
    height: 40px;
    flex-shrink: 0;
  }

  .info-card {
    display: flex;
    flex-direction: column;
    line-height: 1.3;
  }

  .brand-title {
    font-size: 15px;
    font-weight: 600;
    color: var(--el-menu-text-color, #303133);
    margin: 0;
  }

  .brand-subtitle {
    font-size: 12px;
    color: var(--el-menu-text-color-light, #909399);
    margin: 2px 0 0;
  }
}
</style>
