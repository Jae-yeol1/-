<template>
  <div class="vstack">
    <div class="panel">
      <div class="hstack" style="justify-content:space-between">
        <div class="badge info">Baccarat (Solo)</div>
        <div class="hstack">
          <select v-model="main" class="btn">
            <option value="PLAYER">Player</option>
            <option value="BANKER">Banker</option>
            <option value="TIE">Tie</option>
          </select>
          <input v-model.number="amount" class="btn" type="number" min="10" step="10" style="width:120px"/>
          <label class="hstack"><input type="checkbox" v-model="pairP"/> Player Pair</label>
          <label class="hstack"><input type="checkbox" v-model="pairB"/> Banker Pair</label>
          <label class="hstack"><input type="checkbox" v-model="super6"/> Super6</label>
          <button class="btn primary" @click="betting">Bet+Deal</button>
          <button class="btn" @click="refresh">Refresh</button>
        </div>
      </div>
      <div class="table-board" style="margin-top:12px">
        <div class="badge muted">Player</div>
        <div class="hstack" style="justify-content:center">
          <CardImg v-for="(c,i) in (player?.cards||[])" :key="'p'+i" :rank="c.rank" :suit="c.suit" :delay="i*40"/>
        </div>
        <div class="badge muted" style="margin-top:10px">Banker</div>
        <div class="hstack" style="justify-content:center">
          <CardImg v-for="(c,i) in (banker?.cards||[])" :key="'b'+i" :rank="c.rank" :suit="c.suit" :delay="i*40"/>
        </div>
        <div v-if="settleTxt" class="badge success" style="margin-top:10px">{{ settleTxt }}</div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, computed } from 'vue'
import CardImg from '../components/CardImg.vue'
const main = ref('PLAYER'); const amount = ref(100)
const pairP = ref(false), pairB = ref(false), super6 = ref(false)
const player = ref(null), banker = ref(null), settle = ref(null)
async function betting(){
  await fetch(`/api/baccarat/solo/bet?main=${main.value}&amount=${amount.value}&pairP=${pairP.value}&pairB=${pairB.value}&super6=${super6.value}`, {method:'POST'})
  const r = await fetch('/api/baccarat/solo/deal',{method:'POST'})
  const d = await r.json(); apply(d.detail||d)
}
async function refresh(){ const r = await fetch('/api/baccarat/solo/state'); const d = await r.json(); apply(d.detail||d) }
function apply(s){ player.value=s.player; banker.value=s.banker; settle.value=s.settle }
const settleTxt = computed(()=> settle.value? JSON.stringify(settle.value):'')
</script>
