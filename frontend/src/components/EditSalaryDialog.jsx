import { useEffect, useState } from 'react'
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  MenuItem,
  Stack,
  TextField,
} from '@mui/material'

const CURRENCIES = ['USD', 'INR', 'GBP', 'EUR', 'JPY', 'AUD']

/** Dialog to change one employee's salary amount and currency. */
export default function EditSalaryDialog({ employee, onClose, onSave, saving }) {
  const [amount, setAmount] = useState('')
  const [currency, setCurrency] = useState('USD')

  useEffect(() => {
    if (employee) {
      setAmount(String(employee.salaryAmount ?? ''))
      setCurrency(employee.currency ?? 'USD')
    }
  }, [employee])

  const open = Boolean(employee)

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="xs">
      <DialogTitle>
        Edit salary{employee ? ` — ${employee.firstName} ${employee.lastName}` : ''}
      </DialogTitle>
      <DialogContent>
        <Stack spacing={2} sx={{ mt: 1 }}>
          <TextField
            label="Amount"
            type="number"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            fullWidth
            autoFocus
          />
          <TextField
            select
            label="Currency"
            value={currency}
            onChange={(e) => setCurrency(e.target.value)}
            fullWidth
          >
            {CURRENCIES.map((c) => (
              <MenuItem key={c} value={c}>
                {c}
              </MenuItem>
            ))}
          </TextField>
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button
          variant="contained"
          disabled={saving || !amount || Number(amount) <= 0}
          onClick={() => onSave({ amount: Number(amount), currency })}
        >
          Save
        </Button>
      </DialogActions>
    </Dialog>
  )
}
