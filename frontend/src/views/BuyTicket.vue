<template>
  <div class="buy-ticket">
    <el-card>
      <template #header>
        <h3>购买车票</h3>
      </template>

      <el-form label-width="110px" class="buy-form">
        <el-form-item label="车次">
          <el-select
            v-model="buyForm.trainId"
            placeholder="请选择车次"
            filterable
            clearable
            @change="handleTrainChange"
          >
            <el-option
              v-for="train in trains"
              :key="train.trainId"
              :label="formatTrainLabel(train)"
              :value="train.trainId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="出发站">
          <el-select
            v-model="buyForm.departureStation"
            placeholder="请选择出发站"
            filterable
            clearable
          >
            <el-option
              v-for="station in availableStations"
              :key="station"
              :label="station"
              :value="station"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="发车日期时间">
          <el-date-picker
            v-model="departureDateTime"
            type="datetime"
            placeholder="请选择发车日期时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="queryRemaining">先查余票</el-button>
          <el-button type="success" :loading="buying" @click="handleBuy">确认购票</el-button>
        </el-form-item>
      </el-form>

      <el-alert
        v-if="selectedTrain"
        class="train-tip"
        type="info"
        :closable="false"
        :title="`当前车次路线：${selectedTrain.stations.join(' -> ')}`"
      />

      <el-descriptions v-if="remaining !== null" :column="1" border class="summary">
        <el-descriptions-item label="余票">{{ remaining }}</el-descriptions-item>
        <el-descriptions-item label="购票状态">
          {{ remaining > 0 ? '可购买' : '暂无余票' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useStore } from '../store'

const store = useStore()

const trains = ref([])
const loading = ref(false)
const buying = ref(false)
const remaining = ref(null)
const departureDateTime = ref('')

const buyForm = reactive({
  trainId: '',
  departureStation: '',
  departureTime: ''
})

const selectedTrain = computed(() => {
  return trains.value.find(train => train.trainId === buyForm.trainId) || null
})

const availableStations = computed(() => {
  if (!selectedTrain.value?.stations?.length) {
    return []
  }
  return selectedTrain.value.stations.slice(0, -1)
})

const formatDepartureTime = (dateTime) => {
  if (!dateTime) {
    return ''
  }
  const [datePart, timePart] = dateTime.split(' ')
  const [, month, day] = datePart.split('-')
  return `${timePart} ${month}-${day}`
}

const formatTrainLabel = (train) => {
  const stationText = train.stations?.length ? train.stations.join(' -> ') : '无站点信息'
  return `${train.trainId} | ${train.startTime} | ${stationText}`
}

const loadTrains = async () => {
  try {
    const response = await axios.get('/api/train/list', {
      headers: {
        Authorization: `Bearer ${store.sessionId}`
      }
    })

    if (response.data.code === 200) {
      trains.value = response.data.data || []
      return
    }

    ElMessage.error(response.data.message || '加载车次失败')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '加载车次失败')
  }
}

const syncDepartureTime = () => {
  buyForm.departureTime = formatDepartureTime(departureDateTime.value)
}

const ensureCompleteSelection = () => {
  syncDepartureTime()
  if (!buyForm.trainId || !buyForm.departureStation || !buyForm.departureTime) {
    ElMessage.warning('请完整选择车次、出发站和发车日期时间')
    return false
  }
  return true
}

const handleTrainChange = () => {
  buyForm.departureStation = ''
  remaining.value = null
}

const queryRemaining = async () => {
  if (!ensureCompleteSelection()) {
    return
  }

  loading.value = true
  try {
    const response = await axios.post('/api/ticket/remaining', buyForm, {
      headers: {
        Authorization: `Bearer ${store.sessionId}`
      }
    })

    if (response.data.code === 200) {
      remaining.value = response.data.data
      ElMessage.success('余票查询成功')
      return
    }

    ElMessage.error(response.data.message || '查询余票失败')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '查询余票失败')
  } finally {
    loading.value = false
  }
}

const handleBuy = async () => {
  if (!ensureCompleteSelection()) {
    return
  }

  if (remaining === null) {
    await queryRemaining()
  }

  if (remaining.value !== null && remaining.value <= 0) {
    ElMessage.warning('当前没有可购买的余票')
    return
  }

  buying.value = true
  try {
    const response = await axios.post('/api/ticket/buy', buyForm, {
      headers: {
        Authorization: `Bearer ${store.sessionId}`
      }
    })

    if (response.data.code === 200) {
      ElMessage.success('购票成功')
      remaining.value = remaining.value === null ? null : Math.max(remaining.value - 1, 0)
      return
    }

    ElMessage.error(response.data.message || '购票失败')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '购票失败')
  } finally {
    buying.value = false
  }
}

onMounted(() => {
  loadTrains()
})
</script>

<style scoped>
.buy-ticket {
  max-width: 800px;
}

.buy-form {
  max-width: 560px;
}

.train-tip {
  margin-top: 16px;
}

.summary {
  margin-top: 16px;
}
</style>
