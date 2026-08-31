import { render, screen } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { vi } from 'vitest'
import DashboardPage from './DashboardPage'
import * as client from '../api/client'

vi.mock('../api/client')

function renderWithQuery(ui) {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
  return render(<QueryClientProvider client={queryClient}>{ui}</QueryClientProvider>)
}

describe('DashboardPage', () => {
  it('shows the summary stat cards once loaded', async () => {
    client.fetchSummary.mockResolvedValue({
      headcount: 10000,
      totalUsd: 888591234.4,
      averageUsd: 88859.12,
      medianUsd: 85320,
    })

    renderWithQuery(<DashboardPage />)

    expect(await screen.findByText('10,000')).toBeInTheDocument()
    expect(screen.getByText('$88,859')).toBeInTheDocument()
    expect(screen.getByText('$85,320')).toBeInTheDocument()
    expect(screen.getByText('Median salary')).toBeInTheDocument()
  })
})
