import { useState } from 'react'
import { keepPreviousData, useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  Alert,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  IconButton,
  MenuItem,
  Paper,
  Skeleton,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
  TextField,
  Tooltip,
  Typography,
} from '@mui/material'
import AddIcon from '@mui/icons-material/Add'
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutlined'
import DownloadIcon from '@mui/icons-material/Download'
import EditIcon from '@mui/icons-material/Edit'
import {
  createEmployee,
  deactivateEmployee,
  fetchByCountry,
  fetchByDepartment,
  fetchEmployees,
  updateEmployee,
  updateSalary,
} from '../api/client'
import { useDebounce } from '../hooks/useDebounce'
import { formatCurrency, formatNumber, formatUsd } from '../utils/format'
import { downloadCsv, toCsv } from '../utils/csv'
import AddEmployeeDialog from '../components/AddEmployeeDialog'
import EditEmployeeDialog from '../components/EditEmployeeDialog'

const COLUMNS = [
  { label: 'Name', sx: { width: 190, minWidth: 190 } },
  { label: 'Email' },
  { label: 'Country' },
  { label: 'Department' },
  { label: 'Job title' },
]

// Ties the country dot colours to the dashboard donut palette.
const COUNTRY_COLOR = {
  'United States': '#4f46e5',
  'United Kingdom': '#14b8a6',
  Germany: '#f59e0b',
  Australia: '#ec4899',
  Japan: '#8b5cf6',
  India: '#0ea5e9',
}

export default function EmployeesPage() {
  const [page, setPage] = useState(0)
  const [rowsPerPage, setRowsPerPage] = useState(20)
  const [country, setCountry] = useState('')
  const [department, setDepartment] = useState('')
  const [search, setSearch] = useState('')
  const debouncedSearch = useDebounce(search, 300)

  const countryOptions = useQuery({ queryKey: ['byCountry'], queryFn: fetchByCountry })
  const departmentOptions = useQuery({ queryKey: ['byDepartment'], queryFn: fetchByDepartment })

  const query = useQuery({
    queryKey: ['employees', page, rowsPerPage, country, department, debouncedSearch],
    queryFn: () =>
      fetchEmployees({ page, size: rowsPerPage, country, department, q: debouncedSearch }),
    placeholderData: keepPreviousData,
  })

  const queryClient = useQueryClient()
  const [addOpen, setAddOpen] = useState(false)
  const [editEmployee, setEditEmployee] = useState(null)
  const [deactivateTarget, setDeactivateTarget] = useState(null)
  const [exporting, setExporting] = useState(false)

  const refreshAll = () => queryClient.invalidateQueries()

  const createMutation = useMutation({
    mutationFn: createEmployee,
    onSuccess: () => {
      setAddOpen(false)
      refreshAll()
    },
  })

  const updateMutation = useMutation({
    mutationFn: async ({ id, salaryAmount, currency, ...details }) => {
      await updateEmployee(id, details)
      await updateSalary(id, { amount: salaryAmount, currency })
    },
    onSuccess: () => {
      setEditEmployee(null)
      refreshAll()
    },
  })

  const deactivateMutation = useMutation({
    mutationFn: (id) => deactivateEmployee(id),
    onSuccess: () => {
      setDeactivateTarget(null)
      refreshAll()
    },
  })

  async function exportCsv() {
    setExporting(true)
    try {
      const size = data?.totalElements || 10000
      const all = await fetchEmployees({ page: 0, size, country, department, q: debouncedSearch })
      const csv = toCsv(all.content, [
        { key: 'firstName', label: 'First name' },
        { key: 'lastName', label: 'Last name' },
        { key: 'email', label: 'Email' },
        { key: 'country', label: 'Country' },
        { key: 'department', label: 'Department' },
        { key: 'jobTitle', label: 'Job title' },
        { key: 'salaryAmount', label: 'Salary (local)' },
        { key: 'currency', label: 'Currency' },
        { key: 'salaryUsd', label: 'Salary (USD)' },
      ])
      downloadCsv('employees.csv', csv)
    } finally {
      setExporting(false)
    }
  }

  const createError = createMutation.isError
    ? createMutation.error?.response?.data?.message || 'Could not add employee.'
    : null

  // Any filter change returns to the first page.
  function withFirstPage(setter) {
    return (event) => {
      setter(event.target.value)
      setPage(0)
    }
  }

  const hasFilters = Boolean(country || department || search)
  function clearFilters() {
    setCountry('')
    setDepartment('')
    setSearch('')
    setPage(0)
  }

  const data = query.data
  const rows = data?.content ?? []

  return (
    <>
      <Stack
        className="reveal"
        direction={{ xs: 'column', sm: 'row' }}
        justifyContent="space-between"
        alignItems={{ xs: 'flex-start', sm: 'center' }}
        spacing={2}
        sx={{ mb: 3 }}
      >
        <Box>
          <Typography variant="h4" gutterBottom>
            Employees
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Browse and search all{' '}
            {data?.totalElements != null ? formatNumber(data.totalElements) : '…'} employees —
            salaries shown in local currency and USD.
          </Typography>
        </Box>
        <Stack direction="row" spacing={2.5} alignItems="center">
          <Button
            variant="text"
            disableRipple
            startIcon={<DownloadIcon sx={{ fontSize: 18 }} />}
            onClick={exportCsv}
            disabled={exporting}
            sx={{
              minWidth: 0,
              p: 0,
              color: 'text.secondary',
              '&:hover': { bgcolor: 'transparent', color: 'text.primary' },
            }}
          >
            Export CSV
          </Button>
          <Button
            variant="text"
            disableRipple
            startIcon={<AddIcon sx={{ fontSize: 18 }} />}
            onClick={() => setAddOpen(true)}
            sx={{
              minWidth: 0,
              p: 0,
              color: 'primary.main',
              '&:hover': { bgcolor: 'transparent', color: 'primary.dark' },
            }}
          >
            Add employee
          </Button>
        </Stack>
      </Stack>

      <Paper className="reveal" variant="outlined" sx={{ p: 2, mb: 2, animationDelay: '60ms' }}>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <TextField
            label="Search name or email"
            value={search}
            onChange={withFirstPage(setSearch)}
            size="small"
            sx={{ minWidth: 240 }}
          />
          <TextField
            select
            label="Country"
            value={country}
            onChange={withFirstPage(setCountry)}
            size="small"
            sx={{ minWidth: 180 }}
          >
            <MenuItem value="">All countries</MenuItem>
            {(countryOptions.data ?? []).map((c) => (
              <MenuItem key={c.name} value={c.name}>
                {c.name}
              </MenuItem>
            ))}
          </TextField>
          <TextField
            select
            label="Department"
            value={department}
            onChange={withFirstPage(setDepartment)}
            size="small"
            sx={{ minWidth: 200 }}
          >
            <MenuItem value="">All departments</MenuItem>
            {(departmentOptions.data ?? []).map((d) => (
              <MenuItem key={d.name} value={d.name}>
                {d.name}
              </MenuItem>
            ))}
          </TextField>
          <Button onClick={clearFilters} disabled={!hasFilters} sx={{ alignSelf: 'center' }}>
            Clear filters
          </Button>
        </Stack>
      </Paper>

      <Paper className="reveal" variant="outlined" sx={{ animationDelay: '120ms' }}>
        {query.isError ? (
          <Alert severity="error" sx={{ m: 2 }}>
            Could not load employees.
          </Alert>
        ) : (
          <>
            <TableContainer sx={{ maxHeight: 640 }}>
              <Table stickyHeader>
                <TableHead>
                  <TableRow>
                    {COLUMNS.map((c) => (
                      <TableCell key={c.label} sx={c.sx}>
                        {c.label}
                      </TableCell>
                    ))}
                    <TableCell align="right" sx={{ width: 150, minWidth: 150 }}>
                      Salary (local)
                    </TableCell>
                    <TableCell align="right" sx={{ width: 140, minWidth: 140 }}>
                      Salary (USD)
                    </TableCell>
                    <TableCell align="right" sx={{ width: 96 }} />
                  </TableRow>
                </TableHead>
                <TableBody>
                  {query.isLoading ? (
                    Array.from({ length: 8 }).map((_, i) => (
                      <TableRow key={i}>
                        {Array.from({ length: 8 }).map((_, j) => (
                          <TableCell key={j}>
                            <Skeleton />
                          </TableCell>
                        ))}
                      </TableRow>
                    ))
                  ) : rows.length === 0 ? (
                    <TableRow>
                      <TableCell colSpan={8} align="center" sx={{ py: 6 }}>
                        <Box color="text.secondary">No employees match your filters.</Box>
                      </TableCell>
                    </TableRow>
                  ) : (
                    rows.map((e) => (
                      <TableRow key={e.id} hover>
                        <TableCell sx={{ fontWeight: 600 }}>{`${e.firstName} ${e.lastName}`}</TableCell>
                        <TableCell sx={{ color: 'text.secondary' }}>{e.email}</TableCell>
                        <TableCell>
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <Box
                              sx={{
                                width: 6,
                                height: 6,
                                borderRadius: '50%',
                                flexShrink: 0,
                                bgcolor: COUNTRY_COLOR[e.country] ?? 'grey.400',
                              }}
                            />
                            <span>{e.country}</span>
                          </Box>
                        </TableCell>
                        <TableCell>{e.department}</TableCell>
                        <TableCell>{e.jobTitle}</TableCell>
                        <TableCell align="right" sx={{ fontVariantNumeric: 'tabular-nums', color: 'text.secondary' }}>
                          {formatCurrency(e.salaryAmount, e.currency)}
                        </TableCell>
                        <TableCell align="right" sx={{ fontVariantNumeric: 'tabular-nums', fontWeight: 700 }}>
                          {formatUsd(e.salaryUsd)}
                        </TableCell>
                        <TableCell align="right">
                          <Tooltip title="Edit">
                            <IconButton size="small" onClick={() => setEditEmployee(e)}>
                              <EditIcon fontSize="small" />
                            </IconButton>
                          </Tooltip>
                          <Tooltip title="Deactivate">
                            <IconButton size="small" onClick={() => setDeactivateTarget(e)}>
                              <DeleteOutlineIcon fontSize="small" />
                            </IconButton>
                          </Tooltip>
                        </TableCell>
                      </TableRow>
                    ))
                  )}
                </TableBody>
              </Table>
            </TableContainer>
            <TablePagination
              component="div"
              count={data?.totalElements ?? 0}
              page={page}
              onPageChange={(_event, newPage) => setPage(newPage)}
              rowsPerPage={rowsPerPage}
              onRowsPerPageChange={(event) => {
                setRowsPerPage(parseInt(event.target.value, 10))
                setPage(0)
              }}
              rowsPerPageOptions={[10, 20, 50]}
            />
          </>
        )}
      </Paper>

      <AddEmployeeDialog
        open={addOpen}
        countries={(countryOptions.data ?? []).map((c) => c.name)}
        departments={(departmentOptions.data ?? []).map((d) => d.name)}
        onClose={() => {
          setAddOpen(false)
          createMutation.reset()
        }}
        onSave={(payload) => createMutation.mutate(payload)}
        saving={createMutation.isPending}
        error={createError}
      />
      <EditEmployeeDialog
        employee={editEmployee}
        countries={(countryOptions.data ?? []).map((c) => c.name)}
        departments={(departmentOptions.data ?? []).map((d) => d.name)}
        onClose={() => {
          setEditEmployee(null)
          updateMutation.reset()
        }}
        onSave={(payload) => updateMutation.mutate({ id: editEmployee.id, ...payload })}
        saving={updateMutation.isPending}
        error={
          updateMutation.isError
            ? updateMutation.error?.response?.data?.message || 'Could not save changes.'
            : null
        }
      />

      <Dialog
        open={Boolean(deactivateTarget)}
        onClose={() => setDeactivateTarget(null)}
        maxWidth="xs"
        fullWidth
      >
        <DialogTitle>Deactivate employee?</DialogTitle>
        <DialogContent>
          <DialogContentText>
            {deactivateTarget ? `${deactivateTarget.firstName} ${deactivateTarget.lastName}` : ''} will
            be hidden from the list and excluded from insights. Their record is kept, not deleted.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeactivateTarget(null)}>Cancel</Button>
          <Button
            color="error"
            variant="contained"
            disabled={deactivateMutation.isPending}
            onClick={() => deactivateMutation.mutate(deactivateTarget.id)}
          >
            Deactivate
          </Button>
        </DialogActions>
      </Dialog>
    </>
  )
}
