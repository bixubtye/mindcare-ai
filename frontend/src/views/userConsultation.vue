<template>
  <main class="consultation-page">
    <div class="consultation-shell">
      <aside class="consultation-sidebar">
        <section class="assistant-card">
          <div class="assistant-avatar">
            <BrandMark />
          </div>
          <h2>节律教练</h2>
          <p class="online-status"><span class="status-dot"></span>可以开始复盘</p>
        </section>

        <section class="mood-card">
          <h2>今日状态</h2>
          <div class="mood-score">
            <strong>{{ currentEmotion.primaryEmotion }}</strong>
            <span>{{ currentEmotion.emotionScore }}</span>
          </div>
          <p class="mood-today">
            <span>当前感受</span><strong>{{ currentEmotion.primaryEmotion }}</strong>
          </p>
          <p class="mood-level">
            <span class="mood-dots" aria-hidden="true">
              <i v-for="index in Math.min(3, (currentEmotion.riskLevel ?? 0) + 1)" :key="index"></i>
            </span>
            {{ ['状态稳定', '有些波动', '需要支持', '立即求助'][currentEmotion.riskLevel] || '状态稳定' }}
          </p>
          <div v-if="currentEmotion.suggestion" class="mood-advice">
            <span class="advice-icon" aria-hidden="true">○</span>
            <div>
              <strong>今日建议</strong>
              <span>{{ currentEmotion.suggestion }}</span>
            </div>
          </div>
          <div v-if="currentEmotion.improvementSuggestions?.length" class="mood-actions">
            <h3>可以试试</h3>
            <div v-for="item in currentEmotion.improvementSuggestions" :key="item" class="mood-action-item">
              <span class="action-dot" aria-hidden="true"></span>
              <span>{{ item }}</span>
            </div>
          </div>
          <div v-if="currentEmotion.riskLevel >= 2" class="mood-reminder">
            <span class="advice-icon" aria-hidden="true">!</span>
            <div>
              <strong>{{ currentEmotion.riskLevel === 3 ? '立即寻求帮助' : '需要额外支持' }}</strong>
              <span>{{ currentEmotion.riskDescription }}</span>
            </div>
          </div>
        </section>

        <section class="history-card">
          <h2>复盘记录</h2>
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
                  <el-button @click="handleDeleteSession(session.id)" class="history-delete" type="danger" text :icon="Delete" aria-label="删除复盘记录" />
                </div>
                <span class="history-time">{{ session.startedAt }}</span>
                <p class="history-preview">{{ session.lastMessageContent || '暂无消息' }}</p>
                <div class="history-meta">
                  <span><el-icon><ChatRound /></el-icon>{{ session.messageCount ?? 0 }}</span>
                  <span><el-icon><Clock /></el-icon>{{ session.durationMinutes ?? 0 }}分钟</span>
                </div>
              </div>
            </div>
            <p v-else class="history-empty">暂无复盘记录</p>
          </el-scrollbar>
        </section>
      </aside>

      <section class="chat-card" aria-label="压力事件复盘">
        <header class="chat-header">
          <div class="chat-heading">
            <BrandMark tone="light" class="heart-badge" />
            <div>
              <h2>节律教练</h2>
              <p>陪你梳理压力事件与当下状态</p>
            </div>
          </div>
          <el-button circle :disabled="isAiTyping" @click="createNewSession" title="新建复盘" aria-label="新建复盘">
            <el-icon><Plus /></el-icon>
          </el-button>
        </header>

        <div class="chat-messages">
          <div v-if="message.length===0" class="assistant-message">
            <span class="message-avatar" aria-hidden="true">
              <BrandMark />
            </span>
            <div class="message-content">
              <p  class="message-bubble">
                你好，我是节律教练。可以从最近最占据你注意力的一件事说起，我会陪你把压力来源和可以尝试的小调整理清楚。
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
              <BrandMark v-else />
            </span>
            <div class="message-content">
              <p class="message-bubble">
                <span
                  v-if="msg.senderType == 2 && isAiTyping && !msg.content"
                  class="typing-dots"
                  aria-label="节律教练正在输入"
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
           placeholder="可以从最近最占据你注意力的一件事说起…"
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
import { Promotion, ChatRound, Clock, Delete } from '@element-plus/icons-vue';
import { ref, onMounted } from 'vue'
import { startSession,getSessionList,deleteSession,getSessionDetail,getSessionEmotion } from '@/api/frontend'
import { ElMessage } from 'element-plus'
import { fetchEventSource } from '@microsoft/fetch-event-source';
import BrandMark from '@/components/BrandMark.vue'
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
        sessionParams.sessionTitle='今日复盘-'+(new Date()).toLocaleString()
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
                handleError(payload.message || '回复失败')
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
        aiMessage.content='回复失败，请稍后再试'
    }
    isAiTyping.value=false
    ElMessage.error('回复失败')
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
// 当前状态
const DEFAULT_EMOTION = {
  primaryEmotion: '中性',
  emotionScore: 50,
  isNegative: false,
  riskLevel: 0,
  suggestion: '当前状态相对稳定',
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

<style scoped>
.consultation-page { min-height: calc(100vh - 74px); padding: 28px 20px 34px; background: var(--page-bg); color: var(--text-primary); }
.consultation-shell { grid-template-columns: 310px minmax(0, 1fr); gap: 20px; max-width: 1280px; }
.assistant-card, .mood-card, .history-card, .chat-card { border-color: var(--border-color); background: #fff; box-shadow: var(--shadow-sm); }
.assistant-card { height: 156px; padding: 17px 16px; border-radius: var(--radius-lg); }
.assistant-avatar { width: 58px; height: 58px; background: transparent; box-shadow: none; }
.assistant-avatar :deep(.brand-mark), .message-avatar :deep(.brand-mark) { width: 100%; height: 100%; border-radius: inherit; }
.assistant-card h2 { margin-top: 10px; color: var(--brand-primary-dark); }
.online-status { margin-top: 7px; color: var(--brand-primary); font-weight: 600; }
.status-dot { background: var(--brand-primary); box-shadow: 0 0 0 4px var(--brand-primary-soft); }
.mood-card { min-height: auto; padding: 20px 18px; border-radius: var(--radius-lg); background: #f7fbf9; }
.mood-card h2, .history-card h2 { color: var(--text-primary); }
.mood-score { width: 86px; height: 86px; margin: 18px auto 8px; border-color: #fff; background: linear-gradient(145deg, #6fa89f, #347c72); box-shadow: 0 7px 18px rgba(52,124,114,.15); }
.mood-today { gap: 16px; font-size: 14px; }
.mood-today span { color: var(--text-secondary); }
.mood-today strong { color: var(--text-primary); font-size: 16px; }
.mood-level { margin: 14px 0 16px; color: var(--text-secondary); }
.mood-dots i, .mood-dots i:nth-child(2), .mood-dots i:nth-child(3) { background: var(--brand-primary); }
.mood-advice, .mood-action-item { border: 1px solid var(--border-color); box-shadow: none; }
.mood-advice { min-height: 70px; padding: 11px 14px; border-radius: 13px; }
.advice-icon { display: inline-flex; align-items: center; justify-content: center; width: 30px; height: 30px; flex: none; border-radius: 50%; background: var(--brand-primary-soft); color: var(--brand-primary-dark); font-size: 16px; font-weight: 800; }
.mood-advice strong, .mood-advice div span, .mood-actions h3, .mood-action-item { color: #52615e; }
.mood-actions { gap: 8px; margin-top: 16px; }
.mood-action-item { min-height: 44px; padding: 9px 13px; border-radius: 11px; }
.action-dot { width: 7px; height: 7px; flex: none; border-radius: 50%; background: var(--brand-accent); }
.mood-reminder { align-items: flex-start; min-height: 72px; margin-top: 16px; padding: 13px; border: 1px solid #e8c5c5; border-radius: 13px; background: var(--danger-soft); color: #913f3f; }
.mood-reminder .advice-icon { background: #f6dede; color: var(--danger); }
.history-card { height: 300px; padding: 18px; border-radius: var(--radius-lg); }
.history-scrollbar :deep(.el-scrollbar__thumb) { background: #8ebbb2; }
.history-item { min-height: 114px; border-color: transparent; }
.history-item:hover { background: #f6faf8; }
.history-item.is-active { border-color: #bdd8d1; background: var(--brand-primary-soft); }
.chat-card { height: max(780px, calc(100vh - 130px)); border-radius: 20px; }
.chat-header { height: 90px; padding: 0 24px; background: var(--brand-primary-dark); }
.heart-badge { width: 50px; height: 50px; border-radius: 14px; box-shadow: none; }
.chat-heading h2 { font-size: 21px; }
.chat-heading p { color: rgba(255,255,255,.74); font-size: 13px; font-weight: 500; }
.chat-header :deep(.el-button) { border-color: rgba(255,255,255,.32); background: rgba(255,255,255,.1); color: #fff; }
.chat-messages { padding: 28px 25px; background: #fbfcfb; }
.message-avatar { width: 38px; height: 38px; background: transparent; }
.message-avatar.user-avatar { background: #52615e; }
.message-bubble { padding: 12px 16px; border-color: var(--border-color); border-radius: 6px 16px 16px 16px; box-shadow: none; color: var(--text-primary); font-size: 15px; line-height: 1.7; }
.user-message { justify-content: flex-end; flex-direction: row-reverse; }
.user-message .message-bubble { border-color: #cee0dc; border-radius: 16px 6px 16px 16px; background: var(--brand-primary-soft); }
.user-message .message-time { text-align: right; }
.chat-composer { height: auto; min-height: 132px; padding: 18px 22px; border-top-color: var(--border-color); }
.composer-main :deep(.el-textarea__inner) { min-height: 76px !important; resize: none; border: 0; border-radius: 11px; color: var(--text-primary); box-shadow: 0 0 0 1px var(--border-color) inset; }
.composer-main :deep(.el-textarea__inner:focus) { box-shadow: 0 0 0 1px var(--brand-primary) inset, 0 0 0 3px rgba(52,124,114,.1); }
.composer-details { color: var(--text-muted); font-size: 12px; }
.send-symbol { width: 56px; height: 56px; border-radius: 15px; background: var(--brand-primary); box-shadow: 0 8px 18px rgba(52,124,114,.18); }
.send-symbol :deep(.el-button) { border-radius: 15px; }
@media (max-width: 1050px) { .consultation-shell { grid-template-columns: 270px minmax(0, 1fr); } }
@media (max-width: 800px) {
  .consultation-page { padding: 20px 14px 28px; }
  .consultation-shell { grid-template-columns: 1fr; }
  .consultation-sidebar { display: grid; grid-template-columns: repeat(2, minmax(0,1fr)); }
  .mood-card { grid-row: span 2; }
  .history-card { height: 172px; }
  .chat-card { height: min(760px, calc(100vh - 100px)); min-height: 620px; }
}
@media (max-width: 560px) {
  .consultation-sidebar { display: flex; }
  .history-card { height: 220px; }
  .chat-card { height: calc(100vh - 92px); min-height: 600px; }
  .chat-header { height: 82px; padding: 0 14px; }
  .heart-badge { width: 42px; height: 42px; }
  .chat-heading p { max-width: 190px; }
  .chat-messages { padding: 18px 12px; }
  .assistant-message, .user-message { gap: 8px; }
  .message-avatar { width: 32px; height: 32px; }
  .message-content { max-width: calc(100% - 40px); }
  .chat-composer { min-height: 116px; padding: 13px 12px; }
  .send-symbol { width: 48px; height: 48px; }
}
</style>
