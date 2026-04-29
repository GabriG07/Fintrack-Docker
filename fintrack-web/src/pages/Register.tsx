import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import api from '../api/axios'

const schema = z.object({
  name:     z.string().min(2, 'Mínimo 2 caracteres'),
  email:    z.string().email('Email inválido'),
  password: z.string().min(6, 'Mínimo 6 caracteres'),
})
type FormData = z.infer<typeof schema>

export default function Register() {
  const { login } = useAuth()
  const navigate  = useNavigate()
  const { register, handleSubmit, formState: { errors, isSubmitting }, setError } = useForm<FormData>({
    resolver: zodResolver(schema),
  })

  const onSubmit = async (data: FormData) => {
    try {
      const res = await api.post('/auth/register', data)
      login(res.data)
      navigate('/')
    } catch (err: any) {
      const msg = err.response?.data?.error || 'Erro ao cadastrar'
      setError('root', { message: msg })
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-50 to-blue-100 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-xl w-full max-w-md p-8">
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-14 h-14 bg-indigo-600 rounded-2xl mb-3">
            <span className="text-white text-2xl font-bold">F</span>
          </div>
          <h1 className="text-2xl font-bold text-gray-900">Criar conta</h1>
          <p className="text-gray-500 text-sm mt-1">Comece a controlar suas finanças</p>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          {(['name', 'email', 'password'] as const).map((field) => (
            <div key={field}>
              <label className="block text-sm font-medium text-gray-700 mb-1 capitalize">
                {field === 'name' ? 'Nome' : field === 'email' ? 'Email' : 'Senha'}
              </label>
              <input
                {...register(field)}
                type={field === 'password' ? 'password' : field === 'email' ? 'email' : 'text'}
                placeholder={field === 'name' ? 'Seu nome' : field === 'email' ? 'seu@email.com' : '••••••••'}
                className="w-full px-3 py-2.5 border border-gray-300 rounded-lg outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent text-sm"
              />
              {errors[field] && <p className="text-red-500 text-xs mt-1">{errors[field]?.message}</p>}
            </div>
          ))}

          {errors.root && (
            <div className="bg-red-50 border border-red-200 text-red-600 px-3 py-2 rounded-lg text-sm">
              {errors.root.message}
            </div>
          )}

          <button
            type="submit"
            disabled={isSubmitting}
            className="w-full bg-indigo-600 hover:bg-indigo-700 text-white py-2.5 rounded-lg font-medium text-sm transition disabled:opacity-60"
          >
            {isSubmitting ? 'Cadastrando...' : 'Criar conta'}
          </button>
        </form>

        <p className="text-center text-gray-500 mt-5 text-sm">
          Já tem conta?{' '}
          <Link to="/login" className="text-indigo-600 hover:underline font-medium">
            Entrar
          </Link>
        </p>
      </div>
    </div>
  )
}
