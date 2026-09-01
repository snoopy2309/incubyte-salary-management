import { useState } from 'react'
import { Box, LinearProgress, Stack, Typography } from '@mui/material'
import { Cell, Pie, PieChart, ResponsiveContainer, Sector } from 'recharts'
import { formatUsd } from '../utils/format'

const COLORS = ['#4f46e5', '#14b8a6', '#f59e0b', '#ec4899', '#8b5cf6', '#0ea5e9', '#ef4444', '#22c55e']

/** The hovered slice grows slightly, so hover "pops" like the reference dashboard. */
function ActiveSlice(props) {
  const { cx, cy, innerRadius, outerRadius, startAngle, endAngle, fill } = props
  return (
    <Sector
      cx={cx}
      cy={cy}
      innerRadius={innerRadius}
      outerRadius={outerRadius + 9}
      startAngle={startAngle}
      endAngle={endAngle}
      fill={fill}
      cornerRadius={2}
    />
  )
}

/**
 * A donut of payroll share per group, with a legend of amounts and shares.
 * Hovering a slice OR a legend card highlights the other and updates the centre —
 * a linked, interactive read of where the money goes.
 */
export default function PayrollDonut({ groups }) {
  const [active, setActive] = useState(null)

  const total = groups.reduce((sum, g) => sum + g.totalUsd, 0)
  const data = groups
    .map((g, i) => ({
      name: g.name,
      value: g.totalUsd,
      pct: total ? (g.totalUsd / total) * 100 : 0,
      color: COLORS[i % COLORS.length],
    }))
    .sort((a, b) => b.value - a.value)

  const focus = active != null ? data[active] : null

  return (
    <Box
      sx={{
        display: 'grid',
        gridTemplateColumns: { xs: '1fr', sm: '230px 1fr' },
        gap: 3,
        alignItems: 'center',
      }}
    >
      <Box sx={{ position: 'relative', height: 220 }}>
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie
              data={data}
              dataKey="value"
              nameKey="name"
              innerRadius="66%"
              outerRadius="90%"
              paddingAngle={2}
              stroke="none"
              isAnimationActive={false}
              activeIndex={active ?? undefined}
              activeShape={ActiveSlice}
              onMouseEnter={(_, index) => setActive(index)}
              onMouseLeave={() => setActive(null)}
            >
              {data.map((d) => (
                <Cell key={d.name} fill={d.color} />
              ))}
            </Pie>
          </PieChart>
        </ResponsiveContainer>
        <Box
          sx={{
            position: 'absolute',
            inset: 0,
            display: 'grid',
            placeItems: 'center',
            pointerEvents: 'none',
          }}
        >
          <Box sx={{ textAlign: 'center' }}>
            <Typography variant="caption" color="text.secondary" noWrap display="block">
              {focus ? focus.name : 'Total payroll'}
            </Typography>
            <Typography variant="h6">
              {formatUsd(focus ? focus.value : total, { compact: true })}
            </Typography>
            {focus && (
              <Typography variant="caption" sx={{ color: focus.color, fontWeight: 700 }}>
                {focus.pct.toFixed(1)}% of payroll
              </Typography>
            )}
          </Box>
        </Box>
      </Box>

      <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' }, gap: 1.5 }}>
        {data.map((d, i) => (
          <Box
            key={d.name}
            onMouseEnter={() => setActive(i)}
            onMouseLeave={() => setActive(null)}
            sx={{
              p: 1.5,
              borderRadius: 2,
              border: '1px solid',
              borderColor: active === i ? d.color : 'divider',
              boxShadow: active === i ? '0 8px 20px rgba(16,24,40,0.10)' : 'none',
              transform: active === i ? 'translateY(-2px)' : 'none',
              transition: 'border-color .15s ease, box-shadow .15s ease, transform .15s ease',
              cursor: 'default',
            }}
          >
            <Stack direction="row" justifyContent="space-between" alignItems="center">
              <Stack direction="row" spacing={1} alignItems="center">
                <Box sx={{ width: 10, height: 10, borderRadius: '50%', bgcolor: d.color }} />
                <Typography variant="body2" fontWeight={600}>
                  {d.name}
                </Typography>
              </Stack>
              <Typography variant="caption" color="text.secondary">
                {d.pct.toFixed(1)}%
              </Typography>
            </Stack>
            <Typography variant="body2" fontWeight={700} sx={{ mt: 0.5 }}>
              {formatUsd(d.value, { compact: true })}
            </Typography>
            <LinearProgress
              variant="determinate"
              value={d.pct}
              sx={{
                mt: 0.75,
                height: 5,
                borderRadius: 5,
                bgcolor: '#eef0f4',
                '& .MuiLinearProgress-bar': { bgcolor: d.color, borderRadius: 5 },
              }}
            />
          </Box>
        ))}
      </Box>
    </Box>
  )
}
