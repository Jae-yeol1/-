<template>
  <div class="app-root">
    <header class="topbar">
      <div class="brand">CardGame</div>
      <nav class="nav">
        <router-link to="/lobby">Lobby</router-link>
        <router-link to="/blackjack">Blackjack</router-link>
        <router-link to="/baccarat">Baccarat</router-link>
        <router-link to="/seven">Seven</router-link>
        <router-link to="/blackjack-multi">BJ Multi</router-link>
        <router-link to="/baccarat-multi">Bac Multi</router-link>
      </nav>
    </header>
    <main class="page"><router-view /></main>
  </div>
</template>
<script setup></script>
<style>
.app-root{ min-height:100vh; background:#0d1220; color:#e9eef7; }
.topbar{ display:flex; align-items:center; justify-content:space-between; padding:10px 16px; background:linear-gradient(180deg,#0f1730,#0a1023); border-bottom:1px solid rgba(255,255,255,.08); position:sticky; top:0; }
.brand{ font-weight:800; letter-spacing:.5px; }
.nav a{ color:#cfd8ff; margin:0 8px; text-decoration:none; }
.nav a.router-link-active{ color:#fff; font-weight:700; }
.page{ padding:16px; }
</style>
