
<template>
  <div class="container">
    <header class="hero">
      <img src="/assets/logo.png" class="logo" alt="Card Games"/>
    
      <h1>Card Games Deluxe</h1>
      <div class="balance">
        <img src="/assets/chip_100.png" class="chip sm" alt="100"/>
        <img src="/assets/chip_500.png" class="chip sm" alt="500"/>
        <img src="/assets/chip_1000.png" class="chip sm" alt="1000"/>
        <strong>Balance:</strong>
        <span :class="deltaFlashClass">{{ balance }}</span>
        <span v-if="lastDelta!==0" class="delta" :class="lastDelta>0?'up':'down'">
          {{ lastDelta>0?`+${lastDelta}`:lastDelta }}
        </span>
      </div>
    </header>

    <section class="session">
      <label>User ID <input v-model="userId" placeholder="your-id"/></label>
      <label>Init <input v-model.number="init" type="number" min="1"/></label>
      <button @click="startSession">Start</button>
      <button @click="getBalance">Refresh</button>
      <button class="ghost" @click="reset">Reset</button>
    </section>

    <nav class="tabs">
      <button :class="{active: tab==='bj'}" @click="tab='bj'">Blackjack</button>
      <button :class="{active: tab==='bac'}" @click="tab='bac'">Baccarat</button>
      <button :class="{active: tab==='sp'}" @click="tab='sp'">Seven Poker</button>
    </nav>

    <!-- BLACKJACK -->
    <section v-if="tab==='bj'" class="panel">
      <div class="controls">
        <label>Bet <input v-model.number="bet" type="number" min="1"/></label>
        <button @click="bjAuto">Auto Round</button>
        <button @click="bjStart">Start</button>
        <button :disabled="!can('HIT')" @click="bjAction('HIT')">Hit</button>
        <button :disabled="!can('STAND')" @click="bjAction('STAND')">Stand</button>
        <button :disabled="!can('DOUBLE')" @click="bjAction('DOUBLE')">Double</button>
        <button :disabled="!can('SPLIT')" @click="bjAction('SPLIT')">Split</button>
        <button :disabled="!can('SURRENDER')" @click="bjAction('SURRENDER')">Surrender</button>
      </div>
      <div v-if="bj.detail" class="round">
        <HandView :cards="bj.detail.dealer.cards"
                  :total="bj.detail.dealer.total"
                  :soft="bj.detail.dealer.soft"
                  :blackjack="bj.detail.dealer.blackjack"
                  :bust="bj.detail.dealer.bust">
          <template #title><h3>Dealer</h3></template>
        </HandView>

        <div class="player-hands">
          <HandView v-for="(h,i) in bj.detail.playerHands" :key="i"
                    :cards="h.cards" :total="h.total" :soft="h.soft"
                    :blackjack="h.blackjack" :bust="h.bust">
            <template #title>
              <h3>Player #{{ i+1 }}</h3>
              <span class="badge" v-if="bj.detail.activeHandIndex===i && bj.detail.inProgress">◀ ACTIVE</span>
            </template>
          </HandView>
        </div>

        <div class="outcome" :class="resultClass(bj.detail.outcome)" v-if="!bj.detail.inProgress && bj.detail.outcome">
          Outcome: {{ bj.detail.outcome }}
          <span class="small"> (Δ {{ bj.delta }})</span>
        </div>
      </div>
    </section>

    <!-- BACCARAT -->
    <section v-else-if="tab==='bac'" class="panel">
      <div class="controls">
        <label>Bet <input v-model.number="bet" type="number" min="1"/></label>
        <select v-model="bacTarget">
          <option>PLAYER</option>
          <option>BANKER</option>
          <option>TIE</option>
        </select>
        <button @click="bacRound">Play</button>
      </div>
      <div v-if="bac.detail" class="round">
        <HandView :cards="bac.detail.player.cards">
          <template #title>
            <h3>Player</h3>
            <span class="badge">Total: {{ bac.detail.player.total }}</span>
            <span class="badge" v-if="bac.detail.player.natural">NATURAL</span>
            <span class="badge" v-if="bac.detail.player.drewThird">3rd Draw</span>
          </template>
        </HandView>

        <HandView :cards="bac.detail.banker.cards">
          <template #title>
            <h3>Banker</h3>
            <span class="badge">Total: {{ bac.detail.banker.total }}</span>
            <span class="badge" v-if="bac.detail.banker.natural">NATURAL</span>
            <span class="badge" v-if="bac.detail.banker.drewThird">3rd Draw</span>
          </template>
        </HandView>

        <div class="outcome" :class="resultClass(bac.detail.outcome)">
          Outcome: {{ bac.detail.outcome }}
          <span class="small"> (BetOn {{ bac.detail.betOn }} / Δ {{ bac.delta }})</span>
        </div>
      </div>
    </section>

    <!-- SEVEN POKER -->
    <section v-else class="panel">
      <div class="controls">
        <label>Bet <input v-model.number="bet" type="number" min="1"/></label>
        <button @click="spRound">Play</button>
      </div>
      <div v-if="sp.detail" class="round">
        <div class="two-col">
          <div>
            <h3>Player 7</h3>
            <div class="cards">
              <CardImg v-for="(c,i) in sp.detail.player.cards7" :key="i" :rank="c.rank" :suit="c.suit" :delay="i*80"/>
            </div>
            <p class="best">Best5: {{ sp.detail.player.best5Rank }}</p>
            <div class="cards">
              <CardImg v-for="(c,i) in sp.detail.player.best5" :key="'pb'+i" :rank="c.rank" :suit="c.suit" :delay="i*60"/>
            </div>
          </div>
          <div>
            <h3>Dealer 7</h3>
            <div class="cards">
              <CardImg v-for="(c,i) in sp.detail.dealer.cards7" :key="i" :rank="c.rank" :suit="c.suit" :delay="i*80"/>
            </div>
            <p class="best">Best5: {{ sp.detail.dealer.best5Rank }}</p>
            <div class="cards">
              <CardImg v-for="(c,i) in sp.detail.dealer.best5" :key="'db'+i" :rank="c.rank" :suit="c.suit" :delay="i*60"/>
            </div>
          </div>
        </div>
        <div class="outcome" :class="resultClass(sp.detail.outcome)">
          Outcome: {{ sp.detail.outcome }}
          <span class="small"> (Δ {{ sp.delta }})</span>
        </div>
      </div>
    </section>

    <section class="history" v-if="history.length">
      <h2>Round History</h2>
      <ol>
        <li v-for="(h,i) in history" :key="i">
          <span class="game">{{ h.game }}</span>
          <span :class="h.delta>0?'up':'down'">{{ h.delta>0?`+${h.delta}`:h.delta }}</span>
          <span class="bal">→ {{ h.balance }}</span>
        </li>
      </ol>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import HandView from './components/HandView.vue'
import CardImg from './components/CardImg.vue'

const API_BASE = 'http://localhost:8080'
const api = (path, params={}, method='POST') => {
  const q = new URLSearchParams(params).toString()
  const url = `${API_BASE}${path}?${q}`
  return fetch(url, { method }).then(r=>r.json())
}
const get = (path, params={}) => api(path, params, 'GET')

const userId = ref('jaeu')
const init = ref(1000)
const balance = ref(0)
const bet = ref(100)
const tab = ref('bj')

const lastDelta = ref(0)
const deltaFlashClass = ref('')
const history = ref([])

const bj = ref({})
const bacTarget = ref('PLAYER')
const bac = ref({})
const sp = ref({})

function flashDelta(d){
  lastDelta.value = d
  deltaFlashClass.value = d>0 ? 'pulse up' : (d<0 ? 'pulse down' : '')
  setTimeout(()=> deltaFlashClass.value = '', 600)
}
function pushHistory(game, delta, balanceNow){
  history.value.unshift({ game, delta, balance: balanceNow })
  if (history.value.length > 20) history.value.pop()
}

async function startSession(){
  const res = await api('/api/session/start', { userId: userId.value, init: init.value })
  balance.value = res.balance ?? 0
  lastDelta.value = 0
}
async function getBalance(){
  const res = await get('/api/session/balance', { userId: userId.value })
  balance.value = res.balance ?? 0
  lastDelta.value = 0
}
async function reset(){
  const res = await api('/api/session/reset', { userId: userId.value })
  balance.value = res.balance ?? 0
  history.value = []
  lastDelta.value = 0
}

// Blackjack auto
async function bjAuto(){
  const res = await api('/api/blackjack/round', { userId: userId.value, bet: bet.value })
  bj.value = res
  if (res?.ok){ balance.value = res.balance; flashDelta(res.delta||0); pushHistory('BJ', res.delta||0, res.balance) }
}

// Blackjack action flow
async function bjStart(){
  const res = await api('/api/blackjack/start', { userId: userId.value, bet: bet.value })
  bj.value = res
}
async function bjAction(type){
  const res = await api('/api/blackjack/action', { userId: userId.value, type })
  bj.value = res
  if (res?.ok && !res.detail?.inProgress){
    balance.value = res.balance
    flashDelta(res.delta||0)
    pushHistory('BJ', res.delta||0, res.balance)
  }
}
function can(kind){
  const d = bj.value?.detail
  if (!d) return false
  const map = { HIT: d.canHit, STAND: d.canStand, DOUBLE: d.canDouble, SPLIT: d.canSplit, SURRENDER: d.canSurrender }
  return !!map[kind]
}

// Baccarat
async function bacRound(){
  const res = await api('/api/baccarat/round', { userId: userId.value, bet: bet.value, target: bacTarget.value })
  bac.value = res
  if (res?.ok){ balance.value = res.balance; flashDelta(res.delta||0); pushHistory('BAC', res.delta||0, res.balance) }
}

// Seven Poker
async function spRound(){
  const res = await api('/api/sevenpoker/round', { userId: userId.value, bet: bet.value })
  sp.value = res
  if (res?.ok){ balance.value = res.balance; flashDelta(res.delta||0); pushHistory('SP', res.delta||0, res.balance) }
}

function resultClass(outcome){
  return {
    win: outcome && (outcome.includes('PLAYER_WIN') || outcome==='PLAYER' || outcome==='BLACKJACK'),
    lose: outcome && (outcome.includes('DEALER_WIN') || outcome==='DEALER' || outcome==='BUST'),
    push: outcome && (outcome.includes('PUSH') || outcome==='TIE')
  }
}
</script>

<style>
*{ box-sizing: border-box; }
.container{ max-width: 1120px; margin: 24px auto; padding: 0 16px; font-family: ui-sans-serif, system-ui, -apple-system, Segoe UI, Roboto, "Noto Sans KR", sans-serif; }
header{ display:flex; align-items:center; justify-content: space-between; margin-bottom: 10px; }
h1{ margin: 0 0 6px; font-size: 24px; }
.balance{ font-size: 16px; display:flex; align-items:center; gap:8px; }
.balance .delta{ font-weight:700; }
.balance .up{ color:#11831b; }
.balance .down{ color:#b01e1e; }
.pulse{ animation: pulse .6s ease; }
@keyframes pulse{ from{ transform:scale(1.05);} to{ transform:scale(1);} }

.session{ display:flex; gap: 10px; align-items:center; flex-wrap: wrap; margin: 12px 0 16px; }
.session input{ width: 140px; padding:6px 8px; }
button{ padding:6px 10px; border:1px solid #ddd; border-radius:8px; background:#fff; cursor:pointer; }
button:hover{ background:#f6f6f6; }
button.ghost{ background:#f7f7f7; }

.tabs{ display:flex; gap:6px; margin: 8px 0 12px; }
.tabs button.active{ background:#111; color:#fff; border-color:#111; }

.panel{ border:1px solid #eee; border-radius: 12px; padding: 14px; background:#fff; }
.controls{ display:flex; gap:10px; align-items:center; flex-wrap: wrap; margin-bottom: 12px; }
.controls input, .controls select{ padding:6px 8px; }

.round{ display: grid; gap: 12px; }
.player-hands{ display:grid; gap:10px; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); }

.outcome{ font-weight:700; padding:8px 10px; border-radius: 8px; width: fit-content; }
.outcome.win{ background:#ecffec; color:#0b7a0b; }
.outcome.lose{ background:#fff1f0; color:#a8071a; }
.outcome.push{ background:#f6ffed; color:#3f6600; }
.small{ font-weight:400; opacity:.8; margin-left:6px; }

.history{ margin-top: 18px; }
.history ol{ padding-left: 18px; }
.history .game{ display:inline-block; min-width: 36px; font-weight:700; }
.history .up{ color:#0b7a0b; }
.history .down{ color:#a8071a; }

/* hero header */
.hero{ position:relative; margin-bottom: 12px; }
.logo{ width: 100%; max-width: 920px; display:block; margin: 0 auto; border-radius: 12px; box-shadow: 0 8px 22px rgba(0,0,0,.35); }
/* chips */
.chip.sm{ width:28px; height:28px; margin-right:6px; vertical-align: middle; }
.container::after{
  content:""; position: fixed; inset:0;
  background: url("/assets/vignette.png") center/cover no-repeat;
  pointer-events: none;
}

</style>
