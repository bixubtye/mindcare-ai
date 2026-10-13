<template>
  <main class="diary-page">
    <header class="diary-banner">
      <div class="banner-content">
        <BrandMark tone="light" class="banner-mark" />
        <div><p>用简短记录留住每天的变化</p><h1>每日状态打卡</h1></div>
      </div>
    </header>

    <div class="diary-content">
      <section class="diary-card score-card">
        <span class="step-label">01 · 状态评分</span>
        <h2>今日状态评分</h2>
        <p>综合睡眠、压力和感受，你会给今天的状态打几分？</p>
        <el-rate
          v-model="moodScore"
          :max="10"
          :texts="moodTexts"
          :colors="['#ef6b6b', '#f3b83f', '#71bd65']"
          :low-threshold="3"
          :high-threshold="7"
          show-text
          size="large"
        />
      </section>

      <section class="diary-card">
        <span class="step-label">02 · 当下感受</span>
        <h2>今天最明显的感受</h2>
        <div class="emotion-grid">
          <button
            v-for="mood in emotions"
            :key="mood.name"
            type="button"
            class="emotion-item"
            :class="{ selected: dominantEmotion === mood.name }"
            :style="{ '--emotion-color': mood.color }"
            @click="dominantEmotion = mood.name"
          >
            <span class="emotion-icon" aria-hidden="true">{{ mood.icon }}</span>
            <span>{{ mood.name }}</span>
          </button>
        </div>
      </section>

      <section class="diary-card detail-card">
        <span class="step-label">03 · 影响与记录</span>
        <h2>补充记录</h2>
        <el-form label-position="top">
          <div class="select-row">
            <el-form-item label="睡眠质量">
              <el-select v-model="sleepQuality" placeholder="请选择">
                <el-option v-for="item in sleepOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="压力水平">
              <el-select v-model="stressLevel" placeholder="请选择">
                <el-option v-for="item in stressOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </div>
          <el-form-item label="影响状态的事件">
            <el-input v-model="emotionTriggers" type="textarea" :rows="3" maxlength="1000" show-word-limit placeholder="今天发生了什么，让你的状态出现变化？" />
          </el-form-item>
          <el-form-item label="补充记录">
            <el-input v-model="diaryContent" type="textarea" :rows="5" maxlength="2000" show-word-limit placeholder="可以写下此刻的想法、身体感受，或任何想记住的细节…" />
          </el-form-item>
          <div class="form-actions">
            <el-button @click="resetForm">重置</el-button>
            <el-button type="primary" :loading="submitting" @click="submitDiary">保存今日打卡</el-button>
          </div>
        </el-form>
      </section>
    </div>
  </main>
</template>

<script setup>
import dayjs from 'dayjs'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createEmotionDiary } from '@/api/frontend'
import BrandMark from '@/components/BrandMark.vue'

const moodScore = ref(0)
const dominantEmotion = ref('')
const emotionTriggers = ref('')
const diaryContent = ref('')
const sleepQuality = ref()
const stressLevel = ref()
const submitting = ref(false)

const moodTexts = ['消沉抑郁', '难过低落', '疲惫失落', '略感沮丧', '平静一般', '舒缓安稳', '轻松愉快', '开心满足', '欣喜愉悦', '兴奋欣喜']
const emotions = [
  { name: '开心', icon: '☺', color: '#f2db35' },
  { name: '平静', icon: '☻', color: '#4ca0dc' },
  { name: '焦虑', icon: '😰', color: '#dd452f' },
  { name: '悲伤', icon: '☹', color: '#777' },
  { name: '兴奋', icon: '😃', color: '#3b8b2d' },
  { name: '疲惫', icon: '😪', color: '#37388f' },
  { name: '惊讶', icon: '😮', color: '#d2822c' },
  { name: '困惑', icon: '😕', color: '#222' }
]
const sleepOptions = ['很差', '较差', '一般', '良好', '优秀'].map((label, index) => ({ label, value: index + 1 }))
const stressOptions = ['很低', '较低', '中等', '较高', '很高'].map((label, index) => ({ label, value: index + 1 }))
const diaryDate = () => {
  return dayjs().format('YYYY-MM-DD')
}

const resetForm = () => {
  moodScore.value = 0
  dominantEmotion.value = emotionTriggers.value = diaryContent.value = ''
  sleepQuality.value = stressLevel.value = undefined
}

const submitDiary = async () => {
  if (!moodScore.value || !dominantEmotion.value) return ElMessage.warning('请完成状态评分并选择今天最明显的感受')
  submitting.value = true
  try {
    await createEmotionDiary({
      diaryDate: diaryDate(),
      moodScore: moodScore.value,
      dominantEmotion: dominantEmotion.value,
      emotionTriggers: emotionTriggers.value,
      diaryContent: diaryContent.value,
      sleepQuality: sleepQuality.value,
      stressLevel: stressLevel.value
    })
    ElMessage.success('今日状态已记录')
    resetForm()
  } catch {
    ElMessage.error('提交失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.diary-page {
  min-height: calc(100vh - 74px);
  background: #f6fafb;
  color: #303846;
}

.diary-banner {
  padding: 54px 24px;
  background: linear-gradient(110deg, #8dd23c, #f0b238);
}

.banner-content,
.diary-content {
  width: min(1120px, calc(100% - 48px));
  margin: 0 auto;
}

.banner-content {
  display: flex;
  align-items: center;
  gap: 18px;
  color: #fff;
}

.banner-content span {
  font-size: 58px;
  line-height: 1;
}

.banner-content h1 {
  margin: 0;
  color: #fff;
  font-size: 36px;
  font-weight: 700;
  letter-spacing: 0;
}

.diary-content {
  display: grid;
  gap: 24px;
  padding: 24px 0 48px;
}

.diary-card {
  padding: 28px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 4px 16px rgb(42 66 76 / 8%);
}

.diary-card h2 {
  margin: 0 0 22px;
  color: #303846;
  font-size: 26px;
  font-weight: 700;
}

.score-card p {
  margin-bottom: 22px;
  color: #6e7885;
  font-size: 16px;
}

.score-card :deep(.el-rate) {
  height: 32px;
}

.score-card :deep(.el-rate__icon) {
  font-size: 25px;
}

.score-card :deep(.el-rate__text) {
  margin-left: 14px;
  font-size: 16px;
}

.emotion-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}

.emotion-item {
  display: grid;
  place-items: center;
  gap: 8px;
  min-height: 126px;
  border: 2px solid #e5e8e9;
  border-radius: 14px;
  background: #fff;
  color: #3f4753;
  font: inherit;
  cursor: pointer;
  transition: .2s;
}

.emotion-item:hover,
.emotion-item.selected {
  border-color: var(--emotion-color);
  background: color-mix(in srgb, var(--emotion-color) 7%, white);
  transform: translateY(-2px);
}

.emotion-icon {
  color: var(--emotion-color);
  font-size: 48px;
  line-height: 1;
}

.detail-card :deep(.el-form-item__label) {
  color: #3f4753;
  font-size: 16px;
}

.select-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 22px;
}

.select-row :deep(.el-select) {
  width: 100%;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

@media (max-width: 720px) {
  .diary-banner { padding: 36px 0; }
  .banner-content, .diary-content { width: min(100% - 28px, 1120px); }
  .emotion-grid { grid-template-columns: repeat(2, 1fr); }
  .select-row { grid-template-columns: 1fr; gap: 0; }
  .diary-card { padding: 20px; }
}
</style>

<style scoped>
.diary-page { min-height: calc(100vh - 74px); background: var(--page-bg); color: var(--text-primary); }
.diary-banner { padding: 44px 24px; background: var(--brand-primary-dark); }
.banner-content, .diary-content { width: min(920px, calc(100% - 40px)); }
.banner-content { gap: 16px; }
.banner-mark { width: 58px; height: 58px; border-radius: 16px; }
.banner-content p { margin: 0 0 7px; color: rgba(255,255,255,.68); font-size: 13px; letter-spacing: .04em; }
.banner-content h1 { font-size: 32px; }
.diary-content { gap: 18px; padding: 24px 0 48px; }
.diary-card { padding: 26px; border: 1px solid var(--border-color); border-radius: var(--radius-lg); box-shadow: var(--shadow-sm); }
.step-label { display: block; margin-bottom: 7px; color: var(--brand-primary); font-size: 12px; font-weight: 800; letter-spacing: .08em; }
.diary-card h2 { margin-bottom: 18px; color: var(--text-primary); font-size: 22px; }
.score-card p { margin-bottom: 20px; color: var(--text-secondary); }
.score-card :deep(.el-rate__text) { color: var(--text-secondary); }
.emotion-grid { gap: 10px; }
.emotion-item { min-height: 96px; border: 1px solid var(--border-color); border-radius: 12px; color: #465653; }
.emotion-item:hover { border-color: #9fc5bc; background: #f6fbf9; transform: translateY(-2px); box-shadow: var(--shadow-sm); }
.emotion-item.selected { border-color: var(--brand-primary); background: var(--brand-primary-soft); color: var(--brand-primary-dark); transform: none; box-shadow: 0 0 0 2px rgba(52,124,114,.1); }
.emotion-icon { font-size: 34px; filter: saturate(.7); }
.detail-card :deep(.el-form-item__label) { color: #465653; font-size: 14px; font-weight: 650; }
.detail-card :deep(.el-textarea__inner) { line-height: 1.65; }
.form-actions { justify-content: flex-end; }
.form-actions :deep(.el-button) { min-width: 112px; }
@media (max-width: 720px) {
  .diary-banner { padding: 32px 0; }
  .banner-content, .diary-content { width: calc(100% - 28px); }
  .banner-content h1 { font-size: 27px; }
  .diary-card { padding: 20px 16px; }
  .emotion-item { min-height: 88px; }
  .form-actions { flex-direction: column-reverse; }
  .form-actions :deep(.el-button) { width: 100%; margin: 0; }
}
</style>
