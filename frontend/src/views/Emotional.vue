<template>
    <div class="emotional-page">
        <Pagehead title="状态打卡记录" />
        <Tablesearch :formItem="formItem" @search="handleSearch" />
        <el-table
            v-loading="loading"
            :data="tableData"
            class="emotion-table"
            size="small"
            table-layout="fixed"
            style="width: 100%; margin-top: 25px"
        >
            <el-table-column prop="id" label="ID" width="70" show-overflow-tooltip />
            <el-table-column label="会话ID" width="80" align="center">
                <template #default="scope">
                    <el-avatar :size="32">{{ scope.row.nickname }}</el-avatar>
                </template>
            </el-table-column>
            <el-table-column prop="diaryDate" label="记录日期" width="120" show-overflow-tooltip />
            <el-table-column label="状态评分" width="172">
                <template #default="scope">
                    <el-rate
                        class="mood-rate"
                        :model-value="Number(scope.row.moodScore) || 0"
                        :max="10"
                        disabled
                    />
                </template>
            </el-table-column>
            <el-table-column label="生活指标" width="165">
                <template #default="scope">
                    <div class="life-indicators">
                        <span>睡眠：{{ scope.row.sleepQuality }}/5</span>
                        <span>压力：{{ scope.row.stressLevel }}/5</span>
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="emotionTriggers" label="影响状态的事件" min-width="135" show-overflow-tooltip />
            <el-table-column prop="diaryContent" label="补充记录" min-width="160" show-overflow-tooltip />
            <el-table-column label="操作" width="105" align="center" fixed="right">
                <template #default="scope">
                    <div class="action-buttons">
                        <el-button link type="primary" size="small" @click="handleDetail(scope.row)">详情</el-button>
                        <el-button
                            link
                            type="danger"
                            size="small"
                            :loading="deletingId === scope.row.id"
                            @click="handleDelete(scope.row)"
                        >
                            删除
                        </el-button>
                    </div>
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
            title="状态打卡详情"
            width="900px"
            top="3vh"
            class="emotion-detail-dialog"
            destroy-on-close
        >
            <div class="detail-content">
                <section class="detail-section">
                    <h3 class="detail-section-title">用户信息</h3>
                    <el-descriptions :column="2" border>
                        <el-descriptions-item label="用户名">
                            {{ displayValue(selectedDiary.username) }}
                        </el-descriptions-item>
                        <el-descriptions-item label="昵称">
                            {{ displayValue(selectedDiary.nickname) }}
                        </el-descriptions-item>
                        <el-descriptions-item label="用户ID">
                            {{ displayValue(selectedDiary.userId) }}
                        </el-descriptions-item>
                        <el-descriptions-item label="记录日期">
                            {{ displayValue(selectedDiary.diaryDate) }}
                        </el-descriptions-item>
                    </el-descriptions>
                </section>

                <section class="detail-section">
                    <h3 class="detail-section-title">状态记录</h3>
                    <el-descriptions :column="2" border>
                        <el-descriptions-item label="状态评分">
                            <div class="score-content">
                                <el-rate
                                    :model-value="Number(selectedDiary.moodScore) || 0"
                                    :max="10"
                                    disabled
                                />
                                <span>{{ displayValue(selectedDiary.moodScore) }}</span>
                            </div>
                        </el-descriptions-item>
                        <el-descriptions-item label="主要感受">
                            <el-tag effect="plain">
                                {{ displayValue(selectedDiary.dominantEmotion) }}
                            </el-tag>
                        </el-descriptions-item>
                        <el-descriptions-item label="睡眠质量">
                            {{ formatScale(selectedDiary.sleepQuality) }}
                        </el-descriptions-item>
                        <el-descriptions-item label="压力水平">
                            {{ formatScale(selectedDiary.stressLevel) }}
                        </el-descriptions-item>
                    </el-descriptions>
                </section>

                <section class="detail-section">
                    <h3 class="detail-section-title">补充记录</h3>
                    <el-descriptions :column="1" border>
                        <el-descriptions-item label="影响状态的事件">
                            <div class="pre-wrap-text">{{ displayValue(selectedDiary.emotionTriggers) }}</div>
                        </el-descriptions-item>
                        <el-descriptions-item label="补充记录">
                            <div class="pre-wrap-text">{{ displayValue(selectedDiary.diaryContent) }}</div>
                        </el-descriptions-item>
                    </el-descriptions>
                </section>

                <section class="detail-section">
                    <h3 class="detail-section-title">AI状态分析</h3>
                    <template v-if="aiAnalysis">
                        <el-descriptions :column="2" border>
                            <el-descriptions-item label="主要感受">
                                <el-tag effect="plain" type="info">
                                    {{ displayValue(aiAnalysis.primaryEmotion) }}
                                </el-tag>
                            </el-descriptions-item>
                            <el-descriptions-item label="状态强度">
                                <div class="emotion-progress">
                                    <el-progress
                                        :percentage="emotionScore"
                                        :stroke-width="9"
                                        :show-text="false"
                                    />
                                    <span>{{ emotionScore }}%</span>
                                </div>
                            </el-descriptions-item>
                            <el-descriptions-item label="风险等级">
                                <el-tag :type="riskLevel.type" effect="light">
                                    {{ riskLevel.text }}
                                </el-tag>
                            </el-descriptions-item>
                            <el-descriptions-item label="状态性质">
                                <el-tag :type="emotionNature.type" effect="light">
                                    {{ emotionNature.text }}
                                </el-tag>
                            </el-descriptions-item>
                            <el-descriptions-item label="关键词" :span="2">
                                <div v-if="analysisKeywords.length" class="keyword-list">
                                    <el-tag
                                        v-for="keyword in analysisKeywords"
                                        :key="keyword"
                                        size="small"
                                        effect="plain"
                                    >
                                        {{ keyword }}
                                    </el-tag>
                                </div>
                                <span v-else>--</span>
                            </el-descriptions-item>
                            <el-descriptions-item label="调整建议" :span="2">
                                <div class="pre-wrap-text">{{ displayValue(aiAnalysis.suggestion) }}</div>
                            </el-descriptions-item>
                            <el-descriptions-item label="风险描述" :span="2">
                                <div class="pre-wrap-text">{{ displayValue(aiAnalysis.riskDescription) }}</div>
                            </el-descriptions-item>
                            <el-descriptions-item label="改善建议" :span="2">
                                <ul v-if="improvementSuggestions.length" class="suggestion-list">
                                    <li
                                        v-for="suggestion in improvementSuggestions"
                                        :key="suggestion"
                                    >
                                        {{ suggestion }}
                                    </li>
                                </ul>
                                <span v-else>--</span>
                            </el-descriptions-item>
                            <el-descriptions-item label="分析时间" :span="2">
                                {{ analysisTime }}
                            </el-descriptions-item>
                        </el-descriptions>
                    </template>
                    <el-empty
                        v-else
                        :image-size="72"
                        :description="analysisStatusText"
                    />
                </section>

                <section class="detail-section">
                    <h3 class="detail-section-title">时间信息</h3>
                    <el-descriptions :column="2" border>
                        <el-descriptions-item label="创建时间">
                            {{ displayValue(selectedDiary.createdAt) }}
                        </el-descriptions-item>
                        <el-descriptions-item label="更新时间">
                            {{ displayValue(selectedDiary.updatedAt) }}
                        </el-descriptions-item>
                    </el-descriptions>
                </section>
            </div>

            <template #footer>
                <el-button @click="detailDialogVisible = false">关闭</el-button>
            </template>
        </el-dialog>
    </div>
</template>
<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import Pagehead from '@/components/Pagehead.vue'
import Tablesearch from '@/components/Tablesearch.vue'
import { deleteEmotionDiary, getEmotionDiaryPage } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const formItem = [
    {
        prop: 'userId',
        label: '用户ID',
        type: 'input',
        placeholder: '请输入用户ID'
    },
    {
        prop: 'moodRange',
        label: '状态评分',
        type: 'select',
        placeholder: '请选择评分范围',
        options: [
            {
                label: '低分 1-3',
                value: '1-3'
            },
            {
                label: '中分 4-6',
                value: '4-6'
            },
            {
                label: '高分 7-10',
                value: '7-10'
            }
        ]
    },
    {
        prop: 'dominantEmotion',
        label: '主要感受',
        type: 'input',
        placeholder: '请输入主要感受'
    }
]

const scoreRangeMap = {
    '1-3': ['1', '3'],
    '4-6': ['4', '6'],
    '7-10': ['7', '10']
}

const tableData = ref([])
const loading = ref(false)
const searchParams = ref({})
const detailDialogVisible = ref(false)
const selectedDiary = ref({})
const aiAnalysis = ref(null)
const deletingId = ref(null)
const pagination = reactive({
    currentPage: 1,
    size: 10,
    total: 0
})

const riskLevelMap = {
    0: { text: '状态稳定', type: 'success' },
    1: { text: '有些波动', type: 'warning' },
    2: { text: '需要支持', type: 'warning' },
    3: { text: '立即求助', type: 'danger' }
}

const analysisStatusMap = {
    PENDING: 'AI状态分析尚未完成',
    PROCESSING: 'AI状态分析进行中',
    FAILED: 'AI状态分析失败',
    COMPLETED: '暂无AI状态分析内容'
}

const emotionScore = computed(() => {
    const score = Number(aiAnalysis.value?.emotionScore)
    return Number.isFinite(score) ? Math.min(100, Math.max(0, score)) : 0
})

const riskLevel = computed(() => {
    const level = Number(aiAnalysis.value?.riskLevel)
    return riskLevelMap[level] || { text: '未知', type: 'info' }
})

const emotionNature = computed(() => {
    if (aiAnalysis.value?.isNegative === true) {
        return { text: '负向状态', type: 'danger' }
    }
    if (aiAnalysis.value?.isNegative === false) {
        return { text: '正向状态', type: 'success' }
    }
    return { text: '中性状态', type: 'info' }
})

const analysisKeywords = computed(() => (
    Array.isArray(aiAnalysis.value?.keywords) ? aiAnalysis.value.keywords.filter(Boolean) : []
))

const improvementSuggestions = computed(() => (
    Array.isArray(aiAnalysis.value?.improvementSuggestions)
        ? aiAnalysis.value.improvementSuggestions.filter(Boolean)
        : []
))

const analysisTime = computed(() => {
    return displayValue(selectedDiary.value.aiAnalysisUpdatedAt || aiAnalysis.value?.timestamp)
})

const analysisStatusText = computed(() => {
    return analysisStatusMap[selectedDiary.value.aiAnalysisStatus] || '暂无AI状态分析结果'
})

const displayValue = (value) => {
    return value === null || value === undefined || value === '' ? '--' : value
}

const formatScale = (value) => {
    return value === null || value === undefined || value === '' ? '--' : `${value}/5`
}

const parseAiAnalysis = (analysis) => {
    if (analysis && typeof analysis === 'object') {
        return analysis
    }
    if (typeof analysis !== 'string' || !analysis.trim()) {
        return null
    }

    try {
        const parsed = JSON.parse(analysis)
        return parsed && typeof parsed === 'object' ? parsed : null
    } catch (error) {
        return null
    }
}

const handleDetail = (row) => {
    selectedDiary.value = row
    aiAnalysis.value = parseAiAnalysis(row.aiEmotionAnalysis)
    detailDialogVisible.value = true
}

const handleDelete = async (row) => {
    if (row?.id === null || row?.id === undefined) return

    try {
        await ElMessageBox.confirm(
            `确认删除用户“${displayValue(row.nickname || row.username)}”在 ${displayValue(row.diaryDate)} 的状态打卡吗？删除后无法恢复。`,
            '删除状态打卡',
            {
                confirmButtonText: '确定删除',
                cancelButtonText: '取消',
                type: 'warning'
            }
        )

        deletingId.value = row.id
        await deleteEmotionDiary(row.id)
        ElMessage.success('删除成功')

        if (tableData.value.length === 1 && pagination.currentPage > 1) {
            pagination.currentPage -= 1
        }
        await fetchTableData()
    } catch (action) {
        if (action !== 'cancel' && action !== 'close') {
            ElMessage.error('删除失败')
        }
    } finally {
        deletingId.value = null
    }
}

const normalizeSearchParams = (formData = {}) => {
    const params = {}

    if (formData.userId) {
        params.userId = String(formData.userId).trim()
    }
    if (formData.dominantEmotion) {
        params.dominantEmotion = String(formData.dominantEmotion).trim()
    }

    const moodRange = scoreRangeMap[formData.moodRange]
    if (moodRange) {
        params.minMoodScore = moodRange[0]
        params.maxMoodScore = moodRange[1]
    }

    return params
}

const fetchTableData = async () => {
    loading.value = true
    try {
        const res = await getEmotionDiaryPage({
            current: String(pagination.currentPage),
            size: String(pagination.size),
            ...searchParams.value
        })
        const page = res.data || {}
        tableData.value = page.records || []
        pagination.total = Number(page.total) || 0
    } catch (error) {
        tableData.value = []
        pagination.total = 0
        ElMessage.error('获取状态打卡记录失败')
    } finally {
        loading.value = false
    }
}

const handleSearch = (formData = {}) => {
    searchParams.value = normalizeSearchParams(formData)
    pagination.currentPage = 1
    fetchTableData()
}

const handleChange = (page) => {
    pagination.currentPage = page
    fetchTableData()
}

onMounted(() => {
    fetchTableData()
})
</script>
<style scoped>
.life-indicators,
.action-buttons {
    display: flex;
    align-items: center;
    white-space: nowrap;
}

.life-indicators {
    gap: 10px;
    font-size: 13px;
}

.action-buttons {
    justify-content: center;
    gap: 6px;
}

.action-buttons :deep(.el-button + .el-button) {
    margin-left: 0;
}

:deep(.emotion-table .el-table__cell) {
    padding: 6px 0;
}

:deep(.mood-rate) {
    height: 20px;
    white-space: nowrap;
}

:deep(.mood-rate .el-rate__icon) {
    margin-right: 1px;
    font-size: 15px;
}

.detail-content {
    max-height: calc(94vh - 150px);
    padding-right: 4px;
    overflow-y: auto;
}

.detail-section + .detail-section {
    margin-top: 26px;
}

.detail-section-title {
    margin: 0 0 14px;
    color: #303133;
    font-size: 17px;
}

.score-content,
.emotion-progress,
.keyword-list {
    display: flex;
    align-items: center;
}

.score-content {
    gap: 10px;
}

.score-content :deep(.el-rate) {
    height: 22px;
    white-space: nowrap;
}

.score-content :deep(.el-rate__icon) {
    margin-right: 2px;
    font-size: 18px;
}

.emotion-progress {
    gap: 10px;
}

.emotion-progress :deep(.el-progress) {
    width: 160px;
}

.keyword-list {
    flex-wrap: wrap;
    gap: 8px;
}

.pre-wrap-text {
    line-height: 1.7;
    overflow-wrap: anywhere;
    white-space: pre-wrap;
}

.suggestion-list {
    margin: 0;
    padding-left: 20px;
    line-height: 1.8;
}

:deep(.emotion-detail-dialog) {
    max-width: 94%;
    border-radius: 6px;
}

:deep(.emotion-detail-dialog .el-dialog__body) {
    padding-top: 8px;
}

:deep(.emotion-detail-dialog .el-descriptions__label.el-descriptions__cell) {
    width: 138px;
    color: #606266;
    font-weight: 600;
}

@media (max-width: 768px) {
    :deep(.emotion-detail-dialog .el-descriptions__table) {
        table-layout: auto;
    }

    :deep(.emotion-detail-dialog .el-descriptions__label.el-descriptions__cell) {
        width: auto;
    }

    .emotion-progress :deep(.el-progress) {
        width: 110px;
    }
}
</style>
