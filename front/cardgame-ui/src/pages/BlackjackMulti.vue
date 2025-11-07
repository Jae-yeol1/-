<template>
  <div class="vstack">
    <div class="panel">
      <div class="hstack" style="justify-content:space-between">
        <div class="badge info">Blackjack (Multi)</div>
        <div class="hstack">
          <input v-model="roomId" class="btn" placeholder="Room ID" style="width:130px"/>
          <input v-model.number="bet" class="btn" type="number" min="10" step="10" placeholder="Bet" style="width:120px"/>
          <button class="btn primary" @click="start">Start</button>
          <button class="btn" @click="refresh">Refresh</button>
        </div>
      </div>
      <div class="table-board" style="margin-top:12px">
        <div class="badge muted">Dealer</div>
        <div class="hstack" style="justify-content:center">
          <CardImg v-for="(c,i) in dealer.cards" :key="'d'+i" :rank="c.rank" :suit="c.suit" :delay="i*40"/>
        </div>
        <div class="grid" style="display:grid;grid-template-columns:repeat(auto-fit,minmax(280px,1fr));gap:12px;margin-top:14px">
          <div v-for="h in hands" :key="h.user" class="panel">
            <div class="hstack" style="justify-content:space-between">
              <div class="badge">{{ h.user }}</div>
              <div class="badge info">Total: {{ h.total ?? '??' }}</div>
            </div>
            <div class="hstack" style="justify-content:center">
              <CardImg v-for="(c,i) in h.cards" :key="h.user+'_'+i" :rank="c.rank" :suit="c.suit" :delay="i*30"/>
            </div>
            <div v-if="h.user===me && inProgress" class="hstack" style="margin-top:8px">
              <button class="btn primary" @click="hit">Hit</button>
              <button class="btn" @click="stand">Stand</button>
            </div>
          </div>
        </div>
        <div v-if="!inProgress" class="badge success" style="margin-top:10px">Round finished</div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import CardImg from '../components/CardImg.vue'
import { getUserId } from '../user'
const me = getUserId()
const roomId = ref(''); const bet = ref(100)
const inProgress = ref(false); const dealer = ref({cards:[]}); const hands = ref([])
async function start(){
  const r = await fetch(`/api/blackjack/room/start?roomId=${encodeURIComponent(roomId.value)}&host=${encodeURIComponent(me)}&bet=${bet.value}`, {method:'POST'})
  const d = await r.json(); apply(d.detail||d)
}
async function refresh(){
  const r = await fetch(`/api/blackjack/room/state?roomId=${encodeURIComponent(roomId.value)}&viewer=${encodeURIComponent(me)}`)
  const d = await r.json(); apply(d.detail||d)
}
async function hit(){ await fetch(`/api/blackjack/room/hit?roomId=${encodeURIComponent(roomId.value)}&user=${encodeURIComponent(me)}`, {method:'POST'}); await refresh() }
async function stand(){ await fetch(`/api/blackjack/room/stand?roomId=${encodeURIComponent(roomId.value)}&user=${encodeURIComponent(me)}`, {method:'POST'}); await refresh() }
function apply(s){ inProgress.value=!!s.inProgress; dealer.value=s.dealer||{cards:[]}; hands.value=s.hands||[] }
</script>
