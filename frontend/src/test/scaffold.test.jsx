import { render, screen } from '@testing-library/react'

// A trivial component to prove the render + assertion pipeline works.
// Real components are driven test-first from here on.
function Hello() {
  return <h1>Salary Management</h1>
}

describe('frontend test harness', () => {
  it('renders a component and finds its text', () => {
    render(<Hello />)
    expect(screen.getByRole('heading', { name: 'Salary Management' })).toBeInTheDocument()
  })
})
