<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2>用户登录</h2>
      </template>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="100px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model.number="loginForm.userId" placeholder="请输入用户ID"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading">登录</el-button>
          <el-button @click="$router.push('/register')">注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from '../store'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const store = useStore()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  userId: null,
  password: ''
})

const rules = {
  userId: [
    { required: true, message: '请输入用户ID', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const response = await axios.post('/api/user/login', {
          userId: loginForm.userId,
          password: loginForm.password
        })
        
        if (response.data.code === 200) {
          const { sessionId, user } = response.data.data
          store.setSession(sessionId, user)
          ElMessage.success('登录成功')
          router.push('/ticket-query')
        } else {
          ElMessage.error(response.data.message || '登录失败')
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.login-card {
  width: 400px;
}

.login-card h2 {
  margin: 0;
  text-align: center;
}
</style>

