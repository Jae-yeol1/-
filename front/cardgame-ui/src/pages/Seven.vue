<template>
  <div class="panel">
    <div class="hstack" style="justify-content:space-between">
      <div class="badge info">Seven Poker</div>
      <div class="hstack">
        <input v-model="roomId" class="btn" placeholder="Room ID" style="width:130px"/>
        <input v-model.number="ante" class="btn" type="number" min="10" step="10" placeholder="Ante" style="width:120px"/>
        <input v-model="users" class="btn" placeholder="me,ai1,ai2" style="width:200px"/>
        <button class="btn primary" @click="start">Start</button>
        <button class="btn" @click="next">Next</button>
        <button class="btn" @click="refresh">State</button>
      </div>
    </div>
    <div class="vstack" style="margin-top:10px">
      <div v-for="p in players" :key="p.user" class="panel">
        <div class="badge">{{ p.user }}</div>
        <div class="hstack" style="justify-content:center">
          <CardImg v-for="(c,i) in p.cards" :key="p.user+'_'+i" :rank="c.rank" :suit="c.suit" :delay="i*30"/>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import CardImg from '../components/CardImg.vue'
const roomId=ref('room1'), ante=ref(50), users=ref('me,ai1,ai2')
const players=ref([])
async function start(){
  await fetch(`/api/seven/start?roomId=${encodeURIComponent(roomId.value)}&users=${encodeURIComponent(users.value)}&ante=${ante.value}`,{method:'POST'})
  await refresh()
}
async function next(){ await fetch(`/api/seven/next?roomId=${encodeURIComponent(roomId.value)}`,{method:'POST'}); await refresh() }
async function refresh(){
  const r = await fetch(`/api/seven/state?roomId=${encodeURIComponent(roomId.value)}&viewer=${encodeURIComponent('me')}`)
  const d = await r.json(); const s=d.detail||d; players.value=s.players||[]
}
</script>
