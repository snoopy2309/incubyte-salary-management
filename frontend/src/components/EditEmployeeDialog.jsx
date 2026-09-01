import { useEffect, useState } from 'react'
import {
  Alert,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  MenuItem,
  TextField,
} from '@mui/material'

const CURRENCIES = ['USD', 'INR', 'GBP', 'EUR', 'JPY', 'AUD']
const SHRINK = { inputLabel: { shrink: true } }

/** Edit an existing employee's details and salary. */
export default function EditEmployeeDialog({ employee, countries, departments, onClose, onSave, saving, error }) {
  const [form, setForm] = useState(null)
  const set = (key) => (e) => setForm((f) => ({ ...f, [key]: e.target.value }))

  useEffect(() => {
    if (employee) {
      setForm({
        firstName: employee.firstName,
        lastName: employee.lastName,
        email: employee.email,
        country: employee.country,
        department: employee.department,
        jobTitle: employee.jobTitle,
        salaryAmount: String(employee.salaryAmount ?? ''),
        currency: employee.currency ?? 'USD',
      })
    }
  }, [employee])

  const open = Boolean(employee) && Boolean(form)
  if (!open) return null

  const emailValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email.trim())
  const valid =
    form.firstName.trim() && form.lastName.trim() && emailValid && form.country &&
    form.department && form.jobTitle.trim() && Number(form.salaryAmount) > 0

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Edit employee — {`${employee.firstName} ${employee.lastName}`}</DialogTitle>
      <DialogContent>
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2.5, mt: 2 }}>
          <TextField label="First name" value={form.firstName} onChange={set('firstName')} slotProps={SHRINK} />
          <TextField label="Last name" value={form.lastName} onChange={set('lastName')} slotProps={SHRINK} />
          <TextField
            label="Email"
            type="email"
            value={form.email}
            onChange={set('email')}
            error={Boolean(form.email) && !emailValid}
            helperText={Boolean(form.email) && !emailValid ? 'Enter a valid email address' : undefined}
            slotProps={SHRINK}
            sx={{ gridColumn: '1 / -1' }}
          />
          <TextField select label="Country" value={form.country} onChange={set('country')} slotProps={SHRINK}>
            {countries.map((c) => (
              <MenuItem key={c} value={c}>{c}</MenuItem>
            ))}
          </TextField>
          <TextField select label="Department" value={form.department} onChange={set('department')} slotProps={SHRINK}>
            {departments.map((d) => (
              <MenuItem key={d} value={d}>{d}</MenuItem>
            ))}
          </TextField>
          <TextField label="Job title" value={form.jobTitle} onChange={set('jobTitle')} slotProps={SHRINK} />
          <TextField label="Salary amount" type="number" value={form.salaryAmount} onChange={set('salaryAmount')} slotProps={SHRINK} />
          <TextField select label="Currency" value={form.currency} onChange={set('currency')} slotProps={SHRINK}>
            {CURRENCIES.map((c) => (
              <MenuItem key={c} value={c}>{c}</MenuItem>
            ))}
          </TextField>
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button
          variant="contained"
          disabled={saving || !valid}
          onClick={() => onSave({ ...form, salaryAmount: Number(form.salaryAmount) })}
        >
          Save changes
        </Button>
      </DialogActions>
    </Dialog>
  )
}
