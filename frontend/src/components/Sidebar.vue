<template>
  <aside class="sidebar-panel" :class="{ 'is-collapse': isCollapsed }">
    <div class="brand" :class="{ 'is-collapse': isCollapsed }">
      <BrandMark class="brand-logo" />
      <div v-show="!isCollapsed" class="info-card">
        <h1 class="brand-title">校园节律</h1>
        <p class="brand-subtitle">状态管理中心</p>
      </div>
    </div>

    <nav class="sidebar-nav" aria-label="后台导航">
      <router-link
        v-for="item in menuRoutes"
        :key="item.path"
        :to="`/back/${item.path}`"
        class="menu-link"
        active-class="is-active"
        @click="handleMenuClick"
      >
        <el-icon><component :is="iconMap[item.meta?.icon]" /></el-icon>
        <span v-show="!isCollapsed">{{ item.meta?.title }}</span>
      </router-link>
    </nav>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ChatRound, PieChart, Reading, Sunrise } from '@element-plus/icons-vue'
import BrandMark from '@/components/BrandMark.vue'
import { useAdminStore } from '@/stores/admin'

const adminStore = useAdminStore()
const isCollapsed = computed(() => adminStore.isCollapsed)
const router = useRouter()

const iconMap = {
  PieChart,
  Reading,
  ChatRound,
  Sunrise
}

const menuRoutes = computed(() => {
  const backRoute = router.options.routes.find((route) => route.path === '/back')
  return backRoute?.children || []
})

const handleMenuClick = () => {
  if (window.innerWidth <= 768 && !adminStore.isCollapsed) {
    adminStore.toggleCollapsed()
  }
}
</script>

<style lang="scss" scoped>
.sidebar-panel {
  height: 100%;
  overflow: hidden;
  background: #f8fbfa;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  min-height: 76px;
  padding: 14px 16px;
  margin-bottom: 12px;
  border-bottom: 1px solid var(--border-color);

  &.is-collapse {
    justify-content: center;
    padding: 16px 0;
  }
}

.brand-logo {
  width: 42px;
  height: 42px;
  flex-shrink: 0;
}

.info-card {
  display: flex;
  min-width: 0;
  flex-direction: column;
  line-height: 1.3;
}

.brand-title {
  margin: 0;
  color: var(--text-primary);
  font-size: 15px;
  font-weight: 700;
  white-space: nowrap;
}

.brand-subtitle {
  margin: 2px 0 0;
  color: var(--text-secondary);
  font-size: 12px;
  white-space: nowrap;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 5px;
  padding: 0 10px;
}

.menu-link {
  display: flex;
  align-items: center;
  gap: 11px;
  min-height: 48px;
  padding: 0 14px;
  border-radius: 10px;
  color: #52615e;
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
  white-space: nowrap;
  cursor: pointer;
  transition: color .18s ease, background-color .18s ease;
}

.menu-link:hover {
  background: var(--brand-primary-soft);
  color: var(--brand-primary-dark);
}

.menu-link.is-active {
  background: #dceee9;
  color: var(--brand-primary-dark);
}

.menu-link:focus-visible {
  outline: 3px solid rgba(52, 124, 114, .25);
  outline-offset: 1px;
}

.menu-link .el-icon {
  flex: none;
  font-size: 18px;
}

.sidebar-panel.is-collapse {
  .sidebar-nav {
    padding: 0 8px;
  }

  .menu-link {
    justify-content: center;
    padding: 0;
  }
}
</style>
