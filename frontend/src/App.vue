<template>
  <el-container>
    <el-header>
      <div class="header-content">
        <h1>火车票务管理系统</h1>
        <div class="user-info" v-if="userInfo">
          <span>欢迎，{{ userInfo.username }}</span>
          <el-button type="danger" size="small" @click="handleLogout">退出</el-button>
        </div>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px">
        <el-menu
          :default-active="activeMenu"
          router
          class="el-menu-vertical">
          <el-menu-item index="/login" v-if="!userInfo">
            <el-icon><User /></el-icon>
            <span>登录</span>
          </el-menu-item>
          <el-menu-item index="/register" v-if="!userInfo">
            <el-icon><UserFilled /></el-icon>
            <span>注册</span>
          </el-menu-item>
          <template v-if="userInfo">
            <el-menu-item index="/ticket-query">
              <el-icon><Search /></el-icon>
              <span>票务查询</span>
            </el-menu-item>
            <el-menu-item index="/buy-ticket">
              <el-icon><ShoppingCart /></el-icon>
              <span>购票</span>
            </el-menu-item>
            <el-menu-item index="/my-orders">
              <el-icon><Document /></el-icon>
              <span>我的订单</span>
            </el-menu-item>
            <el-menu-item index="/route-query">
              <el-icon><MapLocation /></el-icon>
              <span>路线查询</span>
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
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from './store'
import axios from 'axios'

const router = useRouter()
const route = useRoute()
const store = useStore()

const userInfo = computed(() => store.userInfo)
const activeMenu = computed(() => route.path)

const handleLogout = async () => {
  try {
    await axios.post('/api/user/logout', {}, {
      headers: {
        Authorization: `Bearer ${store.sessionId}`
      }
    })
    store.logout()
    router.push('/login')
  } catch (error) {
    console.error('登出失败', error)
    store.logout()
    router.push('/login')
  }
}

onMounted(() => {
  // 检查是否有保存的会话
  const savedSessionId = localStorage.getItem('sessionId')
  const savedUserInfo = localStorage.getItem('userInfo')
  if (savedSessionId && savedUserInfo) {
    store.setSession(savedSessionId, JSON.parse(savedUserInfo))
  }
})
</script>

<style scoped>
.el-header {
  background-color: #409EFF;
  color: white;
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
  gap: 10px;
}

.el-aside {
  background-color: #f5f5f5;
}

.el-menu-vertical {
  border-right: none;
}

.el-main {
  background-color: #fafafa;
  padding: 20px;
}
</style>

