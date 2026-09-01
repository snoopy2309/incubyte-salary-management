import { createContext, useContext, useState } from 'react'

// NOTE: placeholder demo login only — NOT real security. The credential is
// checked client-side purely to gate the demo UI. Real authentication (a users
// table, BCrypt hashing, server sessions/roles) is documented future work
// (see docs/adr/0004-auth-out-of-scope.md).
const DEMO_USER = { email: 'hr@acme.com', password: 'acme1234' }
const STORAGE_KEY = 'salary-demo-auth'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem(STORAGE_KEY)
    return saved ? JSON.parse(saved) : null
  })

  function login(email, password) {
    if (email.trim().toLowerCase() === DEMO_USER.email && password === DEMO_USER.password) {
      const signedIn = { email: DEMO_USER.email }
      setUser(signedIn)
      localStorage.setItem(STORAGE_KEY, JSON.stringify(signedIn))
      return true
    }
    return false
  }

  function logout() {
    setUser(null)
    localStorage.removeItem(STORAGE_KEY)
  }

  const value = { user, isAuthenticated: Boolean(user), login, logout, demo: DEMO_USER }
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within an AuthProvider')
  return ctx
}
