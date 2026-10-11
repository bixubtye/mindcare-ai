<template>
  <main class="article-detail-page">
    <header class="detail-hero">
      <div class="hero-title">
        <el-icon><Reading /></el-icon>
        <h1>知识文章详情</h1>
      </div>
    </header>

    <div class="detail-content">
      <el-skeleton v-if="loading" animated :rows="10" />

      <template v-else-if="article">
        <el-card class="article-card" shadow="never">
          <h2 class="section-title">文章信息</h2>

          <div class="article-meta">
            <el-tag effect="light">{{ article.categoryName || '未分类' }}</el-tag>
            <span>
              <el-icon><Calendar /></el-icon>
              {{ formatDate(article.publishedAt) }}
            </span>
          </div>

          <h1 class="article-title">{{ article.title }}</h1>
          <p v-if="article.summary" class="article-summary">{{ article.summary }}</p>

          <div class="article-extra">
            <span>
              <el-icon><UserFilled /></el-icon>
              {{ article.authorName || '匿名作者' }}
            </span>
            <span>
              <el-icon><Monitor /></el-icon>
              {{ article.readCount ?? 0 }} 次阅读
            </span>
          </div>
        </el-card>

        <el-card class="content-card" shadow="never">
          <h2 class="section-title">正文内容</h2>
          <div class="article-body" v-html="article.content"></div>
        </el-card>
      </template>

      <el-empty v-else description="文章不存在或已下线" />
    </div>
  </main>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { Calendar, Monitor, Reading, UserFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'
import { getKnowledgeDetail } from '@/api/frontend'

const route = useRoute()
const article = ref(null)
const loading = ref(true)

const formatDate = (date) => date?.slice(0, 10) || '暂无日期'

onMounted(async () => {
  try {
    const res = await getKnowledgeDetail(route.params.id)
    article.value = res.data
  } catch {
    ElMessage.error('获取文章详情失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.article-detail-page {
  min-height: calc(100vh - 134px);
  background: #f6fafb;
  color: #20252b;
}

.detail-hero {
  display: flex;
  align-items: center;
  height: 140px;
  background: linear-gradient(105deg, #f0aa26 0%, #c68579 51%, #7a4ee8 100%);
}

.hero-title {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 0 56px;
}

.hero-title .el-icon {
  color: #fff;
  font-size: 58px;
}

.hero-title h1 {
  margin: 0;
  color: #fff;
  font-size: 36px;
  line-height: 1;
}

.detail-content {
  box-sizing: border-box;
  width: min(1120px, calc(100% - 40px));
  margin: 0 auto;
  padding: 24px 0 48px;
}

.article-card,
.content-card {
  border: 0;
  border-radius: 10px;
  box-shadow: 0 4px 16px rgb(24 39 56 / 8%);
}

.content-card {
  margin-top: 22px;
}

.section-title {
  margin: 0 0 22px;
  font-size: 22px;
  line-height: 1.4;
}

.article-meta,
.article-extra,
.article-meta span,
.article-extra span {
  display: flex;
  align-items: center;
}

.article-meta {
  gap: 22px;
  margin-bottom: 28px;
}

.article-meta span,
.article-extra span {
  gap: 7px;
}

.article-title {
  margin: 0 0 12px;
  font-size: 28px;
  line-height: 1.4;
}

.article-summary {
  margin: 0;
  padding: 12px 16px;
  border-left: 4px solid #8fd54a;
  background: #f3faeb;
  line-height: 1.8;
}

.article-extra {
  gap: 28px;
  margin-top: 22px;
  color: #4f5964;
}

.article-body {
  color: #303640;
  font-size: 16px;
  line-height: 1.9;
  overflow-wrap: anywhere;
}

.article-body :deep(h1),
.article-body :deep(h2),
.article-body :deep(h3),
.article-body :deep(h4) {
  margin: 24px 0 10px;
  color: #20252b;
  line-height: 1.45;
}

.article-body :deep(p) {
  margin: 0 0 14px;
}

.article-body :deep(img) {
  max-width: 100%;
  height: auto;
}

@media (max-width: 800px) {
  .detail-hero {
    height: 112px;
  }

  .hero-title {
    gap: 14px;
    padding: 0 24px;
  }

  .hero-title .el-icon {
    font-size: 44px;
  }

  .hero-title h1 {
    font-size: 28px;
  }

  .detail-content {
    width: calc(100% - 32px);
  }

  .article-title {
    font-size: 24px;
  }

  .article-meta,
  .article-extra {
    align-items: flex-start;
    flex-direction: column;
    gap: 12px;
  }
}
</style>
