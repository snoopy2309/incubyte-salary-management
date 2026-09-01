import {
  Bar,
  BarChart,
  CartesianGrid,
  Legend,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import { CHART } from '../theme'
import { formatUsd } from '../utils/format'

/**
 * Grouped bars comparing Average and Median (both USD) across groups
 * (countries or departments). Two series on one shared axis — never dual-axis.
 * Animation is disabled: it conflicts with React StrictMode and is unnecessary.
 */
export default function GroupBarChart({ data }) {
  const chartData = data.map((d) => ({
    name: d.name,
    Average: d.averageUsd,
    Typical: d.medianUsd,
  }))

  return (
    <ResponsiveContainer width="100%" height="100%">
      <BarChart data={chartData} margin={{ top: 8, right: 8, bottom: 8, left: 8 }} barGap={2}>
        <CartesianGrid stroke={CHART.grid} strokeDasharray="3 3" vertical={false} />
        <XAxis
          dataKey="name"
          interval={0}
          angle={-25}
          textAnchor="end"
          height={64}
          tick={{ fontSize: 11, fill: CHART.axis }}
          tickLine={false}
          axisLine={{ stroke: CHART.grid }}
        />
        <YAxis
          width={56}
          tick={{ fontSize: 12, fill: CHART.axis }}
          tickLine={false}
          axisLine={false}
          tickFormatter={(v) => formatUsd(v, { compact: true })}
        />
        <Tooltip formatter={(value) => formatUsd(value)} cursor={{ fill: 'rgba(0,0,0,0.04)' }} />
        <Legend />
        <Bar dataKey="Average" fill={CHART.average} radius={[4, 4, 0, 0]} isAnimationActive={false} />
        <Bar dataKey="Typical" fill={CHART.median} radius={[4, 4, 0, 0]} isAnimationActive={false} />
      </BarChart>
    </ResponsiveContainer>
  )
}
