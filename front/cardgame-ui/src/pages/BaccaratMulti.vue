<template>
  <div class="vstack">
    <div class="panel">
      <div class="hstack" style="justify-content:space-between">
        <div class="badge info">Baccarat (Multi)</div>
        <div class="hstack">
          <input v-model="roomId" class="btn" placeholder="Room ID" style="width:130px"/>
          <select v-model="main" class="btn">
            <option value="PLAYER">Player</option>
            <option value="BANKER">Banker</option>
            <option value="TIE">Tie</option>
          </select>
          <input v-model.number="amount" class="btn" type="number" min="10" step="10" style="width:120px"/>
          <label class="hstack"><input type="checkbox" v-model="pairP"/> Player Pair</label>
          <label class="hstack"><input type="checkbox" v-model="pairB"/> Banker Pair</label>
          <label class="hstack"><input type="checkbox" v-model="super6"/> Super6</label>
          <button class="btn primary" @click="place">Place</button>
          <button class="btn" @click="deal">Deal</button>
          <button class="btn" @click="refresh">Refresh</button>
        </div>
      </div>
      <div class="panel" style="margin-top:10px">
        <div class="badge muted">Bets</div>
        <table class="panel" style="width:100%; margin-top:8px">
          <thead><tr><th>User</th><th>MAIN</th><th>PAIR_P</th><th>PAIR_B</th><th>SUPER6</th></tr></thead>
          <tbody>
            <tr v-for="(v,u) in ledger" :key="u">
              <td>{{ u }}</td>
              <td>{{ mainOf(v) }}</td>
              <td>{{ v.PAIR_P || 0 }}</td>
              <td>{{ v.PAIR_B || 0 }}</td>
              <td>{{ v.SUPER6 || 0 }}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-if="player || banker" class="table-board" style="margin-top:12px">
        <div class="badge muted">Player</div>
        <div class="hstack" style="justify-content:center">
          <CardImg v-for="(c,i) in (player?.cards||[])" :key="'p'+i" :rank="c.rank" :suit="c.suit" :delay="i*40"/>
        </div>
        <div class="badge muted" style="margin-top:10px">Banker</div>
        <div class="hstack" style="justify-content:center">
          <CardImg v-for="(c,i) in (banker?.cards||[])" :key="'b'+i" :rank="c.rank" :suit="c.suit" :delay="i*40"/>
        </div>
      </div>
      <div v-if="Object.keys(settle).length" class="panel" style="margin-top:12px">
        <div class="badge success">Settlement</div>
        <ul><li v-for="(v,u) in settle" :key="'s'+u">{{ u }}: <strong>{{ v>0? '+'+v : v }}</strong></li></ul>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import CardImg from '../components/CardImg.vue'
const roomId = ref(''); const main = ref('PLAYER'); const amount = ref(100)
const pairP = ref(false), pairB = ref(false), super6 = ref(false)
const ledger = ref({}); const player = ref(null); const banker = ref(null); const settle = ref({})
function mainOf(v){ const k=Object.keys(v||{}).find(x=>x.startsWith('MAIN_')); return k?`${k.replace('MAIN_','')}=${v[k]}`:'-' }
async function place(){
  const user = localStorage.getItem('userId')||'guest'
  const r = await fetch(`/api/baccarat/room/bet?roomId=${encodeURIComponent(roomId.value)}&user=${encodeURIComponent(user)}&main=${main.value}&amount=${amount.value}&pairP=${pairP.value}&pairB=${pairB.value}&super6=${super6.value}`, {method:'POST'})
  const d = await r.json(); apply(d.detail||d)
}
async function deal(){ const r=await fetch(`/api/baccarat/room/deal?roomId=${encodeURIComponent(roomId.value)}`,{method:'POST'}); const d=await r.json(); apply(d.detail||d) }
async function refresh(){ const r=await fetch(`/api/baccarat/room/state?roomId=${encodeURIComponent(roomId.value)}`); const d=await r.json(); apply(d.detail||d) }
function apply(s){ ledger.value=s.ledger||{}; player.value=s.player||null; banker.value=s.banker||null; settle.value=s.settle||{} }
</script>
