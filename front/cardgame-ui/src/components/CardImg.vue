<template>
  <div class="card3d" :class="{ flip: flipped && !isHidden }" :style="{ '--delay': `${delay}ms` }">
    <div class="face back"></div>
    <div class="face front">
      <img v-if="!isHidden" :src="src" :alt="alt" @error="fallback">
    </div>
  </div>
</template>
<script setup>
import { computed, ref, watchEffect } from 'vue'
const props = defineProps({ rank:String, suit:String, delay:{type:Number,default:0}, flipped:{type:Boolean,default:true}, size:{type:Number,default:1} })
const isHidden = computed(()=> ['BACK','HIDDEN'].includes((props.rank||'').toUpperCase()))
const rankMap = { ACE:'A', TWO:'2', THREE:'3', FOUR:'4', FIVE:'5', SIX:'6', SEVEN:'7', EIGHT:'8', NINE:'9', TEN:'10', JACK:'J', QUEEN:'Q', KING:'K' }
const suitMap = { HEARTS:'H', DIAMONDS:'D', CLUBS:'C', SPADES:'S', H:'H', D:'D', C:'C', S:'S' }
const code = computed(() => {
  const r=(props.rank||''), s=(props.suit||'')
  const rr=(rankMap[r.toUpperCase()]||r).replace(/^T$/,'10')
  const ss=(suitMap[s.toUpperCase()]||s).toUpperCase()
  return `${rr}_${ss}`
})
const src = ref(`/cards/${code.value}.png`)
const alt = computed(() => `${props.rank} of ${props.suit}`)
watchEffect(()=> { src.value = `/cards/${code.value}.png` })
function fallback(){
  const c=code.value
  const tries=[ `${c}.png`, `${c}.PNG`, `${c.replace('_','')}.png`, `${c.replace('_','')}.PNG`, `${c.replace('_','-')}.png`, `${c.replace('_','-')}.PNG` ]
  if(tries.length){ src.value = `/cards/${tries.shift()}` }
}
</script>
<style scoped>
.card3d{ width: calc(var(--card-w)*var(--s,1)); height: calc(var(--card-h)*var(--s,1)); perspective: 600px; display:inline-block; margin: 2px; animation: deal .4s ease var(--delay) both; position: relative; transform-style: preserve-3d; transition: transform .45s ease; --s: v-bind(size); }
.face{ width:100%; height:100%; position:absolute; top:0; left:0; backface-visibility: hidden; border-radius: 10px; overflow: hidden; box-shadow: 0 6px 14px rgba(0,0,0,.35); }
.front{ transform: rotateY(180deg); background:#fff; display:grid; place-items:center; }
.back{ background: radial-gradient(600px 300px at 50% -50%, #2753b6, #0e2a68); border:1px solid #0b2e66; }
img{ width:100%; height:100%; object-fit: cover; }
.card3d.flip{ transform: rotateY(180deg); }
@keyframes deal{ from{ transform: translateY(-14px); opacity:0; } to{ transform: translateY(0); opacity:1; } }
</style>
