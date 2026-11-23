<template>
  <div class="screening-detail">
    <h1 class="title">Select Seats</h1>
    <p v-if="screening.movieName" class="movie-info">
      Movie: {{ screening.movieName }} | Room: {{ screening.roomName }}
    </p>
    <p v-else-if="error" class="error-msg">
      Error: {{ error }}
    </p>
    <p v-else-if="loading" class="loading-msg">
      Loading screening details...
    </p>
    
    <div v-if="allSeats.length > 0 && !loading" class="seat-grid">
      <div 
        v-for="seat in allSeats" 
        :key="seat.id" 
        :class="['seat', getSeatStatus(seat)]" 
        @click="toggleSeat(seat)" 
        :title="seat.id"
      >
        {{ seat.id }} 
      </div>
    </div>

    <div v-if="allSeats.length > 0 && !loading" class="legend">
      <span>[X] Taken</span>
      <span>[*] Selected</span>
    </div>

    <div v-if="allSeats.length > 0 && !loading" class="actions">
      <button class="btn-primary" @click="submitReservation" :disabled="selectedSeats.size === 0">
        Continue
      </button>
      <button class="btn-secondary" @click="router.back()">Back</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const screeningId = route.params.id

const allSeats = ref([])
const availableIds = ref(new Set())
const selectedSeats = ref(new Set())
const loading = ref(true)
const error = ref(null)
const screening = ref({
  movieName: '',
  roomName: ''
})

onMounted(async () => {
  try {
    loading.value = true
    const screeningRes = await axios.get(`http://localhost:8080/api/screenings/${screeningId}`)
    console.log('Screening Data:', screeningRes.data)
    screening.value = screeningRes.data
    
    const seatsRes = await axios.get(`http://localhost:8080/api/screenings/${screeningId}/seats`)
    console.log('Seats Data:', seatsRes.data)
    allSeats.value = seatsRes.data
    
    const availRes = await axios.get(`http://localhost:8080/api/screenings/${screeningId}/seats/available`)
    console.log('Available Seats:', availRes.data)
    availableIds.value = new Set(availRes.data.map(s => s.id))
  } catch (err) {
    console.error('Error loading screening detail:', err)
    error.value = err.message
  } finally {
    loading.value = false
  }
})

const getSeatStatus = (seat) => {
  if (!availableIds.value.has(seat.id)) return 'taken'
  if (selectedSeats.value.has(seat.id)) return 'selected'
  return 'available'
}

const toggleSeat = (seat) => {
  if (availableIds.value.has(seat.id)) {
    if (selectedSeats.value.has(seat.id)) {
      selectedSeats.value.delete(seat.id)
    } else {
      selectedSeats.value.add(seat.id)
    }
  }
}

const submitReservation = async () => {
  try {
    const payload = {
      screeningId: Number(screeningId),
      seatIds: Array.from(selectedSeats.value),
      userId: 1
    }
    
    await axios.post('http://localhost:8080/api/reservations', payload)
    router.push('/reservations')
  } catch (err) {
    alert('Error creating reservation')
  }
}
</script>

<style scoped>
.screening-detail {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
}
.title {
  text-align: center;
}
.movie-info {
  text-align: center;
  margin-bottom: 20px;
}
.error-msg {
  color: red;
  text-align: center;
}
.loading-msg {
  color: blue;
  text-align: center;
}
.seat-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
  max-width: 400px;
  margin: 0 auto;
}
.seat {
  padding: 10px;
  border: 1px solid #ccc;
  text-align: center;
  cursor: pointer;
  border-radius: 4px;
}
.seat.taken {
  background-color: #ffcccc;
  color: #cc0000;
  cursor: not-allowed;
  border-color: #ff0000;
}
.seat.selected {
  background-color: #ccffcc;
  color: #006600;
  border-color: #009900;
}
.seat.available {
  background-color: #eeeeee;
}
.legend {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin: 20px 0;
}
.actions {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 20px;
}
.btn-primary {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.btn-primary:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
.btn-secondary {
  padding: 10px 20px;
  background-color: #6c757d;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>
