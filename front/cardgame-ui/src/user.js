export function getUserId(){ return localStorage.getItem('userId') || '' }
export function setUserId(id){ localStorage.setItem('userId', id) }
