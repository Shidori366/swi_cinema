<template>
  <div class="page screening-detail">
    <button class="back-link" @click="router.back()">← Back to screenings</button>

    <header class="page-header detail-header">
      <h1 class="page-title">Select Seats</h1>
      <div v-if="screening.movieName" class="movie-info">
        <span class="movie-name">{{ screening.movieName }}</span>
        <span class="dot">•</span>
        <span>{{ screening.roomName }}</span>
      </div>
    </header>

    <div v-if="error" class="state state-error">
      <div class="state-icon">⚠️</div>
      <p>Error: {{ error }}</p>
    </div>
    <div v-else-if="loading" class="state">
      <div class="spinner"></div>
      <p>Loading screening details…</p>
    </div>

    <template v-if="allSeats.length > 0 && !loading">
      <section class="hall card">
        <div class="screen">
          <div class="screen-curve"></div>
          <span class="screen-label">Screen</span>
        </div>

        <div class="seat-grid">
          <button
            v-for="seat in allSeats"
            :key="seat.id"
            type="button"
            :class="['seat', getSeatStatus(seat)]"
            :disabled="getSeatStatus(seat) === 'taken'"
            :aria-pressed="getSeatStatus(seat) === 'selected'"
            @click="toggleSeat(seat)"
            :title="`Seat ${seat.seatNumber || seat.id}`"
          >
            {{ seat.seatNumber || seat.id }}
          </button>
        </div>

        <div class="legend">
          <span class="legend-item"><i class="swatch available"></i> Available</span>
          <span class="legend-item"><i class="swatch selected"></i> Selected</span>
          <span class="legend-item"><i class="swatch taken"></i> Taken</span>
        </div>
      </section>

      <section class="summary card">
        <div class="summary-info">
          <span class="summary-label">Selected seats</span>
          <div class="summary-seats">
            <template v-if="selectedSeats.size > 0">
              <span v-for="id in selectedSeats" :key="id" class="chip">{{ seatLabel(id) }}</span>
            </template>
            <span v-else class="summary-empty">None yet — tap a seat above</span>
          </div>
        </div>
        <div class="summary-actions">
          <button class="btn btn-ghost" @click="router.back()">Back</button>
          <button class="btn btn-primary" @click="submitReservation" :disabled="selectedSeats.size === 0">
            Continue
            <span v-if="selectedSeats.size > 0" class="count">{{ selectedSeats.size }}</span>
          </button>
        </div>
      </section>
    </template>
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
    const screeningRes = await axios.get(import.meta.env.VITE_API_BASE_URL + `/api/screenings/${screeningId}`)
    console.log('Screening Data:', screeningRes.data)
    screening.value = screeningRes.data

    const seatsRes = await axios.get(import.meta.env.VITE_API_BASE_URL + `/api/screenings/${screeningId}/seats`)
    console.log('Seats Data:', seatsRes.data)
    allSeats.value = seatsRes.data

    const availRes = await axios.get(import.meta.env.VITE_API_BASE_URL + `/api/screenings/${screeningId}/seats/available`)
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

const seatLabel = (id) => {
  const seat = allSeats.value.find(s => s.id === id)
  return seat?.seatNumber || id
}

const submitReservation = async () => {
  try {
    const payload = {
      screeningId: Number(screeningId),
      seatIds: Array.from(selectedSeats.value),
      userId: 1
    }

    await axios.post(import.meta.env.VITE_API_BASE_URL + '/api/reservations', payload)
    router.push('/reservations')
  } catch (err) {
    alert('Error creating reservation')
  }
}
</script>

<style scoped>
.screening-detail {
  max-width: 820px;
}

.back-link {
  background: none;
  border: none;
  color: var(--text-muted);
  font: inherit;
  font-size: 0.9rem;
  cursor: pointer;
  padding: 0;
  margin-bottom: 20px;
}

.back-link:hover {
  color: var(--text);
}

.detail-header {
  text-align: center;
}

.movie-info {
  margin-top: 8px;
  color: var(--text-muted);
  display: flex;
  justify-content: center;
  gap: 10px;
  flex-wrap: wrap;
}

.movie-info .movie-name {
  color: var(--text);
  font-weight: 600;
}

.dot {
  color: var(--accent);
}

/* ---------- Hall ---------- */
.hall {
  padding: 32px 24px 24px;
  margin-bottom: 20px;
}

.screen {
  position: relative;
  margin: 0 auto 40px;
  max-width: 480px;
  text-align: center;
}

.screen-curve {
  height: 36px;
  border-top: 4px solid var(--accent);
  border-radius: 50% 50% 0 0 / 100% 100% 0 0;
  box-shadow: 0 -14px 30px -10px rgba(229, 9, 20, 0.6);
  background: linear-gradient(to bottom, rgba(229, 9, 20, 0.12), transparent);
}

.screen-label {
  position: absolute;
  top: 12px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 0.7rem;
  letter-spacing: 6px;
  text-transform: uppercase;
  color: var(--text-muted);
}

.seat-grid {
  display: grid;
  grid-template-columns: repeat(10, 1fr);
  gap: 8px;
  max-width: 560px;
  margin: 0 auto;
}

.seat {
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px 8px 5px 5px;
  border: 1px solid var(--border);
  border-bottom-width: 4px;
  font: inherit;
  font-size: 0.72rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.12s, background-color 0.15s, border-color 0.15s, box-shadow 0.15s;
}

.seat.available {
  background: var(--bg-elev-2);
  color: var(--text-muted);
}

.seat.available:hover {
  transform: translateY(-2px);
  border-color: var(--text-muted);
  color: var(--text);
}

.seat.selected {
  background: var(--accent);
  border-color: #a5060e;
  color: #fff;
  box-shadow: 0 6px 16px rgba(229, 9, 20, 0.45);
  transform: translateY(-2px);
}

.seat.taken {
  background: transparent;
  border-style: dashed;
  border-bottom-width: 1px;
  color: #4a4f5c;
  cursor: not-allowed;
  text-decoration: line-through;
}

.seat:focus-visible {
  outline: 2px solid var(--gold);
  outline-offset: 2px;
}

/* ---------- Legend ---------- */
.legend {
  display: flex;
  justify-content: center;
  gap: 24px;
  margin-top: 32px;
  font-size: 0.85rem;
  color: var(--text-muted);
  flex-wrap: wrap;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.swatch {
  width: 16px;
  height: 16px;
  border-radius: 4px;
  border: 1px solid var(--border);
}

.swatch.available { background: var(--bg-elev-2); }
.swatch.selected { background: var(--accent); border-color: var(--accent); }
.swatch.taken { background: transparent; border-style: dashed; }

/* ---------- Summary ---------- */
.summary {
  padding: 18px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  position: sticky;
  bottom: 16px;
  box-shadow: var(--shadow);
}

.summary-label {
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: var(--text-muted);
}

.summary-seats {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 6px;
}

.summary-empty {
  color: var(--text-muted);
  font-size: 0.9rem;
}

.chip {
  background: var(--accent-soft);
  color: #ff6b72;
  border: 1px solid rgba(229, 9, 20, 0.35);
  padding: 2px 10px;
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: 600;
}

.summary-actions {
  display: flex;
  gap: 10px;
}

.count {
  background: rgba(255, 255, 255, 0.25);
  border-radius: 999px;
  padding: 0 8px;
  font-size: 0.8rem;
}

@media (max-width: 600px) {
  .hall {
    padding: 24px 14px 20px;
  }
  .seat-grid {
    gap: 4px;
  }
  .seat {
    font-size: 0.6rem;
    border-radius: 5px 5px 3px 3px;
    border-bottom-width: 3px;
  }
  .summary-actions {
    width: 100%;
  }
  .summary-actions .btn {
    flex: 1;
  }
}
</style>
