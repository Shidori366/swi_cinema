<template>
  <div class="reservation-form">
    <h2>Create Reservation</h2>
    <p>Selected Screening ID: {{ screeningId }}</p>
    <p>Selected Seats: {{ seatIds }}</p>
    <button @click="submitReservation">Submit Reservation</button>
    <button @click="$emit('close')">Cancel</button>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps(['screeningId', 'seatIds'])
const emit = defineEmits(['close'])

const submitReservation = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/reservations', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        screeningId: props.screeningId,
        seatIds: props.seatIds
      })
    })
    if (response.ok) {
      alert('Reservation successful!')
      emit('close')
    }
  } catch (e) {
    console.error(e)
    alert('Error creating reservation')
  }
}
</script>
