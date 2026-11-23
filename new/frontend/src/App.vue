<template>
  <div id="app">
    <nav>
      <router-link to="/">Home</router-link> |
      <router-link to="/reservations">My Reservations</router-link> |
      <router-link to="/admin">Admin</router-link>
    </nav>
    <hr />
    <router-view></router-view>
  </div>
</template>

<style>
nav { padding: 20px; }
</style>
