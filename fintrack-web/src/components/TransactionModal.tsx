import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import api from '../api/axios'

const schema = z.object({
  description: z.string().min(1, 'Campo obrigatório'),
  amount:      z.string().refine(v => parseFloat(v) > 0, 'Valor deve ser positivo'),
  type:        z.enum(['INCOME', 'EXPENSE']),
  category:    z.enum(['FOOD','TRANSPORT','HOUSING','HEALTH','ENTERTAINMENT','SALARY','INVESTMENT','OTHER']),
  date:        z.string().min(1, 'Campo obrigatório'),
})
type FormData = z.infer<typeof schema>

const CATEGORIES = [
  { value: 'SALARY',        label: 'Salário' },
  { value: 'FOOD',          label: 'Alimentação' },
  { value: 'TRANSPORT',     label: 'Transporte' },
  { value: 'HOUSING',       label: 'Moradia' },
  { value: 'HEALTH',        label: 'Saúde' },
  { value: 'ENTERTAINMENT', label: 'Lazer' },
  { value: 'INVESTMENT',    label: 'Investimentos' },
  { value: 'OTHER',         label: 'Outros' },
]

interface Props {
  onClose:   () => void
  onSuccess: () => void
}

export default function TransactionModal({ onClose, onSuccess }: Props) {
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: {
      type:     'EXPENSE',
      category: 'FOOD',
      date:     new Date().toISOString().slice(0, 10),
    },
  })

  const onSubmit = async (data: FormData) => {
    await api.post('/transactions', { ...data, amount: parseFloat(data.amount) })
    onSuccess()
    onClose()
  }

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md p-6">
        <div className="flex items-center justify-between mb-5">
          <h2 className="text-lg font-bold text-gray-900">Nova transação</h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600 text-2xl leading-none">×</button>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Tipo</label>
            <div className="grid grid-cols-2 gap-2">
              {(['INCOME', 'EXPENSE'] as const).map(t => (
                <label key={t} className="flex items-center gap-2 cursor-pointer border rounded-lg p-2.5 has-[:checked]:border-indigo-500 has-[:checked]:bg-indigo-50">
                  <input {...register('type')} type="radio" value={t} className="accent-indigo-600" />
                  <span className="text-sm font-medium">{t === 'INCOME' ? '📈 Receita' : '📉 Despesa'}</span>
                </label>
              ))}
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Descrição</label>
            <input {...register('description')} placeholder="Ex: Salário, Mercado..."
              className="w-full px-3 py-2 border border-gray-300 rounded-lg outline-none focus:ring-2 focus:ring-indigo-500 text-sm" />
            {errors.description && <p className="text-red-500 text-xs mt-1">{errors.description.message}</p>}
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Valor (R$)</label>
              <input {...register('amount')} type="number" step="0.01" placeholder="0,00"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg outline-none focus:ring-2 focus:ring-indigo-500 text-sm" />
              {errors.amount && <p className="text-red-500 text-xs mt-1">{errors.amount.message}</p>}
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Data</label>
              <input {...register('date')} type="date"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg outline-none focus:ring-2 focus:ring-indigo-500 text-sm" />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Categoria</label>
            <select {...register('category')}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg outline-none focus:ring-2 focus:ring-indigo-500 text-sm bg-white">
              {CATEGORIES.map(c => (
                <option key={c.value} value={c.value}>{c.label}</option>
              ))}
            </select>
          </div>

          <div className="flex gap-3 pt-1">
            <button type="button" onClick={onClose}
              className="flex-1 border border-gray-300 text-gray-700 py-2 rounded-lg text-sm font-medium hover:bg-gray-50 transition">
              Cancelar
            </button>
            <button type="submit" disabled={isSubmitting}
              className="flex-1 bg-indigo-600 text-white py-2 rounded-lg text-sm font-medium hover:bg-indigo-700 transition disabled:opacity-60">
              {isSubmitting ? 'Salvando...' : 'Salvar'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
