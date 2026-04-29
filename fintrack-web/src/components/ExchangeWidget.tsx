import { useEffect, useState } from 'react'
import api from '../api/axios'
import { ExchangeRates } from '../types'

export default function ExchangeWidget() {
  const [rates, setRates] = useState<ExchangeRates | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.get('/exchange/rates')
      .then(r => setRates(r.data))
      .finally(() => setLoading(false))
  }, [])

  const fmt = (v: number, decimals = 2) =>
    v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL', minimumFractionDigits: decimals })

  if (loading)
    return <div className="bg-white rounded-xl border border-gray-200 p-5 animate-pulse h-32" />

  return (
    <div className="bg-white rounded-xl border border-gray-200 p-5">
      <h3 className="font-semibold text-gray-700 mb-4 text-sm uppercase tracking-wide">
        Câmbio ao vivo
      </h3>
      {rates && (
        <div className="space-y-3">
          {[
            { label: 'USD', flag: '🇺🇸', value: fmt(rates.usdBrl) },
            { label: 'EUR', flag: '🇪🇺', value: fmt(rates.eurBrl) },
            { label: 'BTC', flag: '₿',   value: fmt(rates.btcBrl, 0) },
          ].map(({ label, flag, value }) => (
            <div key={label} className="flex items-center justify-between">
              <span className="text-sm text-gray-600">{flag} {label}</span>
              <span className="text-sm font-semibold text-gray-900">{value}</span>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
