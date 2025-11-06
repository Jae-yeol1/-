
<template>
  <div class="hand">
    <div class="meta">
      <slot name="title"></slot>
      <span class="badge" v-if="typeof total==='number'">Total: {{ total }}</span>
      <span class="badge soft" v-if="soft">SOFT</span>
      <span class="badge bj" v-if="blackjack">BLACKJACK</span>
      <span class="badge bust" v-if="bust">BUST</span>
      <slot name="extra"></slot>
    </div>
    <div class="cards">
      <CardImg v-for="(c,i) in cards" :key="i" :rank="c.rank" :suit="c.suit" :delay="i*100" :flipped="true"/>
    </div>
  </div>
</template>
<script setup>
import CardImg from './CardImg.vue'
defineProps({ cards:Array, total:Number, soft:Boolean, blackjack:Boolean, bust:Boolean })
</script>
<style scoped>
.hand{ padding:10px 12px; background:#fafafa; border:1px solid #eee; border-radius:12px; }
.meta{ display:flex; align-items:center; gap:6px; margin-bottom:8px; flex-wrap: wrap; }
.badge{ font-size:12px; background:#eee; padding:3px 8px; border-radius:999px; }
.badge.soft{ background:#e6f4ff; color:#0958d9; }
.badge.bj{ background:#ecffec; color:#0b7a0b; }
.badge.bust{ background:#fff1f0; color:#a8071a; }
.cards{ display:flex; gap:6px; flex-wrap: wrap; }
</style>
