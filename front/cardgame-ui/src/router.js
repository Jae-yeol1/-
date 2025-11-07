import { createRouter, createWebHistory } from 'vue-router'
import Login from './pages/Login.vue'
import Lobby from './pages/Lobby.vue'
import Blackjack from './pages/Blackjack.vue'
import Baccarat from './pages/Baccarat.vue'
import Seven from './pages/Seven.vue'
import BlackjackMulti from './pages/BlackjackMulti.vue'
import BaccaratMulti from './pages/BaccaratMulti.vue'
const routes=[
  { path:'/', redirect:'/login' },
  { path:'/login', component: Login },
  { path:'/lobby', component: Lobby },
  { path:'/blackjack', component: Blackjack },
  { path:'/baccarat', component: Baccarat },
  { path:'/seven', component: Seven },
  { path:'/blackjack-multi', component: BlackjackMulti },
  { path:'/baccarat-multi', component: BaccaratMulti },
]
export default createRouter({ history:createWebHistory(), routes })
