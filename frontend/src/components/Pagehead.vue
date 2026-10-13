<template>
    <div class="page-head">
       <p class="page-title">{{ props.title }}</p>
       <div class="action-btn">
        <slot name="buttons"></slot>
       </div>
    </div>
</template>
<script setup>
const props = defineProps({
    title: {
        type: String,
        default: '页面标题'
    }
})
</script>
<style lang="scss" scoped>
.page-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  margin-bottom: 16px;
  border-bottom: 1px solid var(--border-color);
 .page-title {
  font-size: 24px;
  color: var(--text-primary);
  margin: 0;
  font-weight: 700;
 }
}
</style>
