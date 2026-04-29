import React, { createContext, useContext, useState, useEffect } from 'react'
import { User, AuthResponse } from '../types'

interface AuthContextType {
  user: User | null
  loading: boolean
  login: (data: AuthResponse) => void
  logout: () => void
  isAuthenticated: boolean
}

const AuthContext = createContext<AuthContextType | null>(null)

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const token = localStorage.getItem('accessToken')
    const name  = localStorage.getItem('userName')
    const email = localStorage.getItem('userEmail')
    if (token && name && email) setUser({ name, email })
    setLoading(false)
  }, [])

  const login = (data: AuthResponse) => {
    localStorage.setItem('accessToken',  data.accessToken)
    localStorage.setItem('refreshToken', data.refreshToken)
    localStorage.setItem('userName',     data.name)
    localStorage.setItem('userEmail',    data.email)
    setUser({ name: data.name, email: data.email })
  }

  const logout = () => {
    localStorage.clear()
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, logout, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
