<template>
  <div class="panel" style="max-width:420px;margin:40px auto">
    <h2 style="margin:4px 0 12px 0">Login</h2>
    <div class="vstack">
      <input class="btn" v-model="id" placeholder="Enter user id"/>
      <button class="btn primary" @click="go">Enter</button>
    </div>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import { setUserId } from '../user'
import { useRouter } from 'vue-router'
const id = ref(''); const router = useRouter()
function go(){ if(!id.value.trim()) return; setUserId(id.value.trim()); router.push('/lobby') }
</script>
