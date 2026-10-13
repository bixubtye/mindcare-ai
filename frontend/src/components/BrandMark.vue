<template>
  <span class="brand-mark" :class="[`brand-mark--${tone}`]" role="img" :aria-label="label">
    <svg viewBox="0 0 64 64" aria-hidden="true">
      <path class="mark-sun" d="M21 31a11 11 0 0 1 22 0" />
      <path class="mark-ray" d="M32 10v7M15.5 17.2l5 5M48.5 17.2l-5 5M9 31h7M48 31h7" />
      <path class="mark-wave" d="M10 38c7-4.8 14-4.8 21 0s14 4.8 23 0" />
      <path class="mark-wave mark-wave--soft" d="M13 47c6-3.8 12-3.8 18 0s12 3.8 20 0" />
    </svg>
  </span>
</template>

<script setup>
defineProps({
  tone: { type: String, default: 'brand' },
  label: { type: String, default: '校园节律品牌标志' }
})
</script>

<style scoped>
.brand-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  flex: none;
  border-radius: 14px;
  background: var(--brand-primary-soft);
  color: var(--brand-primary);
}
.brand-mark svg { width: 78%; height: 78%; overflow: visible; }
.mark-sun, .mark-ray, .mark-wave { fill: none; stroke: currentColor; stroke-width: 3.2; stroke-linecap: round; stroke-linejoin: round; }
.mark-ray { stroke-width: 2.7; }
.mark-wave--soft { opacity: 0.58; }
.brand-mark--light { background: rgba(255, 255, 255, 0.14); color: #fff; }
.brand-mark--plain { background: transparent; }
</style>
