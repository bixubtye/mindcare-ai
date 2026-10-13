<template>
  <main class="home-page">
    <div class="home-shell">
      <section class="home-copy">
        <span class="eyebrow">CampusRhythm · 为大学生活设计</span>
        <h1>看见压力，也看见自己的恢复节奏</h1>
        <p class="home-description">用90秒记录睡眠、压力与情绪，在一次次复盘中找到更适合自己的调整方式。</p>
        <div class="home-actions">
          <el-button type="primary" size="large" @click="goTo('/consultation')">开始今日复盘</el-button>
          <el-button size="large" @click="goTo('/emotion-dairy')">完成状态打卡</el-button>
        </div>
        <ul class="capability-list" aria-label="平台能力">
          <li><span></span>记录当下状态</li>
          <li><span></span>梳理压力来源</li>
          <li><span></span>找到调整节奏</li>
        </ul>
      </section>

      <section class="rhythm-visual" aria-label="校园节律状态示意">
        <div class="visual-orbit orbit-one"></div>
        <div class="visual-orbit orbit-two"></div>
        <BrandMark class="hero-mark" />
        <div class="rhythm-card rhythm-card--sleep">
          <span class="card-label">昨夜睡眠</span>
          <strong>先记录，再调整</strong>
          <div class="mini-bars" aria-hidden="true"><i></i><i></i><i></i><i></i><i></i><i></i></div>
        </div>
        <div class="rhythm-card rhythm-card--pause">
          <span class="pulse-dot"></span>
          <div><span class="card-label">此刻状态</span><strong>给自己一点缓冲</strong></div>
        </div>
        <svg class="rhythm-line" viewBox="0 0 420 190" aria-hidden="true">
          <path d="M8 119c37 0 41-58 81-58s45 91 88 91 47-120 94-120 50 87 141 87" />
          <path class="line-soft" d="M8 145c45 0 55-27 91-27s51 34 93 34 58-53 95-53 56 42 125 42" />
        </svg>
      </section>
    </div>
  </main>
</template>

<script setup>
import { useRouter } from 'vue-router'
import BrandMark from '@/components/BrandMark.vue'

const router = useRouter()
const goTo = (path) => router.push(path)
</script>

<style scoped>
.home-page { position: relative; min-height: calc(100vh - 158px); overflow: hidden; background: radial-gradient(circle at 83% 23%, rgba(232, 185, 104, 0.16), transparent 28%), var(--page-bg); }
.home-page::before { position: absolute; top: 0; bottom: 0; left: 52%; width: 1px; background: linear-gradient(transparent, rgba(52, 124, 114, 0.09), transparent); content: ''; }
.home-shell { position: relative; display: grid; grid-template-columns: minmax(0, 1.02fr) minmax(380px, 0.98fr); align-items: center; gap: clamp(48px, 8vw, 110px); width: min(1180px, calc(100% - 48px)); min-height: calc(100vh - 158px); margin: 0 auto; padding: 72px 0; }
.eyebrow { display: inline-flex; padding: 8px 13px; border: 1px solid #cfe1dc; border-radius: 999px; background: rgba(255,255,255,.72); color: var(--brand-primary-dark); font-size: 13px; font-weight: 700; letter-spacing: 0.04em; }
.home-copy h1 { max-width: 640px; margin: 24px 0 20px; color: var(--text-primary); font-size: clamp(42px, 5vw, 66px); font-weight: 750; line-height: 1.13; letter-spacing: -0.045em; }
.home-description { max-width: 610px; margin: 0; color: var(--text-secondary); font-size: 18px; line-height: 1.8; }
.home-actions { display: flex; flex-wrap: wrap; gap: 14px; margin-top: 34px; }
.home-actions :deep(.el-button) { min-width: 166px; height: 50px; padding: 0 24px; border-radius: 12px; font-size: 16px; }
.home-actions :deep(.el-button + .el-button) { margin-left: 0; }
.capability-list { display: flex; flex-wrap: wrap; gap: 12px 22px; margin: 30px 0 0; padding: 0; color: #50615e; font-size: 14px; list-style: none; }
.capability-list li { display: flex; align-items: center; gap: 8px; }
.capability-list li span { width: 7px; height: 7px; border-radius: 50%; background: var(--brand-accent); box-shadow: 0 0 0 5px rgba(232,185,104,.14); }
.rhythm-visual { position: relative; min-height: 480px; border: 1px solid rgba(197, 218, 211, 0.86); border-radius: 38% 62% 42% 58% / 52% 40% 60% 48%; background: linear-gradient(145deg, rgba(234,245,241,.96), rgba(255,255,255,.88)); box-shadow: var(--shadow-md); isolation: isolate; }
.visual-orbit { position: absolute; border: 1px solid rgba(52,124,114,.14); border-radius: 50%; }
.orbit-one { inset: 42px 50px 74px 36px; transform: rotate(16deg); }
.orbit-two { inset: 90px 90px 46px 78px; transform: rotate(-18deg); }
.hero-mark { position: absolute; top: 64px; left: 62px; width: 92px; height: 92px; border-radius: 28px; box-shadow: 0 16px 30px rgba(52,124,114,.14); }
.rhythm-card { position: absolute; z-index: 2; padding: 18px; border: 1px solid rgba(221,231,227,.95); border-radius: 16px; background: rgba(255,255,255,.94); box-shadow: var(--shadow-sm); }
.rhythm-card strong, .rhythm-card span { display: block; }
.rhythm-card strong { margin-top: 6px; color: var(--text-primary); font-size: 15px; }
.card-label { color: var(--text-muted); font-size: 12px; }
.rhythm-card--sleep { top: 78px; right: 32px; width: 192px; }
.rhythm-card--pause { right: 65px; bottom: 58px; display: flex; align-items: center; gap: 13px; min-width: 215px; }
.pulse-dot { width: 13px; height: 13px; flex: none; border: 3px solid #c6dfd8; border-radius: 50%; background: var(--brand-primary); box-shadow: 0 0 0 6px var(--brand-primary-soft); }
.mini-bars { display: flex; align-items: flex-end; gap: 5px; height: 38px; margin-top: 15px; }
.mini-bars i { width: 9px; border-radius: 6px 6px 2px 2px; background: #9cc8bd; }
.mini-bars i:nth-child(1) { height: 15px; }.mini-bars i:nth-child(2) { height: 27px; }.mini-bars i:nth-child(3) { height: 20px; }.mini-bars i:nth-child(4) { height: 34px; background: var(--brand-accent); }.mini-bars i:nth-child(5) { height: 25px; }.mini-bars i:nth-child(6) { height: 31px; }
.rhythm-line { position: absolute; top: 185px; left: 5%; z-index: 1; width: 90%; overflow: visible; }
.rhythm-line path { fill: none; stroke: var(--brand-primary); stroke-width: 4; stroke-linecap: round; }
.rhythm-line .line-soft { stroke: #e3b868; stroke-width: 2.5; opacity: .6; }
@media (max-width: 900px) {
  .home-page::before { display: none; }
  .home-shell { grid-template-columns: 1fr; min-height: auto; padding: 58px 0 68px; }
  .home-copy { text-align: center; }
  .home-copy h1, .home-description { margin-right: auto; margin-left: auto; }
  .home-actions, .capability-list { justify-content: center; }
  .rhythm-visual { width: min(100%, 560px); min-height: 430px; margin: 0 auto; }
}
@media (max-width: 560px) {
  .home-shell { width: calc(100% - 32px); padding-top: 42px; }
  .home-copy h1 { margin-top: 20px; font-size: clamp(36px, 11vw, 48px); }
  .home-description { font-size: 16px; }
  .home-actions { flex-direction: column; }
  .home-actions :deep(.el-button) { width: 100%; margin: 0; }
  .capability-list { align-items: flex-start; flex-direction: column; width: fit-content; margin-right: auto; margin-left: auto; }
  .rhythm-visual { min-height: 350px; border-radius: 28px; }
  .hero-mark { top: 34px; left: 30px; width: 72px; height: 72px; }
  .rhythm-card--sleep { top: 38px; right: 18px; width: 175px; }
  .rhythm-card--pause { right: 24px; bottom: 30px; min-width: 205px; }
  .rhythm-line { top: 145px; }
}
</style>
