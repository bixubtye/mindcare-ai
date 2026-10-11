import request from '@/utils/request'
import { formContextKey } from 'element-plus'
export function login(data) {
    return request.post('/user/login',data)
}

export function catagoryTree() {
    return request.get('/knowledge/category/tree')
}
export function articlePage(params) {
    return request.get('/knowledge/article/page',{params})
}
export function uploadFile(file,businessInfo) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('businessType','ARTICLE')
    formData.append('businessId',businessInfo.businessId)
    formData.append('businessField','cover')
    return request.post('/file/upload',formData,
        {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        }
    )
}
export function createArticle(data) {
    return request.post('/knowledge/article',data)
}
export function getArticleDetail(id) {
    return request.get(`/knowledge/article/${id}`)
}
export function updateArticle(id,data) {
    return request.put(`/knowledge/article/${id}`,data)
}
export function changeArticleStatus(id,data) {
    return request.put(`/knowledge/article/${id}/status`,data)
}
export function deleteArticle(id) {
    return request.delete(`/knowledge/article/${id}`)
}
export function getConsultationPage(params) {
    return request.get('/psychological-chat/sessions',{params})
}
export function getSessionDetail(sessionId) {
    return request.get(`/psychological-chat/sessions/${sessionId}/messages`)
}
export function getEmotionDiaryPage(params) {
    return request.get('/emotion-diary/admin/page',{params})
}
export function deleteEmotionDiary(id) {
    return request.delete(`/emotion-diary/admin/${id}`)
}
export function getAnalyticsOverview() {
    return request.get(`/data-analytics/overview`)
}
export function logout() {
    return request.post(`/user/logout`)
}
