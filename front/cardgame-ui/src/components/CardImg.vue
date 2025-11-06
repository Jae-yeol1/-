
<template>
  <div class="card3d" :class="{ flip: flipped }" :style="{ '--delay': `${delay}ms` }">
    <div class="face back"></div>
    <div class="face front">
      <img :src="src" :alt="alt" @error="onErr">
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watchEffect } from 'vue'
const props = defineProps({
  rank: String,
  suit: String,
  delay: { type: Number, default: 0 },
  flipped: { type: Boolean, default: true }
})

const rankMap = { ACE:'A', TWO:'2', THREE:'3', FOUR:'4', FIVE:'5', SIX:'6', SEVEN:'7', EIGHT:'8', NINE:'9', TEN:'T', JACK:'J', QUEEN:'Q', KING:'K' }
const suitMap = { HEARTS:'H', DIAMONDS:'D', CLUBS:'C', SPADES:'S' }
const code = computed(() => `${rankMap[props.rank] || props.rank}_${suitMap[props.suit] || props.suit}`)
const src = ref(`/cards/${code.value}.png`)
const alt = computed(() => `${props.rank} of ${props.suit}`)
watchEffect(()=> { src.value = `/cards/${code.value}.png` })
function onErr(){ /* fallback could swap to vector but assets are provided */ }
</script>

<style scoped>
.card3d{
  width: 70px; height: 100px;
  perspective: 600px;
  display:inline-block;
  margin: 2px;
  animation: deal .4s ease var(--delay) both;
}
.face{
  width:100%; height:100%;
  position:absolute; top:0; left:0;
  backface-visibility: hidden;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 3px 8px rgba(0,0,0,.18);
}
.front{ transform: rotateY(180deg); background:#fff; display:grid; place-items:center; }
.back{ background: linear-gradient(135deg,#0e3a7c,#1d57b6); border:1px solid #0b2e66; }
img{ width:100%; height:100%; object-fit: cover; }
.card3d{ position: relative; transform-style: preserve-3d; transition: transform .45s ease; }
.card3d.flip{ transform: rotateY(180deg); }

@keyframes deal{ from{ transform: translateY(-14px); opacity:0; } to{ transform: translateY(0); opacity:1; } }
</style>
