<template>
    <div class="consultation-page">
        <Pagehead title="复盘记录" />
        <el-table
            :data="tableData"
            style="width: 100%; margin-top: 20px"
        >
            <el-table-column label="会话ID" width="200">
                <template #default="scope">
                    <div class="session-user">
                        <el-avatar :size="40">{{scope.row.userNickname}}</el-avatar>
                        <span class="session-user-name">{{scope.row.userNickname}}</span>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="复盘内容" min-width="320">
                <template #default="scope">
                    <div class="session-title">{{scope.row.sessionTitle}}</div>
                    <div class="session-content">{{scope.row.lastMessageContent}}</div>
                </template>
            </el-table-column>
            <el-table-column label="消息数" prop="messageCount" width="100" align="center"/>
            <el-table-column label="时间" prop="lastMessageTime" width="180">
                <template #default="scope">
                    <span class="session-time">{{scope.row.lastMessageTime}}</span>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="120" align="center">
                <template #default="scope">
                    <el-button type="primary" text @click="handleDetail(scope.row)">详情</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
            layout="prev, pager, next"
            :current-page="pagination.currentPage"
            :page-size="pagination.size"
            :total="pagination.total"
            :hide-on-single-page="false"
            @current-change="handleChange"
            style="margin-top: 20px;"
        />

        <el-dialog
            v-model="detailDialogVisible"
            title="复盘详情"
            width="95%"
            top="3vh"
            class="session-detail-dialog"
            destroy-on-close
        >
            <div class="session-summary">
                <div class="summary-row">
                    <span class="summary-label">用户：</span>
                    <span>{{ selectedSession.userNickname || '--' }}</span>
                </div>
                <div class="summary-row">
                    <span class="summary-label">开始时间：</span>
                    <span>{{ sessionStartTime }}</span>
                </div>
                <div class="summary-row">
                    <span class="summary-label">消息数：</span>
                    <span>{{ selectedSession.messageCount ?? '--' }}{{ selectedSession.messageCount == null ? '' : '条' }}</span>
                </div>
            </div>

            <h3 class="records-title">对话记录</h3>
            <div v-loading="detailLoading" class="message-list">
                <el-empty
                    v-if="!detailLoading && sessionDetail.length === 0"
                    description="暂无对话记录"
                />
                <div
                    v-for="message in sessionDetail"
                    :key="message.id"
                    class="message-item"
                    :class="message.senderType === 1 ? 'message-user' : 'message-ai'"
                >
                    <div class="message-header">
                        <strong>{{ message.senderType === 1 ? '用户' : '节律教练' }}</strong>
                        <span class="message-time">{{ message.createdAt }}</span>
                    </div>
                    <div class="message-content">{{ message.content }}</div>
                </div>
            </div>

            <template #footer>
                <el-button @click="detailDialogVisible = false">关闭</el-button>
            </template>
        </el-dialog>
    </div>
</template>
<script setup>
import {computed, ref, onMounted, reactive} from 'vue'
import Pagehead from '@/components/Pagehead.vue'
import { getConsultationPage,getSessionDetail } from '@/api/admin'
import { ElMessage } from 'element-plus'
const tableData = ref([])
const pagination = reactive({
    currentPage:1,
    size:10,
    total:0
})
const headleSearch=()=>{
    getConsultationPage(pagination).then(res=>{
        const {records,total} = res.data
        tableData.value = records
        pagination.total = total
    })
}
const handleChange=(page)=>{
    pagination.currentPage = page
    headleSearch()
}
onMounted(()=>{
    headleSearch()
})
const detailDialogVisible = ref(false)
const detailLoading = ref(false)
const selectedSession = ref({})
const sessionDetail = ref([])
const sessionStartTime = computed(() => {
    const session = selectedSession.value
    return session.startTime || session.firstMessageTime || session.createdAt || session.lastMessageTime || '--'
})

const handleDetail = async (row) => {
    selectedSession.value = row
    sessionDetail.value = []
    detailDialogVisible.value = true
    detailLoading.value = true

    try {
        const res = await getSessionDetail(row.id)
        sessionDetail.value = Array.isArray(res.data) ? res.data : []
    } catch (error) {
        ElMessage.error('获取会话详情失败')
    } finally {
        detailLoading.value = false
    }
}
</script>
<style scoped>
.consultation-page {
    padding: 0;
}
.session-user {
    display: flex;
    align-items: center;
    gap: 12px;
}
.session-user-name {
    font-weight: 500;
    color: var(--text-primary);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}
.session-title {
    font-weight: 500;
    color: #303133;
    margin-bottom: 4px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}
.session-content {
    color: #909399;
    font-size: 13px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}
.session-time {
    color: #606266;
    font-size: 13px;
}
.session-summary {
    padding: 18px;
    color: #303133;
    background: #f3f8f6;
    border: 1px solid var(--border-color);
    border-radius: 10px;
}
.summary-row {
    display: flex;
    line-height: 32px;
}
.summary-label {
    flex: 0 0 100px;
    font-weight: 500;
}
.records-title {
    margin: 26px 0 16px;
    color: #303133;
    font-size: 18px;
}
.message-list {
    box-sizing: border-box;
    min-height: 180px;
    max-height: calc(100vh - 390px);
    padding: 18px;
    overflow-y: auto;
    border: 1px solid #ebeef5;
    border-radius: 10px;
}
.message-item {
    padding: 16px;
    border-radius: 10px;
}
.message-item + .message-item {
    margin-top: 14px;
}
.message-user { background: #eef5f3; }
.message-ai { background: #fff8e9; }
.message-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 20px;
    margin-bottom: 8px;
    color: #303133;
}
.message-time {
    flex-shrink: 0;
    color: #909399;
    font-size: 13px;
    font-weight: 400;
}
.message-content {
    color: #4e5969;
    line-height: 1.7;
    overflow-wrap: anywhere;
    white-space: pre-wrap;
}
:deep(.session-detail-dialog) {
    max-width: 1400px;
    border-radius: 6px;
}
:deep(.session-detail-dialog .el-dialog__body) {
    padding-top: 8px;
}
@media (max-width: 768px) {
    .summary-label {
        flex-basis: 82px;
    }
    .message-list {
        max-height: calc(100vh - 420px);
        padding: 10px;
    }
    .message-header {
        align-items: flex-start;
        flex-direction: column;
        gap: 4px;
    }
}
</style>
