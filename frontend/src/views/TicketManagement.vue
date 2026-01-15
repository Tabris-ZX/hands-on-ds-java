<template>
  <div class="ticket-management">
    <el-card>
      <template #header>
        <h3>票务管理</h3>
      </template>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="发售车票" name="release">
          <el-form :model="releaseForm" label-width="120px" style="max-width: 400px">
            <el-form-item label="车次ID">
              <el-input v-model="releaseForm.trainId" placeholder="请输入车次ID"></el-input>
            </el-form-item>
            <el-form-item label="出发时间">
              <el-input v-model="releaseForm.departureTime" placeholder="格式: HH:MM MM-DD，如 08:00 06-15"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleRelease" :loading="loading">发售</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="停售车票" name="expire">
          <el-form :model="expireForm" label-width="120px" style="max-width: 400px">
            <el-form-item label="车次ID">
              <el-input v-model="expireForm.trainId" placeholder="请输入车次ID"></el-input>
            </el-form-item>
            <el-form-item label="出发时间">
              <el-input v-model="expireForm.departureTime" placeholder="格式: HH:MM MM-DD，如 08:00 06-15"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="danger" @click="handleExpire" :loading="loading">停售</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useStore } from '../store'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const store = useStore()

const activeTab = ref('release')
const loading = ref(false)

const releaseForm = reactive({
  trainId: '',
  departureTime: ''
})

const expireForm = reactive({
  trainId: '',
  departureTime: ''
})

const handleRelease = async () => {
  if (!releaseForm.trainId || !releaseForm.departureTime) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  loading.value = true
  try {
    const response = await axios.post('/api/ticket/release', {
      trainId: releaseForm.trainId,
      departureTime: releaseForm.departureTime
    }, {
      headers: {
        Authorization: `Bearer ${store.sessionId}`
      }
    })
    
    if (response.data.code === 200) {
      ElMessage.success('发售成功')
      releaseForm.trainId = ''
      releaseForm.departureTime = ''
    } else {
      ElMessage.error(response.data.message || '发售失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '发售失败')
  } finally {
    loading.value = false
  }
}

const handleExpire = async () => {
  if (!expireForm.trainId || !expireForm.departureTime) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  loading.value = true
  try {
    const response = await axios.post('/api/ticket/expire', {
      trainId: expireForm.trainId,
      departureTime: expireForm.departureTime
    }, {
      headers: {
        Authorization: `Bearer ${store.sessionId}`
      }
    })
    
    if (response.data.code === 200) {
      ElMessage.success('停售成功')
      expireForm.trainId = ''
      expireForm.departureTime = ''
    } else {
      ElMessage.error(response.data.message || '停售失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '停售失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.ticket-management {
  width: 100%;
}
</style>

