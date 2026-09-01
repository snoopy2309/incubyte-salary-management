import { useQuery } from '@tanstack/react-query'
import { Alert, Box, Paper, Skeleton, Typography } from '@mui/material'
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet'
import GroupsIcon from '@mui/icons-material/Groups'
import TrendingUpIcon from '@mui/icons-material/TrendingUp'
import EqualizerIcon from '@mui/icons-material/Equalizer'
import {
  fetchByCountry,
  fetchByDepartment,
  fetchDistribution,
  fetchSummary,
} from '../api/client'
import StatCard from '../components/StatCard'
import ChartCard from '../components/ChartCard'
import GroupBarChart from '../components/GroupBarChart'
import DistributionChart from '../components/DistributionChart'
import PayrollDonut from '../components/PayrollDonut'
import { formatNumber, formatUsd } from '../utils/format'

function ChartBody({ query, children }) {
  if (query.isLoading) return <Skeleton variant="rounded" width="100%" height="100%" />
  if (query.isError) return <Alert severity="error">Could not load this chart.</Alert>
  return children
}

// --- takeaway builders (plain-English answers computed from the data) ---

function countryNote(groups) {
  if (!groups?.length) return null
  const sorted = [...groups].sort((a, b) => b.averageUsd - a.averageUsd)
  const top = sorted[0]
  const bottom = sorted[sorted.length - 1]
  const ratio = (top.averageUsd / bottom.averageUsd).toFixed(1)
  return `${top.name} pays the most on average (${formatUsd(top.averageUsd)}); ${bottom.name} the least (${formatUsd(bottom.averageUsd)}) — a ${ratio}× gap once normalised to USD.`
}

function departmentNote(groups) {
  if (!groups?.length) return null
  const sorted = [...groups].sort((a, b) => b.averageUsd - a.averageUsd)
  const top = sorted[0]
  return `${top.name} has the highest average pay (${formatUsd(top.averageUsd)}) of any department.`
}

function distributionNote(bands) {
  if (!bands?.length) return null
  const top = [...bands].sort((a, b) => b.headcount - a.headcount)[0]
  return `Most employees sit in the ${top.label} band (${formatNumber(top.headcount)} people).`
}

export default function DashboardPage() {
  const summary = useQuery({ queryKey: ['summary'], queryFn: fetchSummary })
  const byCountry = useQuery({ queryKey: ['byCountry'], queryFn: fetchByCountry })
  const byDepartment = useQuery({ queryKey: ['byDepartment'], queryFn: fetchByDepartment })
  const distribution = useQuery({ queryKey: ['distribution'], queryFn: fetchDistribution })

  if (summary.isError) return <Alert severity="error">Could not load pay insights.</Alert>

  const s = summary.data
  const cards = s
    ? [
        { label: 'Total payroll', value: formatUsd(s.totalUsd, { compact: true }), caption: 'Annual, all employees', icon: <AccountBalanceWalletIcon /> },
        { label: 'Headcount', value: formatNumber(s.headcount), caption: 'Across 6 countries', icon: <GroupsIcon /> },
        { label: 'Average pay', value: formatUsd(s.averageUsd), caption: 'Across all staff', icon: <TrendingUpIcon /> },
        { label: 'Typical pay', value: formatUsd(s.medianUsd), caption: 'The middle earner', icon: <EqualizerIcon /> },
      ]
    : null

  return (
    <>
      <Box className="reveal">
        <Typography variant="h4" gutterBottom>
          How does ACME pay its people?
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
          {s ? formatNumber(s.headcount) : '—'} employees across six countries — every figure
          normalised to USD so they compare fairly.
        </Typography>
      </Box>

      <Box
        className="reveal"
        sx={{
          display: 'grid',
          gap: 2.5,
          gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr', md: 'repeat(4, 1fr)' },
          animationDelay: '60ms',
        }}
      >
        {cards
          ? cards.map((card) => <StatCard key={card.label} {...card} />)
          : Array.from({ length: 4 }).map((_, i) => (
              <Skeleton key={i} variant="rounded" height={116} />
            ))}
      </Box>

      <Paper className="reveal" variant="outlined" sx={{ p: 3, mt: 2.5, animationDelay: '120ms' }}>
        <Typography variant="h6">Where the payroll goes</Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
          Share of total salary cost by country (USD)
        </Typography>
        {byCountry.isLoading ? (
          <Skeleton variant="rounded" height={220} />
        ) : (
          <PayrollDonut groups={byCountry.data ?? []} />
        )}
      </Paper>

      <Box
        className="reveal"
        sx={{
          display: 'grid',
          gap: 2.5,
          mt: 2.5,
          gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' },
          animationDelay: '180ms',
        }}
      >
        <ChartCard title="Pay by country" subtitle="Average vs typical pay, USD" note={countryNote(byCountry.data)}>
          <ChartBody query={byCountry}>
            <GroupBarChart data={byCountry.data ?? []} />
          </ChartBody>
        </ChartCard>
        <ChartCard title="Pay by department" subtitle="Average vs typical pay, USD" note={departmentNote(byDepartment.data)}>
          <ChartBody query={byDepartment}>
            <GroupBarChart data={byDepartment.data ?? []} />
          </ChartBody>
        </ChartCard>
      </Box>

      <Box className="reveal" sx={{ mt: 2.5, animationDelay: '240ms' }}>
        <ChartCard title="How is pay spread?" subtitle="Employees per USD salary band" note={distributionNote(distribution.data)}>
          <ChartBody query={distribution}>
            <DistributionChart data={distribution.data ?? []} />
          </ChartBody>
        </ChartCard>
      </Box>
    </>
  )
}
