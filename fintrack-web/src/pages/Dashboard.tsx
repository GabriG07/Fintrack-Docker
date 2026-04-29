import { useState, useEffect, useCallback } from 'react'
import { useAuth } from '../contexts/AuthContext'
import api from '../api/axios'
import { Transaction, MonthSummary } from '../types'
import SummaryCard from '../components/SummaryCard'
import ExchangeWidget from '../components/ExchangeWidget'
import CategoryPieChart from '../components/CategoryPieChart'
import MonthlyBarChart from '../components/MonthlyBarChart'
import TransactionList from '../components/TransactionList'
import TransactionModal from '../components/TransactionModal'

const MONTHS = ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
  'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro']

export default function Dashboard() {
  const { user, logout } = useAuth()
  const now = new Date()

  const [month, setMonth] = useState(now.getMonth() + 1)
  const [year, setYear] = useState(now.getFullYear())
  const [transactions, setTransactions] = useState<Transaction[]>([])
  const [summary, setSummary] = useState<MonthSummary | null>(null)
  const [history, setHistory] = useState<MonthSummary[]>([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)

  const fmt = (v: number) => v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })

  const fetchData = useCallback(async () => {
    setLoading(true)
    try {
      const [txRes, sumRes, histRes] = await Promise.all([
        api.get('/transactions', { params: { month, year } }),
        api.get('/transactions/summary', { params: { month, year } }),
        api.get('/transactions/history'),
      ])
      setTransactions(txRes.data)
      setSummary(sumRes.data)
      setHistory(histRes.data)
    } finally {
      setLoading(false)
    }
  }, [month, year])

  useEffect(() => { fetchData() }, [fetchData])

  const handleDelete = async (id: number) => {
    if (!confirm('Excluir esta transação?')) return
    await api.delete(`/transactions/${id}`)
    fetchData()
  }

  const prevMonth = () => {
    if (month === 1) { setMonth(12); setYear(y => y - 1) }
    else setMonth(m => m - 1)
  }
  const nextMonth = () => {
    if (month === 12) { setMonth(1); setYear(y => y + 1) }
    else setMonth(m => m + 1)
  }

  return (
    <div className="min-h-screen bg-slate-50">
      {/* Header */}
      <header className="bg-white border-b border-gray-200 sticky top-0 z-40">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 h-14 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 bg-indigo-600 rounded-lg flex items-center justify-center">
              <span className="text-white font-bold text-sm">F</span>
            </div>
            <span className="font-bold text-gray-900">FinTrack</span>
          </div>
          <div className="flex items-center gap-3">
            <span className="text-sm text-gray-600 hidden sm:block">Olá, {user?.name?.split(' ')[0]} 👋</span>
            <button onClick={logout}
              className="text-sm text-gray-500 hover:text-red-500 transition font-medium">
              Sair
            </button>
          </div>
        </div>
      </header>

      <main className="max-w-6xl mx-auto px-4 sm:px-6 py-6 space-y-6">

        {/* Month selector */}
        <div className="flex items-center gap-3">
          <button onClick={prevMonth} className="p-1.5 rounded-lg hover:bg-gray-200 transition text-gray-600">◀</button>
          <h2 className="text-lg font-semibold text-gray-900 w-44 text-center">
            {MONTHS[month - 1]} {year}
          </h2>
          <button onClick={nextMonth} className="p-1.5 rounded-lg hover:bg-gray-200 transition text-gray-600">▶</button>
        </div>

        {/* Summary cards */}
        {loading ? (
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            {[1, 2, 3].map(i => (
              <div key={i} className="h-20 bg-white rounded-xl border border-gray-100 animate-pulse" />
            ))}
          </div>
        ) : summary ? (
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <SummaryCard title="Receitas" value={fmt(Number(summary.totalIncome))} icon="💰" color="green" />
            <SummaryCard title="Despesas" value={fmt(Number(summary.totalExpense))} icon="💸" color="red" />
            <SummaryCard title="Saldo" value={fmt(Number(summary.balance))} icon="📊" color="blue" />
          </div>
        ) : null}

        {/* Charts + Exchange */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
          <div className="lg:col-span-1">
            <CategoryPieChart transactions={transactions} />
          </div>
          <div className="lg:col-span-1">
            <MonthlyBarChart history={history} />
          </div>
          <div className="lg:col-span-1">
            <ExchangeWidget />
          </div>
        </div>

        {/* Transactions */}
        <div className="bg-white rounded-xl border border-gray-200 p-5">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold text-gray-700 text-sm uppercase tracking-wide">
              Transações - {MONTHS[month - 1]}
            </h3>
            <button
              onClick={() => setShowModal(true)}
              className="bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-medium px-4 py-2 rounded-lg transition flex items-center gap-1.5"
            >
              <span className="text-base leading-none">+</span> Nova transação
            </button>
          </div>
          <TransactionList transactions={transactions} onDelete={handleDelete} />
        </div>
      </main>

      {showModal && (
        <TransactionModal
          onClose={() => setShowModal(false)}
          onSuccess={fetchData}
        />
      )}
    </div>
  )
}
