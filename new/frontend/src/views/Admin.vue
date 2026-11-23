<template>
  <div class="page">
    <header class="page-header">
      <h1 class="page-title">Admin</h1>
      <p class="page-subtitle">Manage all reservations.</p>
    </header>

    <div class="stats">
      <div class="stat card">
        <span class="stat-label">Total</span>
        <span class="stat-value">{{ reservations.length }}</span>
      </div>
      <div class="stat card">
        <span class="stat-label">Pending</span>
        <span class="stat-value warning">{{ countBy('PENDING') }}</span>
      </div>
      <div class="stat card">
        <span class="stat-label">Reserved</span>
        <span class="stat-value success">{{ countBy('RESERVED') }}</span>
      </div>
      <div class="stat card">
        <span class="stat-label">Cancelled</span>
        <span class="stat-value danger">{{ countBy('CANCELLED') }}</span>
      </div>
    </div>

    <div class="table-wrap card">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Contact</th>
            <th>Screening ID</th>
            <th>Status</th>
            <th class="col-actions">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="reservations.length === 0">
            <td colspan="5" class="empty">No reservations yet.</td>
          </tr>
          <tr v-for="res in reservations" :key="res.id">
            <td class="mono">#{{ res.id }}</td>
            <td>{{ res.contactEmail }}</td>
            <td class="mono">{{ res.screeningId }}</td>
            <td>
              <span :class="['badge', `badge-${(res.status || '').toLowerCase()}`]">{{ res.status }}</span>
            </td>
            <td class="col-actions">
              <button
                v-if="res.status !== 'CANCELLED'"
                class="btn btn-danger btn-sm"
                @click="cancelReservation(res.id)"
              >
                Cancel
              </button>
              <span v-else class="muted">—</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
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

// --- presentation helper ---
const countBy = (status) => reservations.value.filter(r => r.status === status).length
</script>

<style scoped>
.stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  margin-bottom: 24px;
}

.stat {
  padding: 16px 18px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-label {
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: var(--text-muted);
}

.stat-value {
  font-family: var(--font-display);
  font-size: 2.2rem;
  line-height: 1;
}

.stat-value.warning { color: var(--warning); }
.stat-value.success { color: var(--success); }
.stat-value.danger { color: var(--danger); }

.table-wrap {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.92rem;
}

th,
td {
  padding: 14px 18px;
  text-align: left;
  border-bottom: 1px solid var(--border);
  white-space: nowrap;
}

th {
  font-size: 0.72rem;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: var(--text-muted);
  font-weight: 600;
  background: var(--bg-elev-2);
}

tbody tr {
  transition: background-color 0.12s;
}

tbody tr:hover {
  background: rgba(255, 255, 255, 0.025);
}

tbody tr:last-child td {
  border-bottom: none;
}

.col-actions {
  text-align: right;
}

.mono {
  font-variant-numeric: tabular-nums;
  color: var(--text-muted);
}

.muted {
  color: var(--text-muted);
}

.empty {
  text-align: center;
  color: var(--text-muted);
  padding: 40px;
}

@media (max-width: 700px) {
  .stats {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
