<template>
  <div class="container">
    <h1>Admin - Manage Reservations</h1>
    <table border="1">
      <thead>
        <tr>
          <th>ID</th>
          <th>Contact</th>
          <th>Screening ID</th>
          <th>Status</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="res in reservations" :key="res.id">
          <td>{{ res.id }}</td>
          <td>{{ res.contactEmail }}</td>
          <td>{{ res.screeningId }}</td>
          <td>{{ res.status }}</td>
          <td>
            <button v-if="res.status !== 'CANCELLED'" @click="cancelReservation(res.id)">Cancel</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const reservations = ref([])

onMounted(async () => {
  const response = await axios.get('http://localhost:8080/api/reservations')
  reservations.value = response.data
})

const cancelReservation = async (id) => {
  try {
    await axios.post(`http://localhost:8080/api/reservations/${id}/cancel`)
    const response = await axios.get('http://localhost:8080/api/reservations')
    reservations.value = response.data
  } catch (err) {
    alert('Error cancelling reservation')
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
table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}
th, td {
  padding: 10px;
  border: 1px solid #ddd;
  text-align: left;
}
</style>
