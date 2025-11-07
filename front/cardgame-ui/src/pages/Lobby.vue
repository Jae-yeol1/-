<template>
  <div class="vstack">
    <div class="panel">
      <div class="hstack" style="justify-content:space-between">
        <div class="badge">Welcome, {{ me }}</div>
        <div class="hstack">
          <router-link class="btn" to="/blackjack">Blackjack (Solo)</router-link>
          <router-link class="btn" to="/baccarat">Baccarat (Solo)</router-link>
          <router-link class="btn" to="/seven">Seven Poker</router-link>
          <router-link class="btn" to="/blackjack-multi">Blackjack (Multi)</router-link>
          <router-link class="btn" to="/baccarat-multi">Baccarat (Multi)</router-link>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { getUserId } from '../user'
const me = getUserId()
</script>
