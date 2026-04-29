interface Props {
  title: string
  value: string
  icon: string
  color: 'green' | 'red' | 'blue'
}

const colorMap = {
  green: 'bg-emerald-50 text-emerald-600 border-emerald-100',
  red:   'bg-red-50 text-red-600 border-red-100',
  blue:  'bg-indigo-50 text-indigo-600 border-indigo-100',
}

export default function SummaryCard({ title, value, icon, color }: Props) {
  return (
    <div className={`rounded-xl border p-5 flex items-center gap-4 ${colorMap[color]}`}>
      <div className="text-3xl">{icon}</div>
      <div>
        <p className="text-sm font-medium opacity-70">{title}</p>
        <p className="text-xl font-bold mt-0.5">{value}</p>
      </div>
    </div>
  )
}
