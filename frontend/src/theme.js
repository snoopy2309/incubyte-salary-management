import { createTheme } from '@mui/material/styles'

// Restrained chart palette: one accent + one supporting hue (validated for CVD).
export const CHART = {
  average: '#4f46e5', // indigo (accent)
  median: '#14b8a6', // teal
  bar: '#4f46e5',
  grid: '#eef0f4',
  axis: '#8a90a2',
}

const theme = createTheme({
  palette: {
    primary: { main: '#4f46e5' },
    background: { default: '#fafafb', paper: '#ffffff' },
    text: { primary: '#101322', secondary: '#6b7180' },
    divider: '#ecedf2',
  },
  shape: { borderRadius: 16 },
  typography: {
    fontFamily: 'system-ui, -apple-system, "Segoe UI", Roboto, sans-serif',
    h4: { fontWeight: 800, letterSpacing: -1, fontSize: '2rem' },
    h5: { fontWeight: 700, letterSpacing: -0.4 },
    h6: { fontWeight: 700, letterSpacing: -0.2 },
    overline: { letterSpacing: 1, fontWeight: 600, fontSize: '0.68rem' },
    button: { fontWeight: 600 },
  },
  components: {
    MuiCard: {
      defaultProps: { elevation: 0 },
      styleOverrides: {
        root: {
          border: '1px solid #ecedf2',
          borderRadius: 16,
          boxShadow: '0 1px 2px rgba(16,24,40,0.03)',
          transition: 'transform .22s ease, box-shadow .22s ease, border-color .22s ease',
          '&:hover': {
            transform: 'translateY(-3px)',
            boxShadow: '0 12px 28px rgba(16,24,40,0.08)',
            borderColor: '#e0e1ea',
          },
        },
      },
    },
    MuiPaper: {
      styleOverrides: { outlined: { borderColor: '#ecedf2', borderRadius: 16 } },
    },
    MuiButton: {
      defaultProps: { disableElevation: true },
      styleOverrides: { root: { textTransform: 'none', borderRadius: 8, fontWeight: 600 } },
    },
    MuiTableCell: {
      styleOverrides: {
        head: {
          fontWeight: 600,
          fontSize: '0.72rem',
          letterSpacing: 0.4,
          textTransform: 'uppercase',
          color: '#8a90a2',
          borderBottom: '1px solid #ecedf2',
        },
      },
    },
  },
})

export default theme
