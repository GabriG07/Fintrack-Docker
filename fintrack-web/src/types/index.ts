export type TransactionType = 'INCOME' | 'EXPENSE'

export type Category =
  | 'FOOD' | 'TRANSPORT' | 'HOUSING' | 'HEALTH'
  | 'ENTERTAINMENT' | 'SALARY' | 'INVESTMENT' | 'OTHER'

export interface Transaction {
  id: number
  description: string
  amount: number
  type: TransactionType
  category: Category
  date: string
}

export interface MonthSummary {
  totalIncome: number
  totalExpense: number
  balance: number
  month: number
  year: number
}

export interface ExchangeRates {
  usdBrl: number
  eurBrl: number
  btcBrl: number
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  name: string
  email: string
}

export interface User {
  name: string
  email: string
}
