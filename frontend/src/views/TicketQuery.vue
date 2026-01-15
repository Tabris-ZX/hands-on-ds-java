<template>
  <div class="ticket-query">
    <el-card>
      <template #header>
        <h3>余票查询</h3>
      </template>
      <el-form :model="queryForm" label-width="120px">
        <el-form-item label="车次ID">
          <el-input v-model="queryForm.trainId" placeholder="请输入车次ID"></el-input>
        </el-form-item>
        <el-form-item label="出发时间">
          <el-input v-model="queryForm.departureTime" placeholder="格式: HH:MM MM-DD，如 08:00 06-15"></el-input>
        </el-form-item>
        <el-form-item label="出发站">
          <el-select v-model="queryForm.departureStation" placeholder="请选择出发站" filterable>
            <el-option
              v-for="station in stations"
              :key="station"
              :label="station"
              :value="station">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery" :loading="loading">查询</el-button>
        </el-form-item>
      </el-form>
      
      <el-result v-if="remaining !== null" :title="`余票数量: ${remaining}`" sub-title=" ">
      </el-result>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useStore } from '../store'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const store = useStore()

const queryForm = reactive({
  trainId: '',
  departureTime: '',
  departureStation: ''
})

const stations = ref([])
const remaining = ref(null)
const loading = ref(false)

const loadStations = async () => {
  try {
    const response = await axios.get('/api/route/stations')
    if (response.data.code === 200) {
      stations.value = response.data.data
    }
  } catch (error) {
    ElMessage.error('加载站点列表失败')
  }
}

const handleQuery = async () => {
  if (!queryForm.trainId || !queryForm.departureTime || !queryForm.departureStation) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  loading.value = true
  try {
    const response = await axios.post('/api/ticket/remaining', {
      trainId: queryForm.trainId,
      departureTime: queryForm.departureTime,
      departureStation: queryForm.departureStation
    }, {
      headers: {
        Authorization: `Bearer ${store.sessionId}`
      }
    })
    
    if (response.data.code === 200) {
      remaining.value = response.data.data
    } else {
      ElMessage.error(response.data.message || '查询失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '查询失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStations()
})
</script>

<style scoped>
.ticket-query {
  max-width: 600px;
}
</style>

