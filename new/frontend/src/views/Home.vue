<template>
  <div class="container">
    <h1>Available Screenings</h1>
    <div v-for="screening in screenings" :key="screening.id" class="screening-card">
      <h3>{{ screening.movieName }}</h3>
      <p>Time: {{ screening.time }}</p>
      <p>Room: {{ screening.roomName }}</p>
      <p>Price: {{ screening.price }} {{ currency }}</p>
      <button @click="goToDetail(screening.id)">View Seats & Reserve</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const screenings = ref([])
const currency = ref('CZK')

onMounted(async () => {
  const response = await axios.get('http://localhost:8080/api/screenings')
  screenings.value = response.data
})

const goToDetail = (id) => {
  router.push(`/screenings/${id}`)
}
</script>

<style scoped>
.container {
  padding: 20px;
}
nav {
  margin-bottom: 20px;
}
.screening-card {
  border: 1px solid #ccc;
  padding: 15px;
  margin-bottom: 10px;
  border-radius: 8px;
}
</style>
