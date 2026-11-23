<template>
  <div class="app-shell">
    <header class="topbar">
      <div class="topbar-inner">
        <router-link to="/" class="brand">
          <span class="brand-icon">🎬</span>
          <span class="brand-name">Cinema</span>
        </router-link>
        <nav class="nav">
          <router-link to="/" class="nav-link" exact-active-class="active">Screenings</router-link>
          <router-link to="/reservations" class="nav-link" active-class="active">My Reservations</router-link>
          <router-link to="/admin" class="nav-link" active-class="active">Admin</router-link>
        </nav>
      </div>
    </header>
    <main>
      <router-view></router-view>
    </main>
  </div>
</template>

<style scoped>
.topbar {
  position: sticky;
  top: 0;
  z-index: 10;
  background: rgba(14, 15, 19, 0.8);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--border);
}

.topbar-inner {
  max-width: 1100px;
  margin: 0 auto;
  padding: 14px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand-icon {
  font-size: 1.4rem;
}

.brand-name {
  font-family: var(--font-display);
  font-size: 1.8rem;
  letter-spacing: 2px;
  color: var(--accent);
  line-height: 1;
}

.nav {
  display: flex;
  gap: 4px;
  background: var(--bg-elev);
  border: 1px solid var(--border);
  padding: 4px;
  border-radius: 999px;
}

.nav-link {
  padding: 7px 16px;
  border-radius: 999px;
  font-size: 0.9rem;
  font-weight: 500;
  color: var(--text-muted);
  transition: color 0.15s, background-color 0.15s;
}

.nav-link:hover {
  color: var(--text);
}

.nav-link.active {
  background: var(--accent);
  color: #fff;
}

@media (max-width: 600px) {
  .topbar-inner {
    flex-direction: column;
    gap: 10px;
    padding: 12px 16px;
  }
  .nav-link {
    padding: 6px 12px;
    font-size: 0.82rem;
  }
}
</style>
