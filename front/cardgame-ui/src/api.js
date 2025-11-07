const base = import.meta.env.VITE_API_BASE || 'http://localhost:8080'
export async function jget(p){ const r = await fetch(base+p); return r.json() }
export async function jpost(p, opts={}){
  const r = await fetch(base+p, { method:'POST', ...opts })
  return r.json()
}
