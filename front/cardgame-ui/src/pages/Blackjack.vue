<template>
  <div class="vstack">
    <div class="panel">
      <div class="hstack" style="justify-content:space-between">
        <div class="badge info">Blackjack (Solo)</div>
        <div class="hstack">
          <input class="btn" v-model.number="bet" type="number" min="10" step="10" style="width:120px"/>
          <button class="btn primary" @click="start">Start</button>
          <button class="btn" @click="refresh">Refresh</button>
        </div>
      </div>
      <div class="table-board" style="margin-top:12px">
        <div class="badge muted">Dealer</div>
        <div class="hstack" style="justify-content:center">
          <CardImg v-for="(c,i) in dealer.cards" :key="'d'+i" :rank="c.rank" :suit="c.suit" :delay="i*40"/>
        </div>
        <div class="badge muted" style="margin-top:12px">You</div>
        <div class="hstack" style="justify-content:center">
          <CardImg v-for="(c,i) in player.cards" :key="'p'+i" :rank="c.rank" :suit="c.suit" :delay="i*30"/>
        </div>
        <div class="hstack" v-if="inProgress" style="justify-content:center;margin-top:10px">
          <button class="btn primary" @click="hit">Hit</button>
          <button class="btn" @click="stand">Stand</button>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import CardImg from '../components/CardImg.vue'
const bet = ref(100)
const dealer = ref({cards:[]})
const player = ref({cards:[]})
const inProgress = ref(false)
async function start(){ const r=await fetch('/api/blackjack/solo/start?bet='+bet.value,{method:'POST'}); const d=await r.json(); apply(d.detail||d) }
async function refresh(){ const r=await fetch('/api/blackjack/solo/state'); const d=await r.json(); apply(d.detail||d) }
async function hit(){ await fetch('/api/blackjack/solo/hit',{method:'POST'}); await refresh() }
async function stand(){ await fetch('/api/blackjack/solo/stand',{method:'POST'}); await refresh() }
function apply(s){ inProgress.value=!!s.inProgress; dealer.value=s.dealer||{cards:[]}; player.value=s.player||{cards:[]} }
</script>
