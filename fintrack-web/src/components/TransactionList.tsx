import { Transaction } from '../types'

const LABELS: Record<string, string> = {
  FOOD: 'Alimentação', TRANSPORT: 'Transporte', HOUSING: 'Moradia',
  HEALTH: 'Saúde', ENTERTAINMENT: 'Lazer', SALARY: 'Salário',
  INVESTMENT: 'Investimentos', OTHER: 'Outros',
}

interface Props {
  transactions: Transaction[]
  onDelete: (id: number) => void
}

export default function TransactionList({ transactions, onDelete }: Props) {
  const fmt = (v: number) => v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
  const fmtDate = (d: string) => new Date(d + 'T00:00:00').toLocaleDateString('pt-BR')

  if (transactions.length === 0)
    return <p className="text-gray-400 text-sm text-center py-8">Nenhuma transação neste período.</p>

  return (
    <div className="space-y-2">
      {transactions.map(t => (
        <div key={t.id} className="flex items-center justify-between p-3 rounded-lg hover:bg-gray-50 transition group">
          <div className="flex items-center gap-3 min-w-0">
            <div className={`w-2 h-2 rounded-full flex-shrink-0 ${t.type === 'INCOME' ? 'bg-emerald-500' : 'bg-red-400'}`} />
            <div className="min-w-0">
              <p className="text-sm font-medium text-gray-900 truncate">{t.description}</p>
              <p className="text-xs text-gray-400">{LABELS[t.category]} · {fmtDate(t.date)}</p>
            </div>
          </div>
          <div className="flex items-center gap-3 flex-shrink-0 ml-3">
            <span className={`text-sm font-semibold ${t.type === 'INCOME' ? 'text-emerald-600' : 'text-red-500'}`}>
              {t.type === 'INCOME' ? '+' : '-'}{fmt(Number(t.amount))}
            </span>
            <button
              onClick={() => onDelete(t.id)}
              className="text-gray-300 hover:text-red-400 transition opacity-0 group-hover:opacity-100 text-lg leading-none"
              title="Excluir"
            >×</button>
          </div>
        </div>
      ))}
    </div>
  )
}
