import axios from 'axios'
import { ElMessage } from 'element-plus'
const request = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
    timeout: 5000
})
//请求拦截器
request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if(token) {
            config.headers.Authorization = 'Bearer ' + token
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)
//响应拦截器
request.interceptors.response.use(
    response => {
        const{data,config} = response
        if(data.code === '200') {
            return data
        }
        else
        {
            if(data.code==='-1')
            {
                if(config.url === '/login') {
                    ElMessage.error(data.msg||'登录过期，请重新登录')
                }
                localStorage.removeItem('token')
                localStorage.removeItem('userInfo')
                window.location.href = '/auth/login'
            }
            else {
                ElMessage.error(data.msg||'请求失败')
                return Promise.reject('请求失败')
            }
        }
        return response
    },
    error => {
        return Promise.reject(error)
    }
)
export default request
