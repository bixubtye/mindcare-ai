<template>
  <div class="common-layout">
    <el-container>
      <el-aside :width="isCollapsed ? '64px' : '220px'" class="layout-aside">
        <Sidebar />
      </el-aside>
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
import { computed } from 'vue'
import Sidebar from './Sidebar.vue'
import Navbar from './Navbar.vue'
import { useAdminStore } from '@/stores/admin'

const adminStore = useAdminStore()
const isCollapsed = computed(() => adminStore.isCollapsed)
</script>
<style lang="scss" scoped>
.common-layout {
  height: 100vh;

  :deep(.el-container) {
    height: 100%;
  }

  :deep(.el-aside) {
    height: 100%;
    transition: width 0.3s;
    overflow: hidden;
  }
  .main-content {
  margin-top: 16px;
  min-height: calc(100% - 16px);
  padding: 20px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 21, 41, 0.06);
  box-sizing: border-box;
}
}

</style>
