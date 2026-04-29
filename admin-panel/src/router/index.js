import { createRouter, createWebHashHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', noAuth: true }
  },
  {
    path: '/',
    component: () => import('@/components/AdminLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '仪表盘', icon: 'Odometer' }
      },
      {
        path: 'users',
        name: 'UserList',
        component: () => import('@/views/users/UserList.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'videos',
        name: 'VideoList',
        component: () => import('@/views/videos/VideoList.vue'),
        meta: { title: '视频管理', icon: 'VideoCamera' }
      },
      {
        path: 'videos/add',
        name: 'VideoAdd',
        component: () => import('@/views/videos/VideoEdit.vue'),
        meta: { title: '添加视频', icon: 'VideoCamera' }
      },
      {
        path: 'videos/:id/edit',
        name: 'VideoEdit',
        component: () => import('@/views/videos/VideoEdit.vue'),
        meta: { title: '编辑视频', icon: 'VideoCamera' }
      },
      {
        path: 'foods',
        name: 'FoodList',
        component: () => import('@/views/foods/FoodList.vue'),
        meta: { title: '食物管理', icon: 'Food' }
      },
      {
        path: 'foods/add',
        name: 'FoodAdd',
        component: () => import('@/views/foods/FoodEdit.vue'),
        meta: { title: '添加食物', icon: 'Food' }
      },
      {
        path: 'foods/:id/edit',
        name: 'FoodEdit',
        component: () => import('@/views/foods/FoodEdit.vue'),
        meta: { title: '编辑食物', icon: 'Food' }
      },
      {
        path: 'foods/import',
        name: 'FoodImport',
        component: () => import('@/views/foods/FoodImport.vue'),
        meta: { title: '批量导入', icon: 'Food' }
      },
      {
        path: 'admins',
        name: 'AdminList',
        component: () => import('@/views/admins/AdminList.vue'),
        meta: { title: '管理员管理', icon: 'Setting', superOnly: true }
      },
      {
        path: 'admins/add',
        name: 'AdminAdd',
        component: () => import('@/views/admins/AdminEdit.vue'),
        meta: { title: '添加管理员', icon: 'Setting', superOnly: true }
      },
      {
        path: 'admins/:id/edit',
        name: 'AdminEdit',
        component: () => import('@/views/admins/AdminEdit.vue'),
        meta: { title: '编辑管理员', icon: 'Setting', superOnly: true }
      },
      {
        path: 'logs',
        name: 'Logs',
        component: () => import('@/views/Logs.vue'),
        meta: { title: '操作日志', icon: 'Document', superOnly: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 健身打卡管理` : '健身打卡管理'
  const authStore = useAuthStore()

  if (to.meta.noAuth) {
    if (authStore.token) {
      return next('/dashboard')
    }
    return next()
  }

  if (!authStore.token) {
    return next('/login')
  }

  if (to.meta.superOnly && authStore.role !== 'super') {
    return next('/dashboard')
  }

  next()
})

export default router
