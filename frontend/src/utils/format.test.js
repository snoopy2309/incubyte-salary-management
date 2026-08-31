import { formatNumber, formatUsd } from './format'

describe('formatUsd', () => {
  it('formats as USD with no fractional digits by default', () => {
    expect(formatUsd(88859.12)).toBe('$88,859')
  })

  it('formats large values compactly when asked', () => {
    expect(formatUsd(888591234, { compact: true })).toBe('$888.6M')
  })

  it('returns a dash for null/undefined', () => {
    expect(formatUsd(null)).toBe('—')
    expect(formatUsd(undefined)).toBe('—')
  })
})

describe('formatNumber', () => {
  it('adds thousands separators', () => {
    expect(formatNumber(10000)).toBe('10,000')
  })

  it('returns a dash for missing values', () => {
    expect(formatNumber(undefined)).toBe('—')
  })
})
