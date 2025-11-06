// src/api.js
async function request(method, url, params = null, body = null) {
    const qs = params ? '?' + new URLSearchParams(params).toString() : ''
    const res = await fetch(url + qs, {
        method,
        headers: body ? { 'Content-Type': 'application/json' } : undefined,
        body: body ? JSON.stringify(body) : null,
    })
    if (!res.ok) throw new Error(`HTTP ${res.status}: ${await res.text().catch(()=>res.statusText)}`)
    return res.json()
}

export const startSession   = (userId, init=1000) => request('POST','/api/session/start',{userId,init})
export const getBalance     = (userId)           => request('GET','/api/session/balance',{userId})
export const playBlackjack  = (userId, bet)      => request('POST','/api/blackjack/round',{userId,bet})
export const playBaccarat   = (userId, bet,target)=>request('POST','/api/baccarat/round',{userId,bet,target})
export const playSevenSimple= (userId, ante)     => request('POST','/api/seven/simple',{userId,ante})
