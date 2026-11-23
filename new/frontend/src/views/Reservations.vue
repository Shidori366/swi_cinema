<template>
  <div class="container">
    <h1>My Reservations</h1>
    <div v-if="reservations.length === 0">
      <p>No reservations found.</p>
    </div>
    <div v-for="res in reservations" :key="res.id" class="reservation-item">
      <p>ID: {{ res.id }} | Status: {{ res.status }}</p>
      <div class="actions">
        <button v-if="res.status !== 'CANCELLED'" @click="cancelReservation(res.id)">Cancel</button>
        <button 
          v-if="res.status === 'PENDING'" 
          @click="payReservation(res.id)"
          class="btn-pay"
        >
          Pay
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const reservations = ref([])

onMounted(async () => {
  const response = await axios.get('http://localhost:8080/api/reservations/user/1')
  reservations.value = response.data
})

const cancelReservation = async (id) => {
  try {
    await axios.post(`http://localhost:8080/api/reservations/${id}/cancel`)
    const response = await axios.get('http://localhost:8080/api/reservations/user/1')
    reservations.value = response.data
  } catch (err) {
    alert('Error cancelling reservation')
  }
}

const payReservation = async (id) => {
  try {
    await axios.post(`http://localhost:8080/api/reservations/${id}/pay`)
    const response = await axios.get('http://localhost:8080/api/reservations/user/1')
    reservations.value = response.data
    alert('Payment Successful')
  } catch (err) {
    alert('Payment Failed')
  }
}
</script>

<style scoped>
.container {
  padding: 20px;
}
nav {
  margin-bottom: 20px;
}
.reservation-item {
  border-bottom: 1px solid #eee;
  padding: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.actions {
  display: flex;
  gap: 10px;
}
.btn-pay {
  background-color: #28a745;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
}
</style>
