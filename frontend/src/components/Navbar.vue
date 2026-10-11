<template>
    <div class="navbar">
        <div class="flex-box">
            <el-button @click="handleCollapse">
                <el-icon><Expand /></el-icon>
            </el-button>
            <p class="page-title">{{ pageTitle }}</p>
        </div>
        <div class="flex-box">
            <el-dropdown @command="handleCommand">
                <div class="flex-box user-info">
                    <el-avatar :size="32">
                        <img src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"/>
                    </el-avatar>
                    <span class="user-name">用户</span>
                    <el-icon><ArrowDown /></el-icon>
                </div>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
    </div>
</template>
<script setup>
import { Expand, ArrowDown } from '@element-plus/icons-vue'
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAdminStore } from '@/stores/admin'
import { ElMessageBox } from 'element-plus'
import { logout } from '@/api/admin'
import { useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const pageTitle = computed(() => route.meta.title || '导航栏')

const handleCollapse = () => {
    useAdminStore().toggleCollapsed()
}

const handleCommand = (command) => {
    if (command === 'logout') {
        // 退出登录
        ElMessageBox.confirm('确定退出登录吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => {
            logout().then(() => {
               localStorage.removeItem('token')
               localStorage.removeItem('userInfo')
               router.push({ path: '/auth/login' })
            })
        })
    }
}
</script>
<style lang="scss" scoped>
.navbar {
height: 64px;
display: flex;
align-items: center;
justify-content: space-between;
padding: 0 15px;
background: white;
box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
border-bottom: 1px solid #e5e7eb;
.flex-box {
    display: flex;
    align-items: center;
    justify-content: center;
}
.page-title {
    font-size: 18px;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 0 12px;
    letter-spacing: 0.5px;
}
.user-info {
    gap: 8px;
    cursor: pointer;
}
.user-name {
    font-size: 14px;
    color: #374151;
}
}


</style>
