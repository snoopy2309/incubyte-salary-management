function escapeCell(value) {
  const s = value === null || value === undefined ? '' : String(value)
  return /[",\n]/.test(s) ? `"${s.replace(/"/g, '""')}"` : s
}

/** Build a CSV string from rows using [{ key, label }] column definitions. */
export function toCsv(rows, columns) {
  const header = columns.map((c) => escapeCell(c.label)).join(',')
  const body = rows.map((r) => columns.map((c) => escapeCell(r[c.key])).join(',')).join('\n')
  return `${header}\n${body}`
}

/** Trigger a browser download of the given CSV text. */
export function downloadCsv(filename, csv) {
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  link.remove()
  URL.revokeObjectURL(url)
}
