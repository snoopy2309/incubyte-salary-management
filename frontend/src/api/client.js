import axios from 'axios'

// All calls go to the versioned API; Vite proxies /api to the backend in dev.
const api = axios.create({ baseURL: '/api/v1' })

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
