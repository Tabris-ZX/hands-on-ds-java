import { reactive } from 'vue'

const store = reactive({
  sessionId: null,
  userInfo: null,
  
  setSession(sessionId, userInfo) {
    this.sessionId = sessionId
    this.userInfo = userInfo
    localStorage.setItem('sessionId', sessionId)
    localStorage.setItem('userInfo', JSON.stringify(userInfo))
  },
  
  logout() {
    this.sessionId = null
    this.userInfo = null
    localStorage.removeItem('sessionId')
    localStorage.removeItem('userInfo')
  }
})

export function useStore() {
  return store
}

