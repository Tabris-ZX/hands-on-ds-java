<template>
  <el-container class="layout">
    <el-header class="app-header">
      <div class="header-content">
        <h1>火车票务管理系统</h1>
        <div v-if="userInfo" class="user-info">
          <span>欢迎，{{ userInfo.username }}</span>
          <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
        </div>
      </div>
    </el-header>

    <el-container>
      <el-aside width="220px" class="app-aside">
        <el-menu :default-active="activeMenu" router class="menu">
          <el-menu-item v-if="!userInfo" index="/login">
            <el-icon><User /></el-icon>
            <span>登录</span>
          </el-menu-item>
          <el-menu-item v-if="!userInfo" index="/register">
            <el-icon><UserFilled /></el-icon>
            <span>注册</span>
          </el-menu-item>

          <template v-if="userInfo">
            <el-menu-item index="/ticket-query">
              <el-icon><Search /></el-icon>
              <span>余票查询</span>
            </el-menu-item>
            <el-menu-item index="/buy-ticket">
              <el-icon><ShoppingCart /></el-icon>
              <span>购买车票</span>
            </el-menu-item>
            <el-menu-item index="/my-orders">
              <el-icon><Document /></el-icon>
              <span>我的订单</span>
            </el-menu-item>
            <el-menu-item index="/route-query">
              <el-icon><MapLocation /></el-icon>
              <span>线路查询</span>
            </el-menu-item>
            <el-menu-item index="/train-list">
              <el-icon><List /></el-icon>
              <span>车次一览</span>
            </el-menu-item>

            <template v-if="userInfo.privilege >= 10">
              <el-menu-item index="/train-management">
                <el-icon><Management /></el-icon>
                <span>车次管理</span>
              </el-menu-item>
              <el-menu-item index="/ticket-management">
                <el-icon><SetUp /></el-icon>
                <span>票务管理</span>
              </el-menu-item>
            </template>
          </template>
        </el-menu>
      </el-aside>

      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useStore } from './store'

const router = useRouter()
const route = useRoute()
const store = useStore()

const userInfo = computed(() => store.userInfo)
const activeMenu = computed(() => route.path)

const restoreSession = async () => {
  if (!store.sessionId) {
    return
  }

  try {
    const response = await axios.get('/api/user/validate', {
      headers: {
        Authorization: `Bearer ${store.sessionId}`
      }
    })

    if (response.data.code === 200) {
      store.setSession(store.sessionId, response.data.data)
      return
    }
  } catch (error) {
    // Ignore and clear stale session below.
  }

  store.logout()
  if (route.path !== '/login' && route.path !== '/register') {
    ElMessage.warning('登录状态已失效，请重新登录')
    router.push('/login')
  }
}

const handleLogout = async () => {
  try {
    await axios.post('/api/user/logout', {}, {
      headers: {
        Authorization: `Bearer ${store.sessionId}`
      }
    })
  } catch (error) {
    // Clear local session even if backend logout fails.
  }

  store.logout()
  router.push('/login')
}

onMounted(() => {
  restoreSession()
})
</script>

<style scoped>
.layout {
  min-height: 100vh;
}

.app-header {
  background: #1f4b99;
  color: #fff;
  display: flex;
  align-items: center;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h1 {
  margin: 0;
  font-size: 24px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.app-aside {
  background: #f4f6fb;
  border-right: 1px solid #e5e7eb;
}

.menu {
  border-right: none;
  min-height: calc(100vh - 60px);
}

.app-main {
  background: #f8fafc;
  padding: 20px;
}
</style>
