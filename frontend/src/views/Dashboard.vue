<template>
    <div class="dashboard-page">
        <div class="overview-grid">
            <article class="overview-card">
                <div class="card-icon card-icon--purple">
                    <el-icon><User /></el-icon>
                </div>
                <div class="card-content">
                    <p class="card-title">总用户数</p>
                    <p class="card-value">{{ aiData.systemOverview?.totalUsers ?? 0 }}</p>
                    <p class="card-subtitle">活跃用户: {{ aiData.systemOverview?.activeUsers ?? 0 }}</p>
                </div>
            </article>
            <article class="overview-card">
                <div class="card-icon card-icon--pink">
                    <el-icon><Notebook /></el-icon>
                </div>
                <div class="card-content">
                    <p class="card-title">情绪日志</p>
                    <p class="card-value">{{ aiData.systemOverview?.totalDiaries ?? 0 }}</p>
                    <p class="card-subtitle">今日新增: {{ aiData.systemOverview?.todayNewDiaries ?? 0 }}</p>
                </div>
            </article>
            <article class="overview-card">
                <div class="card-icon card-icon--blue">
                    <el-icon><ChatDotRound /></el-icon>
                </div>
                <div class="card-content">
                    <p class="card-title">咨询会话</p>
                    <p class="card-value">{{ aiData.systemOverview?.totalSessions ?? 0 }}</p>
                    <p class="card-subtitle">今日新增: {{ aiData.systemOverview?.todayNewSessions ?? 0 }}</p>
                </div>
            </article>
            <article class="overview-card">
                <div class="card-icon card-icon--green">
                    <el-icon><Sunny /></el-icon>
                </div>
                <div class="card-content">
                    <p class="card-title">平均情绪</p>
                    <p class="card-value">{{ aiData.systemOverview?.avgMoodScore ?? 0 }}/10</p>
                    <p class="card-subtitle">情绪健康指数</p>
                </div>
            </article>
        </div>
        <el-row style="margin-top: 20px;" :gutter="20">
            <el-col :span="12">
                <el-card style="width: 100%;">
                    <template #header>
                        <div class="flex-box">
                            情绪趋势分析
                        </div>
                    </template>
                    <div>
                        <div ref="emotionalChartRef" style="height: 300px;width: 100%;"></div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card style="width: 100%;">
                    <template #header>
                        <div class="flex-box">咨询会话统计</div>
                    </template>
                    <div class="consultation-summary">
                        <div>
                            <span>总会话数</span>
                            <strong>{{ aiData.consultationStats?.totalSessions ?? 0 }}</strong>
                        </div>
                        <div>
                            <span>平均时长</span>
                            <strong>{{ aiData.consultationStats?.avgDurationMinutes ?? 0 }}分钟</strong>
                        </div>
                        <div>
                            <span>活跃用户</span>
                            <strong>{{ aiData.consultationStats?.dailyTrend?.slice(-1)[0]?.userCount ?? 0 }}</strong>
                        </div>
                    </div>
                    <div ref="consultationChartRef" style="height: 230px;width: 100%;"></div>
                </el-card>
            </el-col>
        </el-row>
        <el-row style="margin-top: 20px;">
            <el-col :span="24">
                <el-card style="width: 100%;">
                    <template #header>
                        <div class="flex-box">用户活跃度趋势</div>
                    </template>
                    <div ref="userActivityChartRef" style="height: 340px;width: 100%;"></div>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ChatDotRound, Notebook, Sunny, User } from '@element-plus/icons-vue'
import { getAnalyticsOverview } from '@/api/admin'
import * as echarts from 'echarts'

const aiData = ref({})
const emotionalChartRef = ref(null)
const consultationChartRef = ref(null)
const userActivityChartRef = ref(null)
let emotionalChart =null
let consultationChart = null
let userActivityChart = null
//初始化图标
const initChats=()=>{
    initEmotionalChart()
    initConsultationChart()
    initUserActivityChart()
}
const initEmotionalChart=()=>{
    if(!emotionalChartRef.value) return
    if(emotionalChart){
        emotionalChart.dispose()
    }
    emotionalChart = echarts.init(emotionalChartRef.value)
    const TreadData=aiData.value.emotionTrend
    emotionalChart.setOption({
        title: {
            text: '情绪趋势分析',
            textStyle: {
               fontSize: 16,
               fontWeight: 600
            },
            left: 'center',
            top: 10
        },
        tooltip: {
            trigger: 'axis',
        },
        legend: {
            data: ['平均情绪评分','记录数量'],
            top: 40
        },
        xAxis: {
            type: 'category',
            data: TreadData.map(item => item.date)
        },
        yAxis: [{
            type: 'value',
            name: '情绪评分',
            position: 'left'
        },{
        type: 'value',
        name: '记录数量',
        position: 'right'
        }],
        series: [{
            name: '情绪评分',
            type: 'line',
            data: TreadData.map(item => item.avgMoodScore),
            smooth: true
        },{
            name: '记录数量',
            type: 'line',
            data: TreadData.map(item => item.recordCount),
            smooth: true
        }]
    })
}

const initConsultationChart=()=>{
    if(!consultationChartRef.value) return
    if(consultationChart){
        consultationChart.dispose()
    }
    consultationChart = echarts.init(consultationChartRef.value)
    const trendData = aiData.value.consultationStats.dailyTrend
    consultationChart.setOption({
        title: {
            text: '咨询活动统计',
            left: 'center',
            textStyle: { fontSize: 16, fontWeight: 600 }
        },
        tooltip: { trigger: 'axis' },
        legend: {
            data: ['会话数量', '参与用户数'],
            top: 30
        },
        grid: { top: 70, left: 45, right: 20, bottom: 35 },
        xAxis: {
            type: 'category',
            data: trendData.map(item => item.date),
            axisLabel: { hideOverlap: true }
        },
        yAxis: { type: 'value' },
        series: [{
            name: '会话数量',
            type: 'bar',
            data: trendData.map(item => item.sessionCount),
            itemStyle: { color: '#5b9fe8' }
        },{
            name: '参与用户数',
            type: 'bar',
            data: trendData.map(item => item.userCount),
            itemStyle: { color: '#f2b440' }
        }]
    })
}

const initUserActivityChart=()=>{
    if(!userActivityChartRef.value) return
    if(userActivityChart){
        userActivityChart.dispose()
    }
    userActivityChart = echarts.init(userActivityChartRef.value)
    const activityData = aiData.value.userActivity
    userActivityChart.setOption({
        title: {
            text: '用户活跃度趋势',
            left: 'center',
            textStyle: { fontSize: 16, fontWeight: 600 }
        },
        tooltip: { trigger: 'axis' },
        legend: {
            data: ['活跃用户', '新增用户', '日记用户', '咨询用户'],
            top: 30
        },
        grid: { top: 70, left: 45, right: 25, bottom: 40 },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: activityData.map(item => item.date),
            axisLabel: { hideOverlap: true }
        },
        yAxis: { type: 'value' },
        series: [{
            name: '活跃用户',
            type: 'line',
            data: activityData.map(item => item.activeUsers),
            smooth: true,
            itemStyle: { color: '#8b83e6' },
            areaStyle: { color: 'rgba(139, 131, 230, 0.18)' }
        },{
            name: '新增用户',
            type: 'line',
            data: activityData.map(item => item.newUsers),
            smooth: true,
            itemStyle: { color: '#efc15b' }
        },{
            name: '日记用户',
            type: 'line',
            data: activityData.map(item => item.diaryUsers),
            smooth: true,
            itemStyle: { color: '#45b89c' }
        },{
            name: '咨询用户',
            type: 'line',
            data: activityData.map(item => item.consultationUsers),
            smooth: true,
            itemStyle: { color: '#efaa92' }
        }]
    })
}

onMounted(async () => {
    const res = await getAnalyticsOverview()
    aiData.value = res.data
    initChats()
})
</script>

<style scoped>
.dashboard-page {
    background: #f5f7fa;
}

.overview-grid {
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 20px;
    min-height: 96px;
}

.overview-card {
    display: flex;
    align-items: center;
    min-width: 0;
    min-height: 96px;
    padding: 14px 18px;
    background: #fff;
    border: 1px solid #ebeef5;
    border-radius: 8px;
    box-shadow: 0 4px 16px rgba(31, 45, 61, 0.08);
    box-sizing: border-box;
    transition: box-shadow 0.2s ease, transform 0.2s ease;
}

.overview-card:hover {
    box-shadow: 0 8px 24px rgba(31, 45, 61, 0.12);
    transform: translateY(-2px);
}

.card-icon {
    display: flex;
    flex: 0 0 56px;
    align-items: center;
    justify-content: center;
    width: 56px;
    height: 56px;
    margin-right: 14px;
    color: #fff;
    border-radius: 12px;
}

.card-icon :deep(.el-icon) {
    font-size: 32px;
}

.card-icon--purple {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.card-icon--pink {
    background: linear-gradient(135deg, #df70d5 0%, #ef5d79 100%);
}

.card-icon--blue {
    background: linear-gradient(135deg, #49aeea 0%, #55dce6 100%);
}

.card-icon--green {
    background: linear-gradient(135deg, #52d98c 0%, #65e5c9 100%);
}

.card-content {
    min-width: 0;
}

.card-title,
.card-value,
.card-subtitle {
    margin: 0;
}

.card-title {
    color: #606266;
    font-size: 14px;
}

.card-value {
    margin-top: 2px;
    color: #303133;
    font-size: 22px;
    font-weight: 600;
    line-height: 1.25;
    overflow-wrap: anywhere;
}

.card-subtitle {
    margin-top: 1px;
    color: #909399;
    font-size: 12px;
}

.consultation-summary {
    display: flex;
    justify-content: space-around;
    text-align: center;
}

.consultation-summary div,
.consultation-summary strong {
    display: flex;
    flex-direction: column;
}

.consultation-summary span {
    color: #909399;
    font-size: 13px;
}

.consultation-summary strong {
    margin-top: 4px;
    color: #303133;
    font-size: 18px;
}

@media (max-width: 1200px) {
    .overview-grid {
        grid-template-columns: repeat(2, minmax(0, 1fr));
    }
}

@media (max-width: 640px) {
    .overview-grid {
        grid-template-columns: 1fr;
        gap: 14px;
    }

    .overview-card {
        min-height: 90px;
        padding: 14px 16px;
    }

    .card-icon {
        flex-basis: 52px;
        width: 52px;
        height: 52px;
    }
}
</style>
