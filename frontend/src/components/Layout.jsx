import { AppBar, Box, Button, Container, Toolbar, Typography } from '@mui/material'
import { Link, useLocation } from 'react-router-dom'

const NAV = [
  { label: 'Dashboard', to: '/dashboard' },
  { label: 'Employees', to: '/employees' },
]

/** App shell: top navigation bar plus a centred content container. */
export default function Layout({ children }) {
  const { pathname } = useLocation()
  return (
    <Box sx={{ minHeight: '100vh', bgcolor: 'grey.50' }}>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            ACME Salary Management
          </Typography>
          {NAV.map((item) => (
            <Button
              key={item.to}
              component={Link}
              to={item.to}
              sx={{
                color: 'white',
                fontWeight: pathname.startsWith(item.to) ? 700 : 400,
              }}
            >
              {item.label}
            </Button>
          ))}
        </Toolbar>
      </AppBar>
      <Container maxWidth="lg" sx={{ py: 4 }}>
        {children}
      </Container>
    </Box>
  )
}
