// Formatting helpers for money and counts, using the browser's Intl API.

export function formatUsd(value, { compact = false } = {}) {
  if (value === null || value === undefined) return '—'
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    notation: compact ? 'compact' : 'standard',
    maximumFractionDigits: compact ? 1 : 0,
  }).format(value)
}

export function formatNumber(value) {
  if (value === null || value === undefined) return '—'
  return new Intl.NumberFormat('en-US').format(value)
}

// Format an amount in its own local currency (e.g. ₹, ¥, €).
export function formatCurrency(value, currency) {
  if (value === null || value === undefined) return '—'
  try {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency,
      maximumFractionDigits: 0,
    }).format(value)
  } catch {
    return `${formatNumber(value)} ${currency}`
  }
}
