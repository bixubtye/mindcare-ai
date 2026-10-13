import { createRouter, createWebHashHistory } from 'vue-router'
import BackendLayout from '@/components/BackendLayout.vue'
import AuthLayout from '@/components/AuthLayout.vue'
import Login from '@/views/Login.vue'
import FrontendLayout from '@/components/FrontendLayout.vue'
const backendRoutes = [
  {
    path: '/back',
    redirect:'/back/dashboard',
    component: BackendLayout,
    children: [
      {
        path:'dashboard',
        component:()=>import('@/views/Dashboard.vue'),
        meta:{
          title:'校园状态概览',
          icon:'PieChart'
        }
      }
      ,
      {
        path:'knowledge',
        component:()=>import('@/views/Knowledge.vue'),
        meta:{
          title:'工具内容管理',
          icon:'Reading'
        }
      }
      ,
      {
        path:'consultation',
        component:()=>import('@/views/Consultation.vue'),
        meta:{
          title:'复盘记录',
          icon:'ChatRound'
        }
      }
      ,
      {
        path:'emotional',
        component:()=>import('@/views/Emotional.vue'),
        meta:{
          title:'状态打卡记录',
          icon:'Sunrise'
        }
      }
    ]
  },
  {
    path:'/auth',
    component:AuthLayout,
    children:[
      {
        path:'login',
        component:Login,
        meta:{
          title:'登录'
        }
      },
      {
        path:'register',
        component:()=>import('@/views/Register.vue'),
        meta:{
          title:'注册'
        }
      }
    ]
  }
]
const frontendRoutes = [
  {
    path:'/',
    component:FrontendLayout,
    children:[
      {
        path:'',
        component:()=>import('@/views/Home.vue'),
      },
      {
        path:'consultation',
        component:()=>import('@/views/userConsultation.vue'),
      },
      {
        path:'emotion-dairy',
        component:()=>import('@/views/emotionDairy.vue'),
      },
      {
        path:'knowledge',
        component:()=>import('@/views/userKnowledge.vue'),
      },
      {
        path:'knowledge/article/:id',
        component:()=>import('@/views/articleDetail.vue'),
      }
    ]
  }
]
const router = createRouter({
  history: createWebHashHistory(),
  routes: [...backendRoutes,...frontendRoutes]
})
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    if (token) {
      const userInfo = JSON.parse(localStorage.getItem('userInfo'))
      if(userInfo.userType ==2){
        if(to.path.startsWith('/back')){
          next()
        }
        else{
        next({ path: '/back/dashboard' })
      }
      }
      else if(userInfo.userType==1){
        if(to.path.startsWith('/back')||to.path.startsWith('/auth')){
          next({ path: '/' })
        }
        else{
          next()
        }
      }
    } else {
        if (to.path.startsWith('/back')) {
            next({ path: '/auth/login' })
        } else {
            next()
        }
    }
})
export default router
