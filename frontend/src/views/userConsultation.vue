<template>
  <main class="consultation-page">
    <div class="consultation-shell">
      <aside class="consultation-sidebar">
        <section class="assistant-card">
          <div class="assistant-avatar">
            <img :src="robotImage" alt="" class="robot-image" />
          </div>
          <h2>AI助手</h2>
          <p class="online-status"><span class="status-dot"></span>在线服务中</p>
        </section>

        <section class="mood-card">
          <h2>情绪花园</h2>
          <div class="mood-score">
            <strong>{{ currentEmotion.primaryEmotion }}</strong>
            <span>{{ currentEmotion.emotionScore }}</span>
          </div>
          <p class="mood-today">
            <span>今天感觉</span><strong>{{ currentEmotion.isNegative ? '需要关注' : '很不错' }}</strong>
          </p>
          <p class="mood-level">
            <span class="mood-dots" aria-hidden="true">
              <i v-for="index in Math.min(3, (currentEmotion.riskLevel ?? 0) + 1)" :key="index"></i>
            </span>
            {{ ['正常', '关注', '严重', '危机'][currentEmotion.riskLevel] || '正常' }}
          </p>
          <div v-if="currentEmotion.suggestion" class="mood-advice">
            <span class="advice-icon" aria-hidden="true">💝</span>
            <div>
              <strong>给你的小建议</strong>
              <span>{{ currentEmotion.suggestion }}</span>
            </div>
          </div>
          <div v-if="currentEmotion.improvementSuggestions?.length" class="mood-actions">
            <h3>治愈小行动</h3>
            <div v-for="item in currentEmotion.improvementSuggestions" :key="item" class="mood-action-item">
              <span aria-hidden="true">✨</span>
              <span>{{ item }}</span>
            </div>
          </div>
          <div v-if="currentEmotion.riskLevel >= 2" class="mood-reminder">
            <span class="advice-icon" aria-hidden="true">🤗</span>
            <div>
              <strong>{{ currentEmotion.riskLevel === 3 ? '紧急安全支持' : '温馨提醒' }}</strong>
              <span>{{ currentEmotion.riskDescription }}</span>
            </div>
          </div>
        </section>

        <section class="history-card">
          <h2>会话历史</h2>
          <el-scrollbar class="history-scrollbar" always>
            <div v-if="sessionList.length" class="history-list">
              <div
                v-for="session in sessionList"
                :key="session.id"
                class="history-item"
                :class="{ 'is-active': currentSession?.id === session.id }"
                @click="handleSessionClick(session)"
              >
                <div class="history-heading">
                  <strong class="history-title" :title="session.sessionTitle">{{ session.sessionTitle }}</strong>
                  <el-button @click="handleDeleteSession(session.id)" class="history-delete" type="danger" text :icon="Delete" aria-label="删除会话" />
                </div>
                <span class="history-time">{{ session.startedAt }}</span>
                <p class="history-preview">{{ session.lastMessageContent || '暂无消息' }}</p>
                <div class="history-meta">
                  <span><el-icon><ChatRound /></el-icon>{{ session.messageCount ?? 0 }}</span>
                  <span><el-icon><Clock /></el-icon>{{ session.durationMinutes ?? 0 }}分钟</span>
                </div>
              </div>
            </div>
            <p v-else class="history-empty">暂无会话记录</p>
          </el-scrollbar>
        </section>
      </aside>

      <section class="chat-card" aria-label="AI心理咨询">
        <header class="chat-header">
          <div class="chat-heading">
            <span class="heart-badge" aria-hidden="true">
              <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 21.35 10.55 20C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.51L12 21.35Z" />
              </svg>
            </span>
            <div>
              <h2>AI助手</h2>
              <p>您的贴心AI心理健康助手</p>
            </div>
          </div>
          <el-button circle :disabled="isAiTyping" @click="createNewSession" title="创建新会话">
            <el-icon><Plus /></el-icon>
          </el-button>
        </header>

        <div class="chat-messages">
          <div v-if="message.length===0" class="assistant-message">
            <span class="message-avatar" aria-hidden="true">
              <img :src="robotImage" alt="" class="robot-image" />
            </span>
            <div class="message-content">
              <p  class="message-bubble">
                您好！我是小暖，您的AI心理健康助手。很高兴陪伴您，为您提供温暖的心理支持。请告诉我，今天您感觉怎么样？有什么想要分享的吗？
              </p>
              <span class="message-time">刚刚</span>
            </div>
          </div>
          <div
            v-for="msg in message"
            :key="msg.id"
            :class="msg.senderType === 1 ? 'user-message' : 'assistant-message'"
          >
            <span class="message-avatar" :class="{ 'user-avatar': msg.senderType === 1 }" aria-hidden="true">
              <svg v-if="msg.senderType === 1" class="user-avatar-icon" viewBox="0 0 24 24" fill="none">
                <circle cx="9" cy="8" r="3" />
                <path d="M3.5 19c.35-3.4 2.35-5.5 5.5-5.5s5.15 2.1 5.5 5.5" />
                <path d="M15 5.5a3 3 0 0 1 0 5.8M16.5 13.5c2.35.65 3.75 2.5 4 5" />
              </svg>
              <img v-else :src="robotImage" alt="" class="robot-image" />
            </span>
            <div class="message-content">
              <p class="message-bubble">
                <span
                  v-if="msg.senderType == 2 && isAiTyping && !msg.content"
                  class="typing-dots"
                  aria-label="AI正在输入"
                >
                  <i></i><i></i><i></i>
                </span>
                <template v-else>{{ msg.content }}</template>
              </p>
              <span class="message-time">{{ msg.createdAt }}</span>
            </div>
          </div>
        </div>

        <div class="chat-composer">
          <div class="composer-main">
           <el-input
           v-model="userMessage"
           placeholder="请输入您想要分享的内容..."
           type="textarea"
           :rows="3"
           :disabled="isAiTyping"
           @keydown="handlkeyDown"
        </el-input>
            <div class="composer-details">
              <span>按Enter发送，Shift+Enter换行</span>
              <span>{{ userMessage.length }}/500</span>
            </div>
          </div>
          <span class="send-symbol" aria-hidden="true">
            <el-button size="small" :disabled="!userMessage.trim()||userMessage.length > 500" @click="sendMessage">
                <el-icon><Promotion /></el-icon>
            </el-button>
          </span>
        </div>
      </section>
    </div>
  </main>
</template>

<script setup>
import robotImage from '@/assets/robot.png'
import { Promotion, ChatRound, Clock, Delete } from '@element-plus/icons-vue';
import { ref, onMounted } from 'vue'
import { startSession,getSessionList,deleteSession,getSessionDetail,getSessionEmotion } from '@/api/frontend'
import { ElMessage } from 'element-plus'
import { fetchEventSource } from '@microsoft/fetch-event-source';
const handlkeyDown=(e)=>{
  if(e.key==='Enter'){
  }
}
const handleDeleteSession=(sessionId)=>{
    deleteSession(sessionId).then((res)=>{
            ElMessage.success('删除成功')
            getSessionPage()
    })

}
//处理会话点击事件
const handleSessionClick=(session)=>{
    currentSession.value=session
    getSessionDetail(session.id).then((res)=>{
        console.log(res)
        message.value=res.data || []
    })
    const sessionData={
        sessionId:"session_"+session.id,
        status:'ACTIVE',
        sessionTitle:session.sessionTitle
    }
    currentSession.value=sessionData
    localSessionEmotion(session.id)
}
//发送消息
const sendMessage=async()=>{
    if(!userMessage.value.trim()){
        return
    }
    if(isAiTyping.value){
        ElMessage.warning('请稍后再发送')
        return
    }
    const massage=userMessage.value.trim()
    userMessage.value=''
    //如果没有会话或者临时会话，创建新会话
    if(!currentSession.value || currentSession.value.status==='TEMP'){
        startNewSession(massage)
    }else{
        message.value.push({
            id:Date.now(),
            createAt:new Date().toISOString(),
            senderType:1,
            content:massage
        })
        startAiResponse(currentSession.value.sessionId,massage)
    }
}
const startNewSession=(massage)=>{
    const sessionParams={
        initialMessage:massage
    }
    if(currentSession.value.sessionTitle==='新会话'){
        sessionParams.sessionTitle='AI助手-'+(new Date()).toLocaleString()
    }
    else{
        sessionParams.sessionTitle=currentSession.value.sessionTitle
    }
    startSession(sessionParams).then((res)=>{
       console.log(res)
       const sessionData={
        sessionId:res.data.sessionId,
        status:res.data.status,
        sessionTitle:sessionParams.sessionTitle
       }
       //如果是临时会话，更新数据
       if(currentSession.value && currentSession.value.status==='TEMP'){
        Object.assign(currentSession.value,sessionData)
       }
       else{
        currentSession.value=sessionData
       }
       //更新会话列表
       getSessionPage()
       //添加初始用户信息
       message.value.push({
        id:Date.now(),
        createAt:new Date().toISOString(),
        senderType:1,
        content:massage
       })
       //开始流式对话
       startAiResponse(currentSession.value.sessionId,massage)
    })
}
const startAiResponse=(sessionId,userMassage)=>{
    if(isAiTyping.value){
        ElMessage.warning('请稍后再发送')
        return
    }
    isAiTyping.value=true
    const aiMessage={
        id:'ai-'+Date.now()+Math.random().toString(36).substring(2,9),
        createAt:new Date().toISOString(),
        senderType:2,
        content:''
    }
    message.value.push(aiMessage)
    const ctrl = new AbortController()//终止fetch请求
    fetchEventSource('/api/psychological-chat/stream',{
        method:'POST',
        headers:{
            'Content-Type':'application/json',
            token:localStorage.getItem('token'),
            'Accept':'text/event-stream'
        },
        body:JSON.stringify({
            sessionId:sessionId,
            userMessage:userMassage
        }),
        signal:ctrl.signal,
        onopen:(response)=>{
            if(response.headers.get('Content-Type')!=='text/event-stream'){
                ElMessage.error('连接失败')
            }
        },
        onmessage:(event)=>{
            const raw=event.data.trim()
            if(!raw){
                return
            }
            const eventName=event.event
            const aiMessage=message.value[message.value.length-1]
            if(eventName==='done'){
                isAiTyping.value=false
                ctrl.abort()
                localSessionEmotion(currentSession.value.sessionId)
                return
            }
            const payload=JSON.parse(raw)
            const ok=String(payload.code)==='200'
            if(ok && payload.data&&payload.data.content){
                aiMessage.content+=payload.data.content
            }else if(!ok){
                handleError(payload.message || 'AI回复失败')
            }

        },
        onError:(error)=>{
            handleError(error)
            throw error
        },
    })
}
//处理错误
const handleError=(error)=>{
    const aiMessage=message.value[message.value.length-1]
    if(aiMessage){
        aiMessage.content='AI回复失败'
    }
    isAiTyping.value=false
    ElMessage.error('AI回复失败')
}
//新建会话
const createNewSession=()=>{
    if(isAiTyping.value){
        ElMessage.warning('请等待当前回复完成后再新建会话')
        return
    }
    //新建会话对象
    const newSession={
        sessionId:'temp_'+Date.now(),
        status:'TEMP',
        sessionTitle:'新会话'
    }
    message.value=[]
    userMessage.value=''
    currentSession.value=newSession
    currentEmotion.value={...DEFAULT_EMOTION}
}
//获取会话列表
const getSessionPage=()=>{
    getSessionList({
        pageNum:1,
        pageSize:10
    }).then((res)=>{
        sessionList.value=res.data?.records || []
    })
}
onMounted(()=>{
    getSessionPage()
    createNewSession()
})
const localSessionEmotion=(sessionId)=>{
    const id=sessionId.toString().startsWith('session_')?sessionId:'session_'+sessionId
    getSessionEmotion(id).then((res)=>{
        console.log(res)
        currentEmotion.value={...DEFAULT_EMOTION,...(res.data || {})}
    })
}
//定义当前会话对象
const currentSession=ref(null)
const message=ref([])
const sessionList=ref([])
const userMessage=ref('')
const isAiTyping=ref(false)
// 情绪花园
const DEFAULT_EMOTION = {
  primaryEmotion: '中性',
  emotionScore: 50,
  isNegative: false,
  riskLevel: 0,
  suggestion: '情绪状态平稳',
  improvementSuggestions: [],
  riskDescription: null,
  showWarmReminder: false
}
const currentEmotion = ref({...DEFAULT_EMOTION})
</script>

<style scoped>
.consultation-page {
  box-sizing: border-box;
  min-height: calc(100vh - 74px);
  padding: 40px 20px 24px;
  background: #fff;
  color: #2d302f;
}

.consultation-shell {
  display: grid;
  grid-template-columns: 360px minmax(0, 1fr);
  gap: 24px;
  width: 100%;
  max-width: 1354px;
  margin: 0 auto;
}

.consultation-sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-width: 0;
}

.assistant-card,
.history-card,
.chat-card {
  box-sizing: border-box;
  border: 1px solid #f0eee9;
  background: #fff;
  box-shadow: 0 7px 20px rgb(79 61 35 / 6%);
}

.assistant-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 180px;
  padding: 18px 16px;
  border-radius: 19px;
}

.assistant-avatar,
.message-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  flex: none;
  border-radius: 50%;
  background: linear-gradient(135deg, #efa04c, #e49a30);
}

.assistant-avatar {
  width: 68px;
  height: 68px;
  box-shadow: 0 5px 12px rgb(226 150 48 / 16%);
}

.robot-image {
  display: block;
  width: 38px;
  height: auto;
  filter: grayscale(1) invert(1) contrast(3);
  mix-blend-mode: screen;
}

.assistant-card h2 {
  margin: 14px 0 0;
  color: #d69038;
  font-size: 18px;
  font-weight: 700;
  line-height: 1.3;
}

.online-status {
  display: flex;
  align-items: center;
  gap: 7px;
  margin: 12px 0 0;
  color: #2f8c67;
  font-size: 14px;
  font-weight: 600;
  line-height: 1;
}

.status-dot {
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: #329a70;
}

.mood-card {
  box-sizing: border-box;
  min-height: 376px;
  padding: 20px 18px 26px;
  border-radius: 19px;
  background: linear-gradient(130deg, #fffaf0 0%, #fbf5e9 100%);
}

.mood-card h2 {
  margin: 0;
  color: #7b5535;
  font-size: 18px;
  font-weight: 700;
  line-height: 1.4;
}

.mood-score {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  width: 94px;
  height: 94px;
  margin: 23px auto 8px;
  border: 3px solid #fff;
  border-radius: 50%;
  background: linear-gradient(135deg, #f4b6c9, #f9d3ef);
  box-shadow: 0 5px 13px rgb(109 65 65 / 13%);
  color: #fff;
  line-height: 1.2;
}

.mood-score strong {
  font-size: 16px;
}

.mood-score span {
  margin-top: 2px;
  font-size: 15px;
  font-weight: 700;
}

.mood-today {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 22px;
  margin: 0;
  font-size: 16px;
}

.mood-today span {
  color: #89877e;
}

.mood-today strong {
  color: #202020;
  font-size: 18px;
}

.mood-level {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin: 18px 0 20px;
  color: #8e877a;
  font-size: 14px;
  line-height: 1;
}

.mood-dots {
  display: inline-flex;
  gap: 3px;
}

.mood-dots i {
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: #e8a9bd;
}

.mood-dots i:nth-child(2) {
  background: #f5bfd4;
}

.mood-dots i:nth-child(3) {
  background: #d4d5d5;
}

.mood-advice {
  display: flex;
  align-items: center;
  gap: 15px;
  box-sizing: border-box;
  min-height: 80px;
  padding: 12px 18px;
  border-radius: 18px;
  background: #fff;
  box-shadow: 0 8px 20px rgb(78 61 46 / 12%);
}

.advice-icon {
  font-size: 22px;
  line-height: 1;
}

.mood-advice div {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.mood-advice strong {
  color: #74685a;
  font-size: 15px;
}

.mood-advice div span {
  color: #74685a;
  font-size: 14px;
}

.mood-actions {
  display: grid;
  gap: 10px;
  margin-top: 20px;
}

.mood-actions h3 {
  margin: 0 0 4px;
  color: #74685a;
  font-size: 16px;
  text-align: center;
}

.mood-action-item {
  display: flex;
  align-items: center;
  gap: 10px;
  min-height: 50px;
  padding: 0 16px;
  border-radius: 14px;
  background: #fff;
  box-shadow: 0 5px 14px rgb(78 61 46 / 8%);
  color: #74685a;
  font-size: 14px;
}

.mood-reminder {
  display: flex;
  align-items: center;
  gap: 15px;
  min-height: 78px;
  margin-top: 20px;
  padding: 12px 18px;
  border-radius: 18px;
  background: linear-gradient(135deg, #fffbe4, #ffefaa);
  color: #b47725;
}

.mood-reminder div {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.mood-reminder strong {
  font-size: 15px;
}

.mood-reminder div span {
  font-size: 14px;
}

.history-card {
  display: flex;
  flex-direction: column;
  height: 320px;
  padding: 20px 18px;
  border-radius: 19px;
}

.history-card h2 {
  margin: 0;
  color: #272d2b;
  font-size: 17px;
  font-weight: 700;
  line-height: 1.4;
}

.history-scrollbar {
  flex: 1;
  min-height: 0;
  margin-top: 16px;
}

.history-scrollbar :deep(.el-scrollbar__thumb) {
  background: #bed1f2;
}

.history-list {
  display: grid;
  gap: 10px;
  padding-right: 12px;
}

.history-item {
  box-sizing: border-box;
  min-width: 0;
  min-height: 128px;
  padding: 13px;
  border: 1px solid transparent;
  border-radius: 13px;
  cursor: pointer;
}

.history-item.is-active {
  border-color: #dce8ff;
  background: #f7f9ff;
}

.history-heading {
  display: flex;
  align-items: center;
  min-width: 0;
}

.history-delete.el-button.is-disabled {
  width: 24px;
  height: 24px;
  padding: 0;
  color: #d86b6b;
}

.history-title,
.history-time,
.history-preview {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.history-title {
  flex: 1;
  min-width: 0;
  color: #30343b;
  font-size: 15px;
}

.history-time,
.history-meta {
  color: #9da0a4;
  font-size: 13px;
}

.history-preview {
  margin: 6px 0;
  color: #626b76;
  font-size: 14px;
}

.history-meta,
.history-meta span {
  display: flex;
  align-items: center;
  gap: 6px;
}

.history-meta {
  gap: 14px;
}

.history-empty {
  margin: 20px 0 0;
  color: #9da0a4;
  font-size: 16px;
  text-align: center;
}

.chat-card {
  display: flex;
  flex-direction: column;
  min-width: 0;
  height: max(920px, calc(100vh - 122px));
  overflow: hidden;
  border-radius: 22px;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex: none;
  box-sizing: border-box;
  height: 105px;
  padding: 0 27px;
  background: linear-gradient(105deg, #ed9848 0%, #e9a032 100%);
  color: #fff;
}

.chat-heading {
  display: flex;
  align-items: center;
  gap: 18px;
}

.heart-badge {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 54px;
  height: 54px;
  border-radius: 50%;
  background: rgb(255 255 255 / 25%);
  box-shadow: 0 5px 10px rgb(111 67 31 / 13%);
}

.heart-badge svg {
  width: 29px;
  height: 29px;
}

.chat-heading h2 {
  margin: 0;
  color: #fff;
  font-size: 23px;
  font-weight: 700;
  line-height: 1.2;
}

.chat-heading p {
  margin: 6px 0 0;
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  line-height: 1.2;
}

.chat-messages {
  flex: 1;
  min-height: 0;
  padding: 28px 27px;
  overflow-y: auto;
}

.assistant-message,
.user-message {
  display: flex;
  align-items: flex-start;
  gap: 14px;
}

.assistant-message + .assistant-message,
.assistant-message + .user-message,
.user-message + .assistant-message,
.user-message + .user-message {
  margin-top: 18px;
}

.message-avatar {
  width: 36px;
  height: 36px;
}

.message-avatar .robot-image {
  width: 25px;
}

.message-avatar.user-avatar {
  background: #525b68;
  color: #fff;
}

.user-avatar-icon {
  width: 24px;
  height: 24px;
  stroke: currentColor;
  stroke-width: 1.7;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.message-content {
  min-width: 0;
  max-width: 640px;
}

.message-bubble {
  box-sizing: border-box;
  margin: 0;
  padding: 13px 18px;
  border: 1px solid #f3ebe0;
  border-radius: 18px;
  background: #fff;
  box-shadow: 0 5px 17px rgb(139 113 79 / 5%);
  color: #2a2d2c;
  font-size: 17px;
  line-height: 1.55;
  overflow-wrap: anywhere;
  white-space: pre-wrap;
}

.message-time {
  display: block;
  margin-top: 7px;
  color: #a9aaae;
  font-size: 13px;
}

.typing-dots {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  min-height: 12px;
}

.typing-dots i {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #9a9da3;
  animation: typing-dot 1.2s infinite ease-in-out;
}

.typing-dots i:nth-child(2) {
  animation-delay: 0.15s;
}

.typing-dots i:nth-child(3) {
  animation-delay: 0.3s;
}

@keyframes typing-dot {
  0%,
  60%,
  100% {
    transform: translateY(0);
    opacity: 0.45;
  }

  30% {
    transform: translateY(-3px);
    opacity: 1;
  }
}

.chat-composer {
  display: flex;
  align-items: flex-end;
  gap: 14px;
  flex: none;
  box-sizing: border-box;
  height: 148px;
  padding: 22px 27px 20px;
  border-top: 1px solid #f0eee9;
  background: #fff;
}

.composer-main {
  flex: 1;
  min-width: 0;
}

.composer-main textarea {
  display: block;
  box-sizing: border-box;
  width: 100%;
  height: 82px;
  padding: 9px 12px;
  resize: none;
  border: 1px solid #d9dce2;
  border-radius: 4px;
  outline: none;
  background: #fff;
  color: #30343a;
  font: inherit;
  font-size: 16px;
}

.composer-main textarea::placeholder {
  color: #a8abb2;
}

.composer-details {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 4px;
  color: #656971;
  font-size: 14px;
  line-height: 18px;
}

.send-symbol {
  display: flex;
  align-items: center;
  justify-content: center;
  flex: none;
  width: 68px;
  height: 68px;
  margin-bottom: 0;
  border-radius: 18px;
  background: linear-gradient(135deg, #f0a34f, #e89b2d);
  box-shadow: 0 7px 18px rgb(226 155 55 / 20%);
  color: #fff;
}

.send-symbol :deep(.el-button) {
  width: 100%;
  height: 100%;
  padding: 0;
  border: none;
  border-radius: 18px;
  background: transparent;
  color: #fff;
  font-size: 26px;
}

.send-symbol :deep(.el-button:hover) {
  background: rgb(255 255 255 / 15%);
  color: #fff;
}

.send-symbol :deep(.el-button:active) {
  background: rgb(0 0 0 / 10%);
}

.send-symbol :deep(.el-icon) {
  font-size: 26px;
}

.send-symbol :deep(.el-button svg) {
  width: 1em;
  height: 1em;
}

@media (max-width: 1050px) {
  .consultation-shell {
    grid-template-columns: 300px minmax(0, 1fr);
  }
}

@media (max-width: 800px) {
  .consultation-page {
    padding: 24px 16px;
  }

  .consultation-shell {
    grid-template-columns: 1fr;
  }

  .consultation-sidebar {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .mood-card {
    grid-row: span 2;
  }

  .history-card {
    height: 176px;
  }

  .chat-card {
    height: 720px;
  }
}

@media (max-width: 560px) {
  .consultation-sidebar {
    display: flex;
  }

  .history-card {
    height: 220px;
  }

  .chat-header {
    height: 90px;
    padding: 0 18px;
  }

  .chat-heading {
    gap: 12px;
  }

  .chat-heading h2 {
    font-size: 19px;
  }

  .chat-heading p {
    font-size: 12px;
  }

  .heart-badge {
    width: 46px;
    height: 46px;
  }

  .chat-messages {
    padding: 20px 16px;
  }

  .message-bubble {
    font-size: 15px;
  }

  .chat-composer {
    gap: 10px;
    padding: 18px 15px;
  }

  .send-symbol {
    width: 52px;
    height: 52px;
    border-radius: 14px;
  }

  .send-symbol :deep(.el-button) {
    border-radius: 14px;
    font-size: 22px;
  }

  .send-symbol :deep(.el-icon) {
    font-size: 22px;
  }

  .composer-details {
    font-size: 11px;
  }
}
</style>
