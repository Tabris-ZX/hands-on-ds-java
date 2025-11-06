<template>
  <div class="train-management">
    <el-card>
      <template #header>
        <h3>车次管理</h3>
      </template>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="添加车次" name="add">
          <el-form :model="addForm" label-width="120px" style="max-width: 800px">
            <el-form-item label="车次ID">
              <el-input v-model="addForm.trainId" placeholder="请输入车次ID"></el-input>
            </el-form-item>
            <el-form-item label="座位数">
              <el-input-number v-model="addForm.seatNum" :min="1"></el-input-number>
            </el-form-item>
            <el-form-item label="首发时间">
              <el-input v-model="addForm.startTime" placeholder="格式: HH:MM，如 08:00"></el-input>
            </el-form-item>
            <el-form-item label="站点列表">
              <el-input
                v-model="addForm.stationsInput"
                type="textarea"
                :rows="3"
                placeholder="请输入站点名称，用 / 分隔，如：北京/天津/济南/青岛">
              </el-input>
            </el-form-item>
            <el-form-item label="区段时长(分钟)">
              <el-input
                v-model="addForm.durationsInput"
                placeholder="请输入各区段时长，用 / 分隔，如：35/95/160">
              </el-input>
            </el-form-item>
            <el-form-item label="区段票价">
              <el-input
                v-model="addForm.pricesInput"
                placeholder="请输入各区段票价，用 / 分隔，如：29/97/118">
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleAdd" :loading="loading">添加</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="查询车次" name="query">
          <el-form :model="queryForm" label-width="120px" style="max-width: 400px">
            <el-form-item label="车次ID">
              <el-input v-model="queryForm.trainId" placeholder="请输入车次ID"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleQuery" :loading="loading">查询</el-button>
            </el-form-item>
          </el-form>
          
          <el-card v-if="trainInfo" style="margin-top: 20px">
            <h4>车次信息</h4>
            <p><strong>车次ID:</strong> {{ trainInfo.trainId }}</p>
            <p><strong>座位数:</strong> {{ trainInfo.seatNum }}</p>
            <p><strong>首发时间:</strong> {{ trainInfo.startTime }}</p>
            <p><strong>站点:</strong> {{ trainInfo.stations.join(' -> ') }}</p>
            <p><strong>区段时长:</strong> {{ trainInfo.durations.join(' / ') }} 分钟</p>
            <p><strong>区段票价:</strong> {{ trainInfo.prices.join(' / ') }} 元</p>
          </el-card>
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

const activeTab = ref('add')
const loading = ref(false)

const addForm = reactive({
  trainId: '',
  seatNum: 1000,
  startTime: '',
  stationsInput: '',
  durationsInput: '',
  pricesInput: ''
})

const queryForm = reactive({
  trainId: ''
})

const trainInfo = ref(null)

const handleAdd = async () => {
  if (!addForm.trainId || !addForm.startTime || !addForm.stationsInput || 
      !addForm.durationsInput || !addForm.pricesInput) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  const stations = addForm.stationsInput.split('/').map(s => s.trim()).filter(s => s)
  const durations = addForm.durationsInput.split('/').map(s => parseInt(s.trim())).filter(s => !isNaN(s))
  const prices = addForm.pricesInput.split('/').map(s => parseInt(s.trim())).filter(s => !isNaN(s))
  
  if (stations.length < 2) {
    ElMessage.warning('至少需要2个站点')
    return
  }
  
  if (durations.length !== stations.length - 1) {
    ElMessage.warning('区段时长数量应该比站点数量少1')
    return
  }
  
  if (prices.length !== stations.length - 1) {
    ElMessage.warning('区段票价数量应该比站点数量少1')
    return
  }
  
  loading.value = true
  try {
    const response = await axios.post('/api/train/add', {
      trainId: addForm.trainId,
      seatNum: addForm.seatNum,
      startTime: addForm.startTime,
      stations: stations,
      durations: durations,
      prices: prices
    }, {
      headers: {
        Authorization: `Bearer ${store.sessionId}`
      }
    })
    
    if (response.data.code === 200) {
      ElMessage.success('添加成功')
      addForm.trainId = ''
      addForm.startTime = ''
      addForm.stationsInput = ''
      addForm.durationsInput = ''
      addForm.pricesInput = ''
    } else {
      ElMessage.error(response.data.message || '添加失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '添加失败')
  } finally {
    loading.value = false
  }
}

const handleQuery = async () => {
  if (!queryForm.trainId) {
    ElMessage.warning('请输入车次ID')
    return
  }
  
  loading.value = true
  try {
    const response = await axios.get(`/api/train/query/${queryForm.trainId}`, {
      headers: {
        Authorization: `Bearer ${store.sessionId}`
      }
    })
    
    if (response.data.code === 200) {
      trainInfo.value = response.data.data
    } else {
      ElMessage.error(response.data.message || '查询失败')
      trainInfo.value = null
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '查询失败')
    trainInfo.value = null
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.train-management {
  width: 100%;
}
</style>

