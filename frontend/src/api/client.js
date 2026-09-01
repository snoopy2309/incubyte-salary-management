import axios from 'axios'

// In dev, Vite proxies /api to the backend. In production, VITE_API_BASE_URL
// points at the deployed backend (e.g. https://your-api.onrender.com).
const baseURL = import.meta.env.VITE_API_BASE_URL
  ? `${import.meta.env.VITE_API_BASE_URL}/api/v1`
  : '/api/v1'

const api = axios.create({ baseURL })

export async function fetchEmployees({ page = 0, size = 20, country, department, q } = {}) {
  const params = { page, size }
  if (country) params.country = country
  if (department) params.department = department
  if (q) params.q = q
  const { data } = await api.get('/employees', { params })
  return data
}

export async function createEmployee(payload) {
  const { data } = await api.post('/employees', payload)
  return data
}

export async function updateSalary(id, { amount, currency }) {
  const { data } = await api.patch(`/employees/${id}/salary`, { amount, currency })
  return data
}

export async function fetchSummary() {
  const { data } = await api.get('/insights/summary')
  return data
}

export async function fetchByCountry() {
  const { data } = await api.get('/insights/by-country')
  return data
}

export async function fetchByDepartment() {
  const { data } = await api.get('/insights/by-department')
  return data
}

export async function fetchDistribution() {
  const { data } = await api.get('/insights/distribution')
  return data
}
