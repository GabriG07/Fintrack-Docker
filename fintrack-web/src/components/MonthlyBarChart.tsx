import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts'
import { MonthSummary } from '../types'

const MONTHS = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez']

interface Props { history: MonthSummary[] }

export default function MonthlyBarChart({ history }: Props) {
    const data = history.map(h => ({
        name: MONTHS[h.month - 1],
        Receitas: Number(h.totalIncome),
        Despesas: Number(h.totalExpense),
    }))

    return (
        <div className="bg-white rounded-xl border border-gray-200 p-5">
            <h3 className="font-semibold text-gray-700 mb-4 text-sm uppercase tracking-wide">
                Últimos 6 meses
            </h3>
            <ResponsiveContainer width="100%" height={220}>
                <BarChart data={data} barSize={14}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" />
                    <XAxis dataKey="name" tick={{ fontSize: 11 }} />
                    <YAxis tick={{ fontSize: 11 }} tickFormatter={v => `R$${(v / 1000).toFixed(0)}k`} />
                    <Tooltip formatter={(v: number) => v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })} />
                    <Legend />
                    <Bar dataKey="Receitas" fill="#22c55e" radius={[4, 4, 0, 0]} />
                    <Bar dataKey="Despesas" fill="#f87171" radius={[4, 4, 0, 0]} />
                </BarChart>
            </ResponsiveContainer>
        </div>
    )
}
