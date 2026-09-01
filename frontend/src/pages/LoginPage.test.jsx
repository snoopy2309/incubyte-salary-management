import { render, screen, fireEvent } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { AuthProvider } from '../auth/AuthContext'
import LoginPage from './LoginPage'

function renderLogin() {
  return render(
    <MemoryRouter>
      <AuthProvider>
        <LoginPage />
      </AuthProvider>
    </MemoryRouter>,
  )
}

describe('LoginPage', () => {
  beforeEach(() => localStorage.clear())

  it('shows an error for wrong credentials', () => {
    renderLogin()

    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'nope@acme.com' } })
    fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'wrong' } })
    fireEvent.click(screen.getByRole('button', { name: /sign in/i }))

    expect(screen.getByText(/invalid email or password/i)).toBeInTheDocument()
  })

  it('accepts the demo credentials (no error shown)', () => {
    renderLogin()

    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'hr@acme.com' } })
    fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'acme1234' } })
    fireEvent.click(screen.getByRole('button', { name: /sign in/i }))

    expect(screen.queryByText(/invalid email or password/i)).not.toBeInTheDocument()
  })
})
