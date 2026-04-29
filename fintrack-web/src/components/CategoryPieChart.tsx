import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer, Legend } from 'recharts'
import { Transaction } from '../types'

const COLORS: Record<string, string> = {
  FOOD:          '#f59e0b',
  TRANSPORT:     '#3b82f6',
  HOUSING:       '#8b5cf6',
  HEALTH:        '#10b981',
  ENTERTAINMENT: '#ec4899',
  SALARY:        '#22c55e',
  INVESTMENT:    '#0ea5e9',
  OTHER:         '#94a3b8',
}

const LABELS: Record<string, string> = {
  FOOD: 'Alimentação', TRANSPORT: 'Transporte', HOUSING: 'Moradia',
  HEALTH: 'Saúde', ENTERTAINMENT: 'Lazer', SALARY: 'Salário',
  INVESTMENT: 'Investimentos', OTHER: 'Outros',
}

interface Props { transactions: Transaction[] }

export default function CategoryPieChart({ transactions }: Props) {
  const data = Object.entries(
    transactions
      .filter(t => t.type === 'EXPENSE')
      .reduce<Record<string, number>>((acc, t) => {
        acc[t.category] = (acc[t.category] || 0) + Number(t.amount)
        return acc
      }, {})
  ).map(([name, value]) => ({ name, label: LABELS[name] || name, value }))

  if (data.length === 0)
    return (
      <div className="bg-white rounded-xl border border-gray-200 p-5 flex items-center justify-center h-64">
        <p className="text-gray-400 text-sm">Sem despesas neste mês</p>
      </div>
    )

  return (
    <div className="bg-white rounded-xl border border-gray-200 p-5">
      <h3 className="font-semibold text-gray-700 mb-4 text-sm uppercase tracking-wide">
        Despesas por categoria
      </h3>
      <ResponsiveContainer width="100%" height={220}>
        <PieChart>
          <Pie data={data} dataKey="value" nameKey="label" cx="50%" cy="50%" outerRadius={80} paddingAngle={3}>
            {data.map((entry) => (
              <Cell key={entry.name} fill={COLORS[entry.name] || '#94a3b8'} />
            ))}
          </Pie>
          <Tooltip formatter={(v: number) => v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })} />
          <Legend formatter={(v) => <span className="text-xs text-gray-600">{v}</span>} />
        </PieChart>
      </ResponsiveContainer>
    </div>
  )
}
