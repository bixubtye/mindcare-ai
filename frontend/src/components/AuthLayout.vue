<template>
    <div class="auth-layout">
        <div class="left-section">
            <div class="content">
                <h2 class="title">AI心理助手</h2>
                <p class="text">每一个深夜，我们都在这里</p>
                <div class="image">
                    <img :src="iconurl" alt="AI心理助手">
                </div>
            </div>
        </div>
        <div class="right-section">
            <router-view></router-view>
        </div>
    </div>
</template>
<script setup>
import iconurl from '@/assets/robot.png'
</script>

<style scoped>
.auth-layout {
    display: flex;
    width: 100%;
    height: 100vh;
    overflow: hidden;
}

/* 左侧品牌区：纯绿色背景 */
.left-section {
    position: relative;
    flex: 1;
    display: flex;
    justify-content: center;
    background-color: #22c55e;
    overflow: hidden;
}

/* 浅色装饰圆点，保持纯绿底色的同时增加层次感 */
.left-section::before,
.left-section::after {
    content: '';
    position: absolute;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.08);
}

.left-section::before {
    width: 320px;
    height: 320px;
    top: -120px;
    left: -120px;
}

.left-section::after {
    width: 260px;
    height: 260px;
    bottom: -100px;
    right: -80px;
}

.content {
    position: relative;
    z-index: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 100%;
    padding: 70px 32px 60px;
    box-sizing: border-box;
}

/* 标题居中靠上 */
.title {
    margin: 0;
    font-size: 34px;
    font-weight: 600;
    letter-spacing: 4px;
    color: #fff;
}

/* 小字副标题排列在标题下方 */
.text {
    margin: 14px 0 0;
    font-size: 15px;
    letter-spacing: 2px;
    color: rgba(255, 255, 255, 0.9);
}

/* 图片居中显示，圆形边框 */
.image {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 180px;
    height: 180px;
    margin: auto 0;
    border-radius: 50%;
    border: 6px solid rgba(255, 255, 255, 0.9);
    background-color: #fff;
    box-shadow: 0 16px 40px rgba(0, 0, 0, 0.18);
    overflow: hidden;
    box-sizing: border-box;
}

.image img {
    width: 72%;
    height: 72%;
    object-fit: contain;
}

/* 右侧表单区 */
.right-section {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 40px;
    background-color: #fff;
    box-sizing: border-box;
}

/* 移动端上下堆叠 */
@media (max-width: 768px) {
    .auth-layout {
        flex-direction: column;
        overflow-y: auto;
    }

    .left-section {
        flex: none;
    }

    .content {
        padding: 36px 20px 30px;
    }

    .title {
        font-size: 24px;
        letter-spacing: 2px;
    }

    .text {
        margin-top: 10px;
        font-size: 13px;
    }

    .image {
        width: 110px;
        height: 110px;
        margin: 24px 0 0;
        border-width: 4px;
    }

    .right-section {
        padding: 24px 20px;
    }
}
</style>
