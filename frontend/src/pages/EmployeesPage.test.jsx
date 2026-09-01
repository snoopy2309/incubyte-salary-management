import { render, screen } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { vi } from 'vitest'
import EmployeesPage from './EmployeesPage'
import * as client from '../api/client'

vi.mock('../api/client')

function renderWithQuery(ui) {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
  return render(<QueryClientProvider client={queryClient}>{ui}</QueryClientProvider>)
}

describe('EmployeesPage', () => {
  it('renders employee rows with local and USD salary', async () => {
    client.fetchByCountry.mockResolvedValue([{ name: 'Japan' }])
    client.fetchByDepartment.mockResolvedValue([{ name: 'Engineering' }])
    client.fetchEmployees.mockResolvedValue({
      content: [
        {
          id: 9,
          firstName: 'Noah',
          lastName: 'Costa',
          email: 'noah@acme.com',
          country: 'Japan',
          department: 'Product',
          jobTitle: 'Manager',
          salaryAmount: 14257000,
          currency: 'JPY',
          salaryUsd: 95521.9,
        },
      ],
      page: 0,
      size: 20,
      totalElements: 1,
      totalPages: 1,
      hasNext: false,
      hasPrevious: false,
      first: true,
      last: true,
    })

    renderWithQuery(<EmployeesPage />)

    expect(await screen.findByText('Noah Costa')).toBeInTheDocument()
    expect(screen.getByText('noah@acme.com')).toBeInTheDocument()
    expect(screen.getByText('$95,522')).toBeInTheDocument()
  })
})
