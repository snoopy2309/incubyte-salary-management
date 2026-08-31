import { useQuery } from '@tanstack/react-query'
import { Alert, Box, CircularProgress, Typography } from '@mui/material'
import { fetchSummary } from '../api/client'
import StatCard from '../components/StatCard'
import { formatNumber, formatUsd } from '../utils/format'

/** Pay-insights dashboard. F1: the headline stat cards. Charts follow. */
export default function DashboardPage() {
  const { data, isLoading, isError } = useQuery({
    queryKey: ['summary'],
    queryFn: fetchSummary,
  })

  if (isLoading) return <CircularProgress />
  if (isError) return <Alert severity="error">Could not load the pay summary.</Alert>

  const cards = [
    { label: 'Total payroll', value: formatUsd(data.totalUsd, { compact: true }) },
    { label: 'Headcount', value: formatNumber(data.headcount) },
    { label: 'Average salary', value: formatUsd(data.averageUsd) },
    { label: 'Median salary', value: formatUsd(data.medianUsd) },
  ]

  return (
    <>
      <Typography variant="h4" gutterBottom>
        Pay insights
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
        All figures in USD, normalised across currencies.
      </Typography>
      <Box
        sx={{
          display: 'grid',
          gap: 3,
          gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr', md: 'repeat(4, 1fr)' },
        }}
      >
        {cards.map((card) => (
          <StatCard key={card.label} label={card.label} value={card.value} />
        ))}
      </Box>
    </>
  )
}
