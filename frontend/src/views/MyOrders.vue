<template>
  <div class="my-orders">
    <el-card>
      <template #header>
        <h3>我的订单</h3>
      </template>
      <el-button type="primary" @click="loadOrders" :loading="loading">刷新</el-button>
      
      <el-table :data="orders" style="width: 100%; margin-top: 20px">
        <el-table-column prop="trainId" label="车次ID" width="120"></el-table-column>
        <el-table-column prop="departureStation" label="出发站" width="120"></el-table-column>
        <el-table-column prop="arrivalStation" label="到达站" width="120"></el-table-column>
        <el-table-column prop="departureTime" label="出发时间" width="150"></el-table-column>
        <el-table-column prop="arrivalTime" label="到达时间" width="150"></el-table-column>
        <el-table-column prop="duration" label="时长(分钟)" width="100"></el-table-column>
        <el-table-column prop="price" label="票价" width="100"></el-table-column>
        <el-table-column prop="ticketNumber" label="票数" width="80"></el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button
              v-if="scope.row.ticketNumber > 0"
              type="danger"
              size="small"
              @click="handleRefund(scope.row)">
              退票
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="orders.length === 0 && !loading" description="暂无订单"></el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useStore } from '../store'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const store = useStore()

const orders = ref([])
const loading = ref(false)

const loadOrders = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/ticket/orders', {
      headers: {
        Authorization: `Bearer ${store.sessionId}`
      }
    })
    
    if (response.data.code === 200) {
      orders.value = response.data.data || []
    } else {
      ElMessage.error(response.data.message || '查询失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '查询失败')
  } finally {
    loading.value = false
  }
}

const handleRefund = async (order) => {
  try {
    await ElMessageBox.confirm('确定要退票吗？', '退票确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await axios.post('/api/ticket/refund', {
      trainId: order.trainId,
      departureTime: order.departureTime,
      departureStation: order.departureStation
    }, {
      headers: {
        Authorization: `Bearer ${store.sessionId}`
      }
    })
    
    if (response.data.code === 200) {
      ElMessage.success('退票成功')
      loadOrders()
    } else {
      ElMessage.error(response.data.message || '退票失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '退票失败')
    }
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.my-orders {
  width: 100%;
}
</style>

