<template>
  <div class="auth-layout">
    <aside class="brand-section">
      <div class="brand-content">
        <router-link class="brand-lockup" to="/" aria-label="返回校园节律首页">
          <BrandMark tone="light" class="auth-mark" />
          <span><strong>校园节律</strong><small>CampusRhythm</small></span>
        </router-link>
        <div class="brand-message">
          <p class="brand-kicker">大学生压力与作息韧性平台</p>
          <h1>每天一点记录，<br />慢慢找到自己的节奏</h1>
          <div class="rhythm-decoration" aria-hidden="true">
            <i></i><i></i><i></i><i></i><i></i><i></i><i></i>
          </div>
        </div>
        <p class="brand-note">记录 · 梳理 · 调整</p>
      </div>
    </aside>
    <section class="form-section"><router-view /></section>
  </div>
</template>

<script setup>
import BrandMark from '@/components/BrandMark.vue'
</script>

<style scoped>
.auth-layout { display: grid; grid-template-columns: minmax(380px, .9fr) minmax(500px, 1.1fr); width: 100%; min-height: 100vh; background: #fff; }
.brand-section { position: relative; overflow: hidden; background: var(--brand-primary-dark); color: #fff; }
.brand-section::before, .brand-section::after { position: absolute; border: 1px solid rgba(255,255,255,.1); border-radius: 50%; content: ''; }
.brand-section::before { top: -180px; right: -160px; width: 470px; height: 470px; }
.brand-section::after { bottom: -230px; left: -190px; width: 540px; height: 540px; }
.brand-content { position: relative; z-index: 1; display: flex; flex-direction: column; min-height: 100vh; padding: clamp(34px, 6vw, 70px); }
.brand-lockup { display: inline-flex; align-items: center; gap: 14px; width: fit-content; color: #fff; text-decoration: none; }
.auth-mark { width: 52px; height: 52px; border-radius: 15px; }
.brand-lockup span { display: flex; flex-direction: column; }
.brand-lockup strong { font-size: 23px; letter-spacing: .08em; }
.brand-lockup small { margin-top: 5px; color: rgba(255,255,255,.7); font-size: 12px; letter-spacing: .1em; }
.brand-message { margin: auto 0; }
.brand-kicker { margin: 0 0 22px; color: #bcd8d1; font-size: 14px; font-weight: 700; letter-spacing: .08em; }
.brand-message h1 { margin: 0; color: #fff; font-size: clamp(34px, 4vw, 54px); line-height: 1.35; letter-spacing: -.035em; }
.rhythm-decoration { display: flex; align-items: center; gap: 9px; height: 72px; margin-top: 40px; }
.rhythm-decoration i { width: 7px; border-radius: 99px; background: rgba(255,255,255,.72); }
.rhythm-decoration i:nth-child(1), .rhythm-decoration i:nth-child(7) { height: 18px; opacity: .45; }
.rhythm-decoration i:nth-child(2), .rhythm-decoration i:nth-child(6) { height: 36px; opacity: .58; }
.rhythm-decoration i:nth-child(3), .rhythm-decoration i:nth-child(5) { height: 58px; opacity: .78; }
.rhythm-decoration i:nth-child(4) { height: 72px; background: var(--brand-accent); }
.brand-note { margin: 0; color: rgba(255,255,255,.62); font-size: 13px; letter-spacing: .22em; }
.form-section { display: flex; align-items: center; justify-content: center; min-width: 0; padding: 40px clamp(28px, 7vw, 96px); background: #fbfcfb; }
@media (max-width: 860px) {
  .auth-layout { grid-template-columns: 1fr; }
  .brand-content { min-height: auto; padding: 24px; }
  .brand-message, .brand-note { display: none; }
  .form-section { min-height: calc(100vh - 100px); padding: 34px 22px; }
}
</style>
