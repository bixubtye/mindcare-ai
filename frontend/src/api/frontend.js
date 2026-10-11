import request from '@/utils/request'
export function register(data) {
    return request.post('/user/add',data)
}
export function startSession(data) {
    return request.post('/psychological-chat/session/start',data)
}
export function getSessionList(params) {
    return request.get('/psychological-chat/sessions',{params})
}
export function deleteSession(sessionId) {
    return request.delete(`/psychological-chat/sessions/${sessionId}`)
}
export function getSessionDetail(sessionId) {
    return request.get(`/psychological-chat/sessions/${sessionId}/messages`)
}
export function getSessionEmotion(sessionId) {
    return request.get(`/psychological-chat/session/${sessionId}/emotion`)
}
export function createEmotionDiary(data) {
    return request.post('/emotion-diary', data)
}
export function getKnowledgeList(params) {
    return request.get('/knowledge/article/page',{params})
}
export function getKnowledgeDetail(id) {
    return request.get(`/knowledge/article/${id}`)
}
