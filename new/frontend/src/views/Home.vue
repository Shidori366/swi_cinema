<template>
  <div class="page">
    <header class="page-header">
      <h1 class="page-title">Now Showing</h1>
      <p class="page-subtitle">Pick a showtime and choose your seats.</p>
    </header>

    <div v-if="screenings.length === 0" class="state">
      <div class="state-icon">🎞️</div>
      <p>No screenings available right now.</p>
    </div>

    <div v-else class="grid">
      <article v-for="movie in movies" :key="movie.name" class="movie-card card">
        <div class="poster" :style="{ background: posterGradient(movie.name) }">
          <span class="poster-initial">{{ (movie.name || '?').charAt(0) }}</span>
          <span class="price-tag">from {{ movie.minPrice }} {{ currency }}</span>
        </div>
        <div class="card-body">
          <h3 class="movie-name">{{ movie.name }}</h3>
          <span class="showtimes-label">
            {{ movie.screenings.length }} showtime{{ movie.screenings.length === 1 ? '' : 's' }}
          </span>
          <div class="showtimes">
            <button
              v-for="screening in movie.screenings"
              :key="screening.id"
              class="showtime"
              @click="goToDetail(screening.id)"
            >
              <span class="showtime-date">{{ formatDate(screening.time) }}</span>
              <span class="showtime-time">{{ formatTime(screening.time) }}</span>
              <span class="showtime-meta">{{ screening.roomName }} · {{ screening.price }} {{ currency }}</span>
            </button>
          </div>
        </div>
      </article>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
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

// --- presentation helpers ---
// One card per movie, showtimes sorted chronologically.
const movies = computed(() => {
  const byName = new Map()
  const sorted = [...screenings.value].sort((a, b) => new Date(a.time) - new Date(b.time))
  for (const s of sorted) {
    if (!byName.has(s.movieName)) byName.set(s.movieName, { name: s.movieName, screenings: [] })
    byName.get(s.movieName).screenings.push(s)
  }
  return [...byName.values()].map(m => ({
    ...m,
    minPrice: Math.min(...m.screenings.map(s => Number(s.price))),
  }))
})

const posterGradient = (name = '') => {
  let hash = 0
  for (const ch of name) hash = (hash * 31 + ch.charCodeAt(0)) | 0
  const hue = Math.abs(hash) % 360
  return `linear-gradient(135deg, hsl(${hue} 60% 28%), hsl(${(hue + 40) % 360} 70% 12%))`
}

const formatDate = (value) => {
  if (!value) return '—'
  const d = new Date(value)
  if (isNaN(d)) return value
  return d.toLocaleDateString(undefined, { weekday: 'short', day: 'numeric', month: 'short' })
}

const formatTime = (value) => {
  if (!value) return '—'
  const d = new Date(value)
  if (isNaN(d)) return ''
  return d.toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' })
}
</script>

<style scoped>
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.movie-card {
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transition: box-shadow 0.2s, border-color 0.2s;
}

.movie-card:hover {
  box-shadow: var(--shadow);
  border-color: #3a3f4c;
}

.poster {
  position: relative;
  aspect-ratio: 16 / 9;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.poster::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, var(--bg-elev) 0%, transparent 60%);
}

.poster-initial {
  font-family: var(--font-display);
  font-size: 6rem;
  color: rgba(255, 255, 255, 0.18);
  line-height: 1;
  transition: transform 0.3s;
}

.movie-card:hover .poster-initial {
  transform: scale(1.08);
}

.price-tag {
  position: absolute;
  top: 12px;
  right: 12px;
  z-index: 1;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(6px);
  color: var(--gold);
  font-weight: 700;
  font-size: 0.8rem;
  padding: 4px 10px;
  border-radius: 999px;
}

.card-body {
  padding: 4px 18px 18px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex: 1;
}

.movie-name {
  font-size: 1.25rem;
  font-weight: 700;
}

.showtimes-label {
  font-size: 0.72rem;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: var(--text-muted);
}

.showtimes {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(115px, 1fr));
  gap: 8px;
}

.showtime {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 1px;
  padding: 10px 12px;
  background: var(--bg-elev-2);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  color: var(--text);
  font: inherit;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.15s, background-color 0.15s, transform 0.1s;
}

.showtime:hover {
  border-color: var(--accent);
  background: var(--accent-soft);
  transform: translateY(-2px);
}

.showtime:focus-visible {
  outline: 2px solid var(--accent);
  outline-offset: 2px;
}

.showtime-date {
  font-size: 0.75rem;
  color: var(--text-muted);
}

.showtime-time {
  font-size: 1.15rem;
  font-weight: 700;
}

.showtime-meta {
  font-size: 0.72rem;
  color: var(--text-muted);
}
</style>
