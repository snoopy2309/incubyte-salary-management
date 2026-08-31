import { render, screen } from '@testing-library/react'
import StatCard from './StatCard'

describe('StatCard', () => {
  it('renders its label and value', () => {
    render(<StatCard label="Headcount" value="10,000" />)

    expect(screen.getByText('Headcount')).toBeInTheDocument()
    expect(screen.getByText('10,000')).toBeInTheDocument()
  })
})
