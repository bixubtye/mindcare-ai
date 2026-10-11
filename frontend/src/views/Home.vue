<template>
  <main class="home-page">
    <div class="home-content">
      <div class="home-copy">
        <h1 class="home-title">
          <span>一次温暖的对话</span>
          <span>化孤独为慰<span class="title-accent">藉</span></span>
        </h1>
        <p class="home-description">
          每个深夜，每个焦虑的时刻，我们都在这里。不必独自承受，让心与心的连接温暖您的每一天
        </p>
        <div class="home-actions">
          <el-button class="home-action home-action-primary" @click="router.push('/consultation')">
            开始倾诉，获得陪伴
          </el-button>
          <el-button class="home-action home-action-secondary" @click="router.push('/emotion-dairy')">
            记录心情，释放情感
          </el-button>
        </div>
      </div>

      <div class="robot-circle" aria-hidden="true">
        <img :src="robotImage" alt="" class="robot-image" />
      </div>
    </div>
  </main>
</template>

<script setup>
import robotImage from '@/assets/robot.png'
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<style scoped>
.home-page {
  display: flex;
  align-items: center;
  min-height: calc(100vh - 134px);
  background: linear-gradient(105deg, #5f998a 0%, #589382 52%, #528a7a 100%);
  color: #fff;
}

.home-content {
  display: grid;
  grid-template-columns: minmax(0, 568px) 300px;
  align-items: center;
  gap: 32px;
  width: min(900px, calc(100% - 48px));
  margin: 0 auto;
}

.home-title {
  margin: 0 0 20px;
  color: #fff;
  font-size: 48px;
  font-weight: 700;
  line-height: 1.55;
  letter-spacing: 0;
}

.home-title > span {
  display: block;
  white-space: nowrap;
}

.title-accent {
  color: #f7d56c;
}

.home-description {
  max-width: 568px;
  margin: 0;
  color: #fff;
  font-size: 18px;
  font-weight: 500;
  line-height: 1.5;
}

.home-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  margin-top: 32px;
}

.home-action {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  min-width: 186px;
  height: 46px;
  padding: 0 18px;
  border: 1px solid #fff;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  line-height: 1;
  text-decoration: none;
  white-space: nowrap;
}

.home-actions :deep(.el-button + .el-button) {
  margin-left: 0;
}

.home-action-primary {
  background: #fff;
  color: #354039;
}

.home-action-secondary {
  background: transparent;
  color: #fff;
}

.robot-circle {
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  width: 300px;
  height: 300px;
  border: 3px solid rgb(255 255 255 / 24%);
  border-radius: 50%;
  background: rgb(255 255 255 / 4%);
  box-shadow: 0 20px 32px rgb(19 73 59 / 10%);
}

.robot-image {
  display: block;
  width: 190px;
  height: auto;
  filter: grayscale(1) invert(1) contrast(3);
  mix-blend-mode: screen;
}

@media (max-width: 800px) {
  .home-page {
    min-height: 0;
    padding: 80px 0;
  }

  .home-content {
    grid-template-columns: 1fr;
    justify-items: center;
    gap: 56px;
  }

  .home-copy {
    width: 100%;
  }

  .home-title {
    font-size: clamp(30px, 7vw, 48px);
  }

  .home-description {
    font-size: 16px;
  }
}

@media (max-width: 480px) {
  .home-page {
    padding: 64px 0;
  }

  .home-title {
    font-size: clamp(26px, 7vw, 34px);
  }

  .home-actions {
    gap: 12px;
  }

  .home-action {
    width: 100%;
  }

  .robot-circle {
    width: 240px;
    height: 240px;
  }

  .robot-image {
    width: 150px;
  }
}
</style>
