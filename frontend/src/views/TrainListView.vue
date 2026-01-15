<template>
  <div class="train-list-view">
    <el-card>
      <template #header>
        <div class="header-content">
          <h3>车次一览</h3>
          <el-tag type="info">显示所有车次调度计划</el-tag>
        </div>
      </template>
      
      <div class="toolbar">
        <el-button type="primary" @click="loadTrains" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
        <el-input
          v-model="searchText"
          placeholder="搜索车次ID、站点..."
          style="width: 300px; margin-left: 10px"
          clearable>
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      
      <el-table 
        :data="filteredTrains" 
        style="width: 100%; margin-top: 20px"
        stripe
        v-loading="loading">
        <el-table-column prop="trainId" label="车次ID" width="120" sortable></el-table-column>
        <el-table-column prop="seatNum" label="座位数" width="100" sortable></el-table-column>
        <el-table-column prop="startTime" label="首发时间" width="150" sortable></el-table-column>
        <el-table-column label="站点路线" min-width="200">
          <template #default="scope">
            <el-text>{{ scope.row.stations ? scope.row.stations.join(' → ') : '-' }}</el-text>
          </template>
        </el-table-column>
        <el-table-column label="区段时长(分钟)" width="150">
          <template #default="scope">
            <el-text>{{ scope.row.durations ? scope.row.durations.join(' / ') : '-' }}</el-text>
          </template>
        </el-table-column>
        <el-table-column label="区段票价(元)" width="150">
          <template #default="scope">
            <el-text>{{ scope.row.prices ? scope.row.prices.join(' / ') : '-' }}</el-text>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button 
              type="primary" 
              size="small" 
              @click="viewDetails(scope.row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="filteredTrains.length === 0 && !loading" description="暂无车次信息"></el-empty>
      
      <div v-if="trains.length > 0" class="summary">
        <el-text>共 {{ trains.length }} 个车次</el-text>
      </div>
    </el-card>
    
    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="车次详情" width="600px">
      <div v-if="selectedTrain" class="train-details">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="车次ID">{{ selectedTrain.trainId }}</el-descriptions-item>
          <el-descriptions-item label="座位数">{{ selectedTrain.seatNum }}</el-descriptions-item>
          <el-descriptions-item label="首发时间">{{ selectedTrain.startTime }}</el-descriptions-item>
          <el-descriptions-item label="站点数">{{ selectedTrain.stations ? selectedTrain.stations.length : 0 }}</el-descriptions-item>
          <el-descriptions-item label="站点路线" :span="2">
            <el-tag 
              v-for="(station, index) in selectedTrain.stations" 
              :key="index"
              style="margin-right: 5px; margin-bottom: 5px">
              {{ station }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="区段时长(分钟)" :span="2">
            {{ selectedTrain.durations ? selectedTrain.durations.join(' / ') : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="区段票价(元)" :span="2">
            {{ selectedTrain.prices ? selectedTrain.prices.join(' / ') : '-' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStore } from '../store'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const store = useStore()

const trains = ref([])
const loading = ref(false)
const searchText = ref('')
const detailDialogVisible = ref(false)
const selectedTrain = ref(null)

const filteredTrains = computed(() => {
  if (!searchText.value) {
    return trains.value
  }
  const text = searchText.value.toLowerCase()
  return trains.value.filter(train => {
    const trainIdMatch = train.trainId && train.trainId.toLowerCase().includes(text)
    const stationsMatch = train.stations && train.stations.some(s => s.toLowerCase().includes(text))
    return trainIdMatch || stationsMatch
  })
})

const loadTrains = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/train/list', {
      headers: {
        Authorization: `Bearer ${store.sessionId}`
      }
    })
    
    if (response.data.code === 200) {
      trains.value = response.data.data || []
      ElMessage.success('加载成功')
    } else {
      ElMessage.error(response.data.message || '加载失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '加载失败')
  } finally {
    loading.value = false
  }
}

const viewDetails = (train) => {
  selectedTrain.value = train
  detailDialogVisible.value = true
}

onMounted(() => {
  loadTrains()
})
</script>

<style scoped>
.train-list-view {
  width: 100%;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h3 {
  margin: 0;
}

.toolbar {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.summary {
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

.train-details {
  padding: 10px 0;
}
</style>

