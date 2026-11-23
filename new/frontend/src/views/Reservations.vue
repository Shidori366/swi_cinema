<template>
  <div class="page">
    <header class="page-header">
      <h1 class="page-title">My Reservations</h1>
      <p class="page-subtitle">Pay for pending bookings or cancel the ones you no longer need.</p>
    </header>

    <div v-if="reservations.length === 0" class="state card">
      <div class="state-icon">🎟️</div>
      <p>No reservations found.</p>
      <router-link to="/" class="btn btn-primary">Browse screenings</router-link>
    </div>

    <div v-else class="list">
      <article v-for="res in reservations" :key="res.id" class="ticket card">
        <div class="ticket-stub">
          <span class="stub-label">Ticket</span>
          <span class="stub-id">#{{ res.id }}</span>
        </div>
        <div class="ticket-body">
          <div class="ticket-info">
            <span :class="['badge', `badge-${(res.status || '').toLowerCase()}`]">{{ res.status }}</span>
            <div class="ticket-meta">
              <span v-if="res.screeningId">Screening #{{ res.screeningId }}</span>
              <span v-if="res.seatIds && res.seatIds.length">
                {{ res.seatIds.length }} seat{{ res.seatIds.length === 1 ? '' : 's' }}
              </span>
              <span v-if="res.createdAt">Booked {{ formatDateTime(res.createdAt) }}</span>
            </div>
          </div>
          <div class="actions">
            <button
              v-if="res.status === 'PENDING'"
              @click="payReservation(res.id)"
              class="btn btn-success btn-sm"
            >
              Pay
            </button>
            <button
              v-if="res.status !== 'CANCELLED'"
              @click="cancelReservation(res.id)"
              class="btn btn-danger btn-sm"
            >
              Cancel
            </button>
          </div>
        </div>
      </article>
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

// --- presentation helper ---
const formatDateTime = (value) => {
  const d = new Date(value)
  if (isNaN(d)) return value
  return d.toLocaleString(undefined, { day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit' })
}
</script>

<style scoped>
.state .btn {
  margin-top: 16px;
}

.list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.ticket {
  display: flex;
  overflow: hidden;
  transition: border-color 0.15s;
}

.ticket:hover {
  border-color: #3a3f4c;
}

.ticket-stub {
  position: relative;
  min-width: 110px;
  padding: 18px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: var(--bg-elev-2);
  border-right: 2px dashed var(--border);
}

/* ticket notches */
.ticket-stub::before,
.ticket-stub::after {
  content: '';
  position: absolute;
  right: -10px;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: var(--bg);
  border: 1px solid var(--border);
}

.ticket-stub::before { top: -10px; }
.ticket-stub::after { bottom: -10px; }

.stub-label {
  font-size: 0.7rem;
  text-transform: uppercase;
  letter-spacing: 2px;
  color: var(--text-muted);
}

.stub-id {
  font-family: var(--font-display);
  font-size: 2rem;
  letter-spacing: 1px;
  line-height: 1.1;
}

.ticket-body {
  flex: 1;
  padding: 18px 22px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.ticket-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-start;
}

.ticket-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 4px 16px;
  color: var(--text-muted);
  font-size: 0.88rem;
}

.actions {
  display: flex;
  gap: 8px;
}

@media (max-width: 600px) {
  .ticket-stub {
    min-width: 80px;
    padding: 12px;
  }
  .stub-id {
    font-size: 1.6rem;
  }
}
</style>
