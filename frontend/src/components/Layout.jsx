import {
  AppBar,
  Box,
  Button,
  Chip,
  Container,
  Stack,
  Toolbar,
  Typography,
} from '@mui/material'
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet'
import LogoutIcon from '@mui/icons-material/Logout'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

const NAV = [
  { label: 'Dashboard', to: '/dashboard' },
  { label: 'Employees', to: '/employees' },
]

/** App shell: a clean, frosted white top bar over a light content area. */
export default function Layout({ children }) {
  const { pathname } = useLocation()
  const navigate = useNavigate()
  const { user, logout } = useAuth()

  return (
    <Box sx={{ minHeight: '100vh', bgcolor: 'background.default' }}>
      <AppBar
        position="sticky"
        elevation={0}
        sx={{
          bgcolor: 'rgba(255,255,255,0.8)',
          backdropFilter: 'blur(10px)',
          color: 'text.primary',
          borderBottom: '1px solid',
          borderColor: 'divider',
        }}
      >
        <Container maxWidth="xl">
          <Toolbar disableGutters sx={{ minHeight: 64 }}>
            <Stack direction="row" spacing={1.2} alignItems="center" sx={{ mr: 4 }}>
              <Box
                sx={{
                  width: 32,
                  height: 32,
                  borderRadius: 1.5,
                  bgcolor: 'primary.main',
                  color: 'white',
                  display: 'grid',
                  placeItems: 'center',
                }}
              >
                <AccountBalanceWalletIcon fontSize="small" />
              </Box>
              <Typography fontWeight={800} letterSpacing={-0.3}>
                ACME Salary
              </Typography>
            </Stack>

            <Stack direction="row" spacing={0.5} sx={{ flexGrow: 1 }}>
              {NAV.map((item) => {
                const active = pathname.startsWith(item.to)
                return (
                  <Button
                    key={item.to}
                    component={Link}
                    to={item.to}
                    disableRipple
                    sx={{
                      color: active ? 'primary.main' : 'text.secondary',
                      fontWeight: active ? 700 : 500,
                      '&:hover': { bgcolor: 'transparent', color: 'text.primary' },
                    }}
                  >
                    {item.label}
                  </Button>
                )
              })}
            </Stack>

            <Chip label={user?.email} size="small" variant="outlined" sx={{ ml: 1.5 }} />
            <Button
              startIcon={<LogoutIcon />}
              onClick={() => {
                logout()
                navigate('/login', { replace: true })
              }}
              disableRipple
              sx={{ ml: 0.5, color: 'text.secondary', '&:hover': { color: 'text.primary', bgcolor: 'transparent' } }}
            >
              Logout
            </Button>
          </Toolbar>
        </Container>
      </AppBar>

      <Container maxWidth="xl" sx={{ py: 4 }}>
        {children}
      </Container>
    </Box>
  )
}
