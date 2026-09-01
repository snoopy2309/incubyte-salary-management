import { Box, Paper, Typography } from '@mui/material'
import LightbulbOutlinedIcon from '@mui/icons-material/LightbulbOutlined'

/** A titled chart container with an optional plain-English takeaway footer. */
export default function ChartCard({ title, subtitle, note, children }) {
  return (
    <Paper variant="outlined" sx={{ p: 3, height: '100%' }}>
      <Typography variant="h6">{title}</Typography>
      {subtitle && (
        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
          {subtitle}
        </Typography>
      )}
      <Box sx={{ width: '100%', height: 300 }}>{children}</Box>
      {note && (
        <Box
          sx={{
            mt: 2,
            pt: 1.5,
            borderTop: '1px solid',
            borderColor: 'divider',
            display: 'flex',
            gap: 1,
            alignItems: 'flex-start',
          }}
        >
          <LightbulbOutlinedIcon fontSize="small" sx={{ color: 'warning.main', mt: 0.2 }} />
          <Typography variant="body2" color="text.secondary">
            {note}
          </Typography>
        </Box>
      )}
    </Paper>
  )
}
