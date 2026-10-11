<template>
  <main class="knowledge-page">
    <header class="knowledge-hero">
      <div class="hero-title">
        <el-icon><Reading /></el-icon>
        <h1>心理健康知识库</h1>
      </div>
    </header>

    <div class="knowledge-content">
      <aside class="recommend-card" aria-labelledby="recommend-title">
        <h2 id="recommend-title">推荐阅读</h2>
        <ul class="recommend-list">
          <li v-for="article in recommendedArticles" :key="article.id" @click="goToArticle(article.id)">
            <h3 :title="article.title">{{ article.title }}</h3>
            <div class="read-count">
              <el-icon><Histogram /></el-icon>
              <span>阅读量：{{ article.readCount ?? 0 }}</span>
            </div>
          </li>
        </ul>
      </aside>

      <section class="article-section">
        <div class="article-list">
          <article v-for="article in articles" :key="article.id" class="article-card" @click="goToArticle(article.id)">
            <img
              v-if="article.coverImage"
              :src="getCoverUrl(article.coverImage)"
              :alt="article.title"
              class="article-cover"
            />
            <div v-else class="article-cover article-cover-placeholder">
              <el-icon><Reading /></el-icon>
            </div>

            <div class="article-info">
              <div class="article-heading">
                <h2>{{ article.title }}</h2>
                <span>{{ article.categoryName }}</span>
              </div>
              <p><el-icon><UserFilled /></el-icon>{{ article.authorName }}</p>
              <p><el-icon><Calendar /></el-icon>{{ article.publishedAt?.slice(0, 10) }}</p>
              <p class="article-views">
                <el-icon><Monitor /></el-icon>观看人数：{{ article.readCount ?? 0 }}
              </p>
            </div>
          </article>
        </div>

        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.size"
          class="article-pagination"
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @current-change="getArticleList"
          @size-change="handleSizeChange"
        />
      </section>
    </div>
  </main>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { Calendar, Histogram, Monitor, Reading, UserFilled } from '@element-plus/icons-vue'
import { getKnowledgeList } from '@/api/frontend'
import { fileBaseURL } from '@/config'

const recommendedArticles = ref([])
const articles = ref([])
const pagination = ref({
  currentPage: 1,
  size: 10,
  total: 0,
})

import { useRouter } from 'vue-router'

const router = useRouter()

const goToArticle = (id) => {
 router.push({ path: '/knowledge/article/' + id })
}

const getArticleList = () => {
  const params = {
    sortField: 'publishedAt',
    sortDirection: 'desc',
    ...pagination.value,
  }

  getKnowledgeList(params).then((res) => {
    articles.value = res.data?.records ?? []
    pagination.value.total = res.data?.total ?? 0
  })
}

const handleSizeChange = () => {
  pagination.value.currentPage = 1
  getArticleList()
}

const getCoverUrl = (cover) => cover.startsWith('http') ? cover : fileBaseURL + cover

onMounted(() => {
  const params = {
    sortField: 'readCount',
    sortDirection: 'desc',
    currentPage: 1,
    size: 5,
  }

  getKnowledgeList(params).then((res) => {
    recommendedArticles.value = res.data?.records ?? []
  })

  getArticleList()
})
</script>

<style scoped>
.knowledge-page {
  min-height: calc(100vh - 134px);
  background: #f6fafb;
  color: #20252b;
}

.knowledge-hero {
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
  width: 58px;
  height: 58px;
  color: #fff;
  font-size: 58px;
}

.hero-title h1 {
  margin: 0;
  color: #fff;
  font-size: 36px;
  font-weight: 700;
  line-height: 1;
  letter-spacing: 0;
}

.knowledge-content {
  display: grid;
  grid-template-columns: 350px minmax(0, 1fr);
  align-items: start;
  gap: 22px;
  box-sizing: border-box;
  width: min(1328px, calc(100% - 40px));
  margin: 0 auto;
  padding: 22px 0 48px;
}

.recommend-card {
  box-sizing: border-box;
  width: 350px;
  padding: 20px 18px 22px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 4px 16px rgb(24 39 56 / 10%);
}

.recommend-card h2 {
  margin: 0 0 12px;
  color: #303640;
  font-size: 18px;
  font-weight: 700;
  line-height: 1.4;
}

.recommend-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.recommend-list li {
  padding-left: 12px;
  border-left: 4px solid #efa51e;
}

.recommend-list h3 {
  overflow: hidden;
  margin: 0 0 12px;
  color: #171a1f;
  font-size: 18px;
  font-weight: 700;
  line-height: 1.35;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.read-count {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #737b85;
  font-size: 14px;
  line-height: 1;
}

.read-count .el-icon {
  font-size: 15px;
}

.article-section {
  min-width: 0;
}

.article-list {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.article-card {
  display: grid;
  grid-template-columns: 272px minmax(0, 1fr);
  gap: 22px;
  box-sizing: border-box;
  min-height: 204px;
  padding: 18px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 4px 16px rgb(24 39 56 / 10%);
}

.article-cover {
  width: 272px;
  height: 168px;
  object-fit: cover;
}

.article-cover-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff8d9;
  color: #e4c969;
}

.article-cover-placeholder .el-icon {
  font-size: 52px;
}

.article-info {
  min-width: 0;
  color: #161a1f;
}

.article-heading {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
  margin-bottom: 12px;
}

.article-heading h2 {
  overflow: hidden;
  margin: 0;
  color: #161a1f;
  font-size: 20px;
  font-weight: 700;
  line-height: 1.35;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.article-heading span {
  flex-shrink: 0;
  padding: 4px 10px;
  border-radius: 5px;
  background: #eef6ff;
  color: #71a9df;
  font-size: 13px;
  line-height: 1.2;
}

.article-info p {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 8px 0;
  font-size: 16px;
  line-height: 1.25;
}

.article-info p .el-icon {
  font-size: 17px;
}

.article-info .article-views {
  margin-top: 24px;
}

.article-pagination {
  justify-content: center;
  margin-top: 32px;
}

@media (max-width: 1100px) {
  .knowledge-content {
    grid-template-columns: 1fr;
  }

  .recommend-card {
    width: 100%;
  }
}

@media (max-width: 800px) {
  .knowledge-hero {
    height: 112px;
  }

  .hero-title {
    gap: 14px;
    padding: 0 24px;
  }

  .hero-title .el-icon {
    width: 44px;
    height: 44px;
    font-size: 44px;
  }

  .hero-title h1 {
    font-size: 28px;
  }

  .knowledge-content {
    width: calc(100% - 32px);
  }

  .article-card {
    grid-template-columns: 1fr;
  }

  .article-cover {
    width: 100%;
    height: auto;
    aspect-ratio: 272 / 168;
  }

  .article-pagination {
    overflow-x: auto;
    justify-content: flex-start;
  }
}
</style>
