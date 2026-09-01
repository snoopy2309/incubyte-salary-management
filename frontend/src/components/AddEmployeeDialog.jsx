import { useState } from 'react'
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

// Keep every label floated above its field, so the form reads cleanly.
const SHRINK = { inputLabel: { shrink: true } }

const EMPTY = {
  firstName: '',
  lastName: '',
  email: '',
  country: '',
  department: '',
  jobTitle: '',
  joinDate: '',
  salaryAmount: '',
  currency: 'USD',
}

/** Dialog to add a new employee together with their salary. */
export default function AddEmployeeDialog({ open, countries, departments, onClose, onSave, saving, error }) {
  const [form, setForm] = useState(EMPTY)
  const set = (key) => (e) => setForm((f) => ({ ...f, [key]: e.target.value }))

  const emailEntered = form.email.trim().length > 0
  const emailValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email.trim())

  const valid =
    form.firstName.trim() && form.lastName.trim() && emailValid && form.country &&
    form.department && form.jobTitle.trim() && form.joinDate && Number(form.salaryAmount) > 0

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Add employee</DialogTitle>
      <DialogContent>
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2.5, mt: 2 }}>
          <TextField
            label="First name"
            placeholder="e.g. Ada"
            value={form.firstName}
            onChange={set('firstName')}
            slotProps={SHRINK}
          />
          <TextField
            label="Last name"
            placeholder="e.g. Lovelace"
            value={form.lastName}
            onChange={set('lastName')}
            slotProps={SHRINK}
          />
          <TextField
            label="Email"
            type="email"
            placeholder="name@acme.com"
            value={form.email}
            onChange={set('email')}
            error={emailEntered && !emailValid}
            helperText={emailEntered && !emailValid ? 'Enter a valid email address' : undefined}
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
          <TextField
            label="Job title"
            placeholder="e.g. Manager"
            value={form.jobTitle}
            onChange={set('jobTitle')}
            slotProps={SHRINK}
          />
          <TextField
            label="Join date"
            type="date"
            value={form.joinDate}
            onChange={set('joinDate')}
            slotProps={SHRINK}
          />
          <TextField
            label="Salary amount"
            type="number"
            placeholder="e.g. 120000"
            value={form.salaryAmount}
            onChange={set('salaryAmount')}
            slotProps={SHRINK}
          />
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
          Add employee
        </Button>
      </DialogActions>
    </Dialog>
  )
}
