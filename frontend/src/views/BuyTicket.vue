<template>
  <div class="buy-ticket">
    <el-card>
      <template #header>
        <h3>购票</h3>
      </template>
      <el-form :model="buyForm" label-width="120px">
        <el-form-item label="车次ID">
          <el-input v-model="buyForm.trainId" placeholder="请输入车次ID"></el-input>
        </el-form-item>
        <el-form-item label="出发时间">
          <el-input v-model="buyForm.departureTime" placeholder="格式: HH:MM MM-DD，如 08:00 06-15"></el-input>
        </el-form-item>
        <el-form-item label="出发站">
          <el-select v-model="buyForm.departureStation" placeholder="请选择出发站" filterable>
            <el-option
              v-for="station in stations"
              :key="station"
              :label="station"
              :value="station">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleBuy" :loading="loading">购买</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useStore } from '../store'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const store = useStore()

const buyForm = reactive({
  trainId: '',
  departureTime: '',
  departureStation: ''
})

const stations = ref([])
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

const handleBuy = async () => {
  if (!buyForm.trainId || !buyForm.departureTime || !buyForm.departureStation) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  loading.value = true
  try {
    const response = await axios.post('/api/ticket/buy', buyForm, {
      headers: {
        Authorization: `Bearer ${store.sessionId}`
      }
    })
    
    if (response.data.code === 200) {
      ElMessage.success('购票成功')
      buyForm.trainId = ''
      buyForm.departureTime = ''
      buyForm.departureStation = ''
    } else {
      ElMessage.error(response.data.message || '购票失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '购票失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStations()
})
</script>

<style scoped>
.buy-ticket {
  max-width: 600px;
}
</style>

